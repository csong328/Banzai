package quickfix.examples.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.PostConstruct;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilder;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilderFactory;
import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.IdGeneratorFactory;
import quickfix.examples.utility.MessageSender;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ExchangeOMSImpl implements ExchangeOMS {
  private final static Logger logger = LoggerFactory.getLogger(Application.class);

  @Autowired
  private ExecutionReportBuilderFactory builderFactory;
  @Autowired
  private MessageSender messageSender;
  @Autowired
  private IdGeneratorFactory idGeneratorFactory;
  @Autowired
  private MarketDataProvider marketDataProvider;

  private Set<String> validOrderTypes;
  private boolean alwaysFillLimitOrders;
  private IdGenerator orderIdGenerator;
  private IdGenerator execIdGenerator;

  @PostConstruct
  public void init() {
    this.orderIdGenerator = this.idGeneratorFactory.idGenerator();
    this.execIdGenerator = this.idGeneratorFactory.idGenerator();
  }

  public void setValidOrderTypes(final Set<String> validOrderTypes) {
    this.validOrderTypes = validOrderTypes;
  }

  public void setAlwaysFillLimitOrders(final boolean alwaysFillLimitOrders) {
    this.alwaysFillLimitOrders = alwaysFillLimitOrders;
  }

  @Override
  public void onNewOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processNewOrder(order, sessionID, builder);
    } catch (final Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      final Message reject = builder.orderRejected(order, genOrderID(), genExecID(), ex.getMessage());
      sendMessage(reject, sessionID);
    }
  }

  @Override
  public void onCanceleOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processCancelOrder(order, sessionID, builder);
    } catch (final Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      final Message reject = builder.cancelRejected(order, order.getString(OrderID.FIELD), '0',
              0, 0, 2);
      sendMessage(reject, sessionID);
    }
  }

  @Override
  public void onReplaceOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    final ExecutionReportBuilder builder = this.builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processReplaceOrder(order, sessionID, builder);
    } catch (final Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      final Message reject = builder.cancelRejected(order, order.getString(OrderID.FIELD), '0',
              0, 0, 2);
      sendMessage(reject, sessionID);
    }
  }

  private void processNewOrder(final Message order, final SessionID sessionID, final ExecutionReportBuilder builder) throws FieldNotFound {
    validateNewOrder(order);

    final Message accept = builder.orderAcked(order, genOrderID(), genExecID());
    sendMessage(accept, sessionID);

    final double price = getPrice(order);

    if (isOrderExecutable(order, price)) {
      final double orderQty = order.getDouble(OrderQty.FIELD);
      final double fillQty = orderQty / 2;
      final Message fill = builder.fillOrder(order, accept.getString(OrderID.FIELD),
              genExecID(), OrdStatus.FILLED,
              fillQty, price, fillQty, price);

      sendMessage(fill, sessionID);
    }
  }

  private void validateNewOrder(final Message order) throws FieldNotFound {
    final OrdType ordType = new OrdType(order.getChar(OrdType.FIELD));
    checkArgument(this.validOrderTypes.contains(Character.toString(ordType.getValue())), "Order type not in supported");

    if (ordType.getValue() == OrdType.MARKET) {
      checkNotNull(this.marketDataProvider, "DefaultMarketPrice setting not specified for market order");
    }

    final double orderQty = order.getDouble(OrderQty.FIELD);
    checkArgument(orderQty > 0, "Invalid order qty");

    if (order.isSetField(Price.FIELD)) {
      final double price = order.getDouble(Price.FIELD);
      checkArgument(price > 0, "Invalid order qty");
    }
  }

  private double getPrice(final Message message) throws FieldNotFound {
    if (message.getChar(OrdType.FIELD) == OrdType.LIMIT
            && this.alwaysFillLimitOrders) {
      return message.getDouble(Price.FIELD);
    } else {
      checkNotNull(this.marketDataProvider != null,
              "No market data provider specified for market order");
      final char side = message.getChar(Side.FIELD);
      final String symbol = message.getString(Symbol.FIELD);
      return quotePrice(side, symbol);
    }
  }

  private double quotePrice(final char side, final String symbol) throws FieldNotFound {
    if (side == Side.BUY) {
      return this.marketDataProvider.getAsk(symbol);
    } else if (side == Side.SELL || side == Side.SELL_SHORT) {
      return this.marketDataProvider.getBid(symbol);
    } else {
      throw new RuntimeException("Invalid order side: " + side);
    }
  }

  private boolean isOrderExecutable(final Message order, final double price)
          throws FieldNotFound {
    if (order.getChar(OrdType.FIELD) == OrdType.LIMIT) {
      final BigDecimal limitPrice = new BigDecimal(order.getString(Price.FIELD));
      final char side = order.getChar(Side.FIELD);
      final BigDecimal thePrice = new BigDecimal("" + price);

      return (side == Side.BUY && thePrice.compareTo(limitPrice) <= 0)
              || ((side == Side.SELL || side == Side.SELL_SHORT) && thePrice
              .compareTo(limitPrice) >= 0);
    }
    return true;
  }

  private void processCancelOrder(final Message order, final SessionID sessionID, final ExecutionReportBuilder builder) throws FieldNotFound {
    validCancelOrder(order);

    final Message canceled = builder.orderCanceled(order, order.getString(OrderID.FIELD), genExecID(),
            0, 0.0);
    sendMessage(canceled, sessionID);
  }

  private void validCancelOrder(final Message order) throws FieldNotFound {
    // TODO
  }

  private void processReplaceOrder(final Message order, final SessionID sessionID, final ExecutionReportBuilder builder) throws FieldNotFound {
    validReplaceOrder(order);

    final Message replaced = builder.orderReplaced(order, order.getString(OrderID.FIELD), genExecID(),
            0, 0.0);
    sendMessage(replaced, sessionID);
  }

  private void validReplaceOrder(final Message order) throws FieldNotFound {
    // TODO
  }


  @Override
  public void onCancelReject(final Message message, final SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void onExecutionReport(final Message message, final SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  private String genExecID() {
    return this.orderIdGenerator.nextID();
  }

  private String genOrderID() {
    return this.orderIdGenerator.nextID();
  }

  private void sendMessage(final Message reject, final SessionID sessionID) {
    this.messageSender.sendMessage(reject, sessionID);
  }
}
