package quickfix.examples.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import quickfix.ConfigError;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilder;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilderFactory;
import quickfix.examples.utility.DefaultMessageSender;
import quickfix.examples.utility.FixApplicationAdapter;
import quickfix.examples.utility.IdGenerator;
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

public class Application extends FixApplicationAdapter {
  private final static Logger logger = LoggerFactory.getLogger(Application.class);

  private static final String DEFAULT_MARKET_PRICE_KEY = "DefaultMarketPrice";
  private static final String ALWAYS_FILL_LIMIT_KEY = "AlwaysFillLimitOrders";
  private static final String VALID_ORDER_TYPES_KEY = "ValidOrderTypes";

  private final boolean alwaysFillLimitOrders;
  private final HashSet<String> validOrderTypes = new HashSet<String>();
  private MarketDataProvider marketDataProvider;
  private ExecutionReportBuilderFactory builderFactory = new ExecutionReportBuilderFactory();
  private MessageSender messageSender;
  private IdGenerator idGenerator = new IdGenerator();

  public Application(SessionSettings settings) throws ConfigError,
          FieldConvertError {
    this(settings, new DefaultMessageSender());
  }

  public Application(SessionSettings settings, MessageSender messageSender)
          throws ConfigError, FieldConvertError {
    String validOrderTypesStr = null;
    if (settings.isSetting(VALID_ORDER_TYPES_KEY)) {
      validOrderTypesStr = settings.getString(VALID_ORDER_TYPES_KEY)
              .trim();
    }

    double defaultMarketPrice = 0.0;
    if (settings.isSetting(DEFAULT_MARKET_PRICE_KEY)) {
      defaultMarketPrice = settings.getDouble(DEFAULT_MARKET_PRICE_KEY);
    }

    if (settings.isSetting(ALWAYS_FILL_LIMIT_KEY)) {
      this.alwaysFillLimitOrders = settings
              .getBool(ALWAYS_FILL_LIMIT_KEY);
    } else {
      this.alwaysFillLimitOrders = false;
    }
    initializeValidOrderTypes(validOrderTypesStr);
    initializeMarketDataProvider(defaultMarketPrice);
    this.messageSender = messageSender;
  }

  public Application(boolean alwaysFillLimitOrders,
                     String validOrderTypesStr, double defaultMarketPrice,
                     MessageSender messageSender) throws ConfigError, FieldConvertError {
    initializeValidOrderTypes(validOrderTypesStr);
    initializeMarketDataProvider(defaultMarketPrice);
    this.alwaysFillLimitOrders = alwaysFillLimitOrders;
    this.messageSender = messageSender;
  }

  private void initializeMarketDataProvider(final double defaultMarketPrice)
          throws ConfigError, FieldConvertError {
    if (defaultMarketPrice > 0.0) {
      if (marketDataProvider == null) {
        marketDataProvider = new MarketDataProvider() {
          public double getAsk(String symbol) {
            return defaultMarketPrice;
          }

          public double getBid(String symbol) {
            return defaultMarketPrice;
          }
        };
      } else {
        logger.warn("Ignoring " + DEFAULT_MARKET_PRICE_KEY
                + " since provider is already defined.");
      }
    }
  }

  private void initializeValidOrderTypes(String validOrderTypesStr)
          throws ConfigError, FieldConvertError {
    if (validOrderTypesStr != null) {
      List<String> orderTypes = Arrays.asList(validOrderTypesStr
              .split("\\s*,\\s*"));
      validOrderTypes.addAll(orderTypes);
    } else {
      validOrderTypes.add(OrdType.LIMIT + "");
    }
  }

  public void onCreate(SessionID sessionID) {
    Session.lookupSession(sessionID).getLog()
            .onEvent("Valid order types: " + validOrderTypes);
  }

