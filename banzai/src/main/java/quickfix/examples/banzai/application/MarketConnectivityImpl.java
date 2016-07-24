package quickfix.examples.banzai.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.ExecutionImpl;
import quickfix.examples.banzai.LogonEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.fix.FixMessageBuilder;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventType;
import quickfix.examples.banzai.ui.event.SimpleOrderEventSource;
import quickfix.examples.utility.MessageSender;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

import static com.google.common.base.Preconditions.checkNotNull;
import static quickfix.examples.banzai.TypeMapping.FIXSideToSide;

@Component("marketConnectivity")
public class MarketConnectivityImpl extends SimpleOrderEventSource implements IMarketConnectivity {
  @Autowired
  private FixMessageBuilderFactory fixMessageBuilderFactory;
  @Autowired
  private MessageSender messageSender;

  private final ObservableLogon observableLogon = new ObservableLogon();
  private final HashMap<SessionID, Set<ExecID>> execIDs = new HashMap<>();
  private final Map<String, Order> orderMap = new HashMap<>();

  @Override
  public void onLogon(final SessionID sessionID) {
    this.observableLogon.logon(sessionID);
  }

  @Override
  public void onLogout(final SessionID sessionID) {
    this.observableLogon.logoff(sessionID);
  }

  @Override
  public void addLogonObserver(final Observer observer) {
    this.observableLogon.addObserver(observer);
  }

  @Override
  public void deleteLogonObserver(final Observer observer) {
    this.observableLogon.deleteObserver(observer);
  }

  @Override
  public void send(final Order order) {
    addClOrdID(order);

    final Message message = getFixMessageBuilder(order).newOrder(order);
    send(message, order.getSessionID());
  }

  @Override
  public void cancel(final Order order) {
    addClOrdID(order);

    final Message message = getFixMessageBuilder(order).cancel(order);
    send(message, order.getSessionID());
  }

  @Override
  public void replace(final Order order, final Order newOrder) {
    addClOrdID(newOrder);

    final Message message = getFixMessageBuilder(order).replace(order, newOrder);
    send(message, order.getSessionID());
  }

  private FixMessageBuilder getFixMessageBuilder(final Order order) {
    checkNotNull(order.getSessionID(), "SessionID not set for order %s", order.getID());
    final String beginString = order.getSessionID().getBeginString();
    final FixMessageBuilder builder = getFixMessageBuilder(beginString);
    checkNotNull(builder, "%s not supported", beginString);
    return builder;
  }

  private void send(final quickfix.Message message, final SessionID sessionID) {
    this.messageSender.sendMessage(message, sessionID);
  }