  @Override
  protected void onNewOrder(Message order, SessionID sessionID) throws FieldNotFound {
    ExecutionReportBuilder builder = builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processNewOrder(order, sessionID, builder);
    } catch (Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      Message reject = builder.orderRejected(order, genOrderID(), genExecID(), ex.getMessage());
      sendMessage(reject, sessionID);
    }
  }

  private void processNewOrder(Message order, SessionID sessionID, ExecutionReportBuilder builder) throws FieldNotFound {
    validateNewOrder(order);

    Message accept = builder.orderAcked(order, genOrderID(), genExecID());
    sendMessage(accept, sessionID);

    double price = getPrice(order);

    if (isOrderExecutable(order, price)) {
      double orderQty = order.getDouble(OrderQty.FIELD);
      double fillQty = orderQty / 2;
      Message fill = builder.fillOrder(order, accept.getString(OrderID.FIELD),
              genExecID(), OrdStatus.FILLED,
              fillQty, price, fillQty, price);

      sendMessage(fill, sessionID);
    }
  }

  private void validateNewOrder(Message order) throws FieldNotFound {
    OrdType ordType = new OrdType(order.getChar(OrdType.FIELD));
    checkArgument(validOrderTypes.contains(Character.toString(ordType.getValue())), "Order type not in supported");

    if (ordType.getValue() == OrdType.MARKET) {
      checkNotNull(marketDataProvider, "DefaultMarketPrice setting not specified for market order");
    }

    double orderQty = order.getDouble(OrderQty.FIELD);
    checkArgument(orderQty > 0, "Invalid order qty");

    if (order.isSetField(Price.FIELD)) {
      double price = order.getDouble(Price.FIELD);
      checkArgument(price > 0, "Invalid order qty");
    }
  }

  private double getPrice(Message message) throws FieldNotFound {
    if (message.getChar(OrdType.FIELD) == OrdType.LIMIT
            && alwaysFillLimitOrders) {
      return message.getDouble(Price.FIELD);
    } else {
      checkNotNull(marketDataProvider != null,
              "No market data provider specified for market order");
      char side = message.getChar(Side.FIELD);
      String symbol = message.getString(Symbol.FIELD);
      return quotePrice(side, symbol);
    }
  }

  private double quotePrice(char side, String symbol) throws FieldNotFound {
    if (side == Side.BUY) {
      return marketDataProvider.getAsk(symbol);
    } else if (side == Side.SELL || side == Side.SELL_SHORT) {
      return marketDataProvider.getBid(symbol);
    } else {
      throw new RuntimeException("Invalid order side: " + side);
    }
  }

  private boolean isOrderExecutable(Message order, double price)
          throws FieldNotFound {
    if (order.getChar(OrdType.FIELD) == OrdType.LIMIT) {
      BigDecimal limitPrice = new BigDecimal(order.getString(Price.FIELD));
      char side = order.getChar(Side.FIELD);
      BigDecimal thePrice = new BigDecimal("" + price);

      return (side == Side.BUY && thePrice.compareTo(limitPrice) <= 0)
              || ((side == Side.SELL || side == Side.SELL_SHORT) && thePrice
              .compareTo(limitPrice) >= 0);
    }
    return true;
  }

  @Override
  protected void onCanceleOrder(Message order, SessionID sessionID) throws FieldNotFound {
    ExecutionReportBuilder builder = builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processCancelOrder(order, sessionID, builder);
    } catch (Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      Message reject = builder.cancelRejected(order, order.getString(OrderID.FIELD), '0',
              0, 0, 2);
      sendMessage(reject, sessionID);
    }
  }

  private void processCancelOrder(Message order, SessionID sessionID, ExecutionReportBuilder builder) throws FieldNotFound {
    validCancelOrder(order);

    Message canceled = builder.orderCanceled(order, order.getString(OrderID.FIELD), genExecID(),
            0, 0.0);
    sendMessage(canceled, sessionID);
  }

  private void validCancelOrder(Message order) throws FieldNotFound {
    // TODO
  }

  @Override
  protected void onReplaceOrder(Message order, SessionID sessionID) throws FieldNotFound {
    ExecutionReportBuilder builder = builderFactory
            .getExecutionReportBuilder(sessionID.getBeginString());
    checkNotNull(String.format("%s not supported", sessionID.getBeginString()), builder);

    try {
      processReplaceOrder(order, sessionID, builder);
    } catch (Exception ex) {
      logger.error(String.format("Failed to process %s", order), ex);
      Message reject = builder.cancelRejected(order, order.getString(OrderID.FIELD), '0',
              0, 0, 2);
      sendMessage(reject, sessionID);
    }
  }

  private void processReplaceOrder(Message order, SessionID sessionID, ExecutionReportBuilder builder) throws FieldNotFound {
    validReplaceOrder(order);

    Message replaced = builder.orderReplaced(order, order.getString(OrderID.FIELD), genExecID(),
            0, 0.0);
    sendMessage(replaced, sessionID);
  }

  private void validReplaceOrder(Message order) throws FieldNotFound {
    // TODO
  }

  @Override
  protected void onCancelReject(Message message, SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void onExecutionReport(Message message, SessionID sessionId) {
    throw new UnsupportedOperationException();
  }

  private String genExecID() {
    return idGenerator.genExecID();
  }

  private String genOrderID() {
    return idGenerator.genOrderID();
  }

  private void sendMessage(Message reject, SessionID sessionID) {
    this.messageSender.sendMessage(reject, sessionID);
  }
}