  @Override
  public void executionReport(final Message message, final SessionID sessionID) throws FieldNotFound {
    final ExecID execID = (ExecID) message.getField(new ExecID());
    if (alreadyProcessed(execID, sessionID)) return;

    final Order order = getOrder(message.getField(new ClOrdID()).getValue());
    if (order == null) {
      return;
    }

    BigDecimal fillSize = BigDecimal.ZERO;

    if (message.isSetField(LastShares.FIELD)) {
      final LastShares lastShares = new LastShares();
      message.getField(lastShares);
      fillSize = new BigDecimal("" + lastShares.getValue());
    } else {
      // > FIX 4.1
//            LeavesQty leavesQty = new LeavesQty();
//            message.getField(leavesQty);
//            fillSize = new BigDecimal(order.getQuantity())
//                    .subtract(new BigDecimal("" + leavesQty.getValue()));
    }

    if (fillSize.compareTo(BigDecimal.ZERO) > 0) {
      order.setOpen(order.getOpen() - (int) Double.parseDouble(fillSize.toPlainString()));
      order.setExecuted(Integer.parseInt(message.getString(CumQty.FIELD)));
      order.setAvgPx(Double.parseDouble(message.getString(AvgPx.FIELD)));
    }

    final OrdStatus ordStatus = (OrdStatus) message.getField(new OrdStatus());

    if (ordStatus.valueEquals(OrdStatus.REJECTED)) {
      order.setRejected(true);
      order.setOpen(0);

    } else if (ordStatus.valueEquals(OrdStatus.CANCELED)) {
      final Order origOrder = getOrder(message.getField(new OrigClOrdID()).getValue());
      if (origOrder != null) {
        order.setExecuted(origOrder.getExecuted());
        order.setAvgPx(origOrder.getAvgPx());
      }
      order.setCanceled(true);
      order.setOpen(0);
      notify(new OrderEvent(order, OrderEventType.OrderReplaced));

    } else if (ordStatus.valueEquals(OrdStatus.REPLACED)) {
      final Order origOrder = getOrder(message.getField(new OrigClOrdID()).getValue());
      if (origOrder != null) {
        order.setExecuted(origOrder.getExecuted());
        order.setAvgPx(origOrder.getAvgPx());
      }
      order.setCanceled(true);
      order.setOpen(order.getQuantity() - order.getExecuted());
      notify(new OrderEvent(order, OrderEventType.OrderReplaced));

    } else if (ordStatus.valueEquals(OrdStatus.DONE_FOR_DAY)) {
      order.setCanceled(true);
      order.setOpen(0);

    } else if (ordStatus.valueEquals(OrdStatus.NEW)) {
      if (order.isNew()) {
        order.setNew(false);
      }
    }

    if (message.isSetField(OrderID.FIELD)) {
      order.setOrderID(message.getString(OrderID.FIELD));
    }

    try {
      order.setMessage(message.getField(new Text()).getValue());
    } catch (final FieldNotFound e) {
    }

    if (fillSize.compareTo(BigDecimal.ZERO) > 0) {
      final Execution execution = new ExecutionImpl();
      execution.setExchangeID(sessionID + message.getField(new ExecID()).getValue());

      execution.setSymbol(message.getField(new Symbol()).getValue());
      execution.setQuantity(fillSize.intValue());
      if (message.isSetField(LastPx.FIELD)) {
        execution.setPrice(Double.parseDouble(message.getString(LastPx.FIELD)));
      }
      final Side side = (Side) message.getField(new Side());
      execution.setSide(FIXSideToSide(side));
      notify(new OrderEvent(order, OrderEventType.Fill, execution));
    }
  }

  @Override
  public void cancelReject(final Message message, final SessionID sessionID) throws FieldNotFound {
    final String id = message.getField(new ClOrdID()).getValue();
    Order order = getOrder(id);
    if (order == null) return;
    if (order.getOriginalID() != null) order = getOrder(order.getOriginalID());

    try {
      order.setMessage(message.getField(new Text()).getValue());
    } catch (final FieldNotFound e) {
    }
  }

  private Order getOrder(final String ID) throws FieldNotFound {
    return this.orderMap.get(ID);
  }

  private void addClOrdID(final Order order) {
    this.orderMap.put(order.getID(), order);
  }

  private boolean alreadyProcessed(final ExecID execID, final SessionID sessionID) {
    Set<ExecID> set = this.execIDs.get(sessionID);
    if (set == null) {
      set = new HashSet<>();
      set.add(execID);
      this.execIDs.put(sessionID, set);
      return false;
    } else {
      if (set.contains(execID)) return true;
      set.add(execID);
      return false;
    }
  }

  private FixMessageBuilder getFixMessageBuilder(final String beginString) {
    return this.fixMessageBuilderFactory.getFixMessageBuilder(beginString);
  }

  private static class ObservableLogon extends Observable {
    private final HashSet<SessionID> set = new HashSet<>();

    public void logon(final SessionID sessionID) {
      this.set.add(sessionID);
      setChanged();
      notifyObservers(new LogonEvent(sessionID, true));
      clearChanged();
    }

    public void logoff(final SessionID sessionID) {
      this.set.remove(sessionID);
      setChanged();
      notifyObservers(new LogonEvent(sessionID, false));
      clearChanged();
    }
  }
}
