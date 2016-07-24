/*******************************************************************************
 * Copyright (c) quickfixengine.org All rights reserved.
 * <p>
 * This file is part of the QuickFIX FIX Engine
 * <p>
 * This file may be distributed under the terms of the quickfixengine.org license as defined by
 * quickfixengine.org and appearing in the file LICENSE included in the packaging of this file.
 * <p>
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE WARRANTY OF DESIGN,
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * <p>
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 * <p>
 * Contact ask@quickfixengine.org if any conditions of this licensing are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.application.Platform;
import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.LogonEvent;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventType;
import quickfix.examples.banzai.ui.event.SimpleOrderEventSource;
import quickfix.examples.utility.DefaultMessageSender;
import quickfix.examples.utility.MessageSender;
import quickfix.field.AvgPx;
import quickfix.field.BeginString;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.DeliverToCompID;
import quickfix.field.ExecID;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

import static quickfix.examples.banzai.TypeMapping.FIXSideToSide;

@Component("banzaiService")
public class BanzaiServiceImpl extends SimpleOrderEventSource implements Application, IBanzaiService {
  private static final Logger logger = LoggerFactory.getLogger(BanzaiServiceImpl.class);

  private final ObservableLogon observableLogon = new ObservableLogon();

  private boolean isAvailable = true;
  private boolean isMissingField;
  private final MessageSender messageSender = new DefaultMessageSender();

  private static final HashMap<SessionID, Set<ExecID>> execIDs = new HashMap<>();
  private static final FixMessageBuilderFactory fixMessageBuilderFactory = new FixMessageBuilderFactory(new DefaultMessageFactory());
  private final Map<String, Order> orderMap = new HashMap<>();

  public void onCreate(final SessionID sessionID) {
  }

  public void onLogon(final SessionID sessionID) {
    this.observableLogon.logon(sessionID);
  }

  public void onLogout(final SessionID sessionID) {
    this.observableLogon.logoff(sessionID);
  }

  public void toAdmin(final quickfix.Message message, final SessionID sessionID) {
  }

  public void toApp(final quickfix.Message message, final SessionID sessionID) throws DoNotSend {
  }

  public void fromAdmin(final quickfix.Message message, final SessionID sessionID)
          throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
  }

  public void fromApp(final quickfix.Message message, final SessionID sessionID)
          throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    try {
      Platform.runLater(new MessageProcessor(message, sessionID));
    } catch (final Exception e) {
    }
  }

  private class MessageProcessor implements Runnable {
    private final quickfix.Message message;

    private final SessionID sessionID;

    MessageProcessor(final quickfix.Message message, final SessionID sessionID) {
      this.message = message;
      this.sessionID = sessionID;
    }

    public void run() {
      try {
        final MsgType msgType = new MsgType();
        if (BanzaiServiceImpl.this.isAvailable) {
          if (BanzaiServiceImpl.this.isMissingField) {
            // For OpenFIX certification testing
            sendBusinessReject(this.message, BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING,
                    "Conditionally required field missing");
          } else if (this.message.getHeader().isSetField(DeliverToCompID.FIELD)) {
            // This is here to support OpenFIX certification
            sendSessionReject(this.message, SessionRejectReason.COMPID_PROBLEM);
          } else if (this.message.getHeader().getField(msgType).valueEquals("8")) {
            executionReport(this.message, this.sessionID);
          } else if (this.message.getHeader().getField(msgType).valueEquals("9")) {
            cancelReject(this.message, this.sessionID);
          } else {
            sendBusinessReject(this.message, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                    "Unsupported Message Type");
          }
        } else {
          sendBusinessReject(this.message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
                  "Application not available");
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void sendSessionReject(final Message message, final int rejectReason)
          throws FieldNotFound, SessionNotFound {
    final String beginString = message.getHeader().getString(BeginString.FIELD);
    final Message reply = getFixMessageBuilder(beginString).sessionReject(message, rejectReason);
    Session.sendToTarget(reply);
    logger.error("Reject: {}", reply.toString());
  }

  private void sendBusinessReject(final Message message, final int rejectReason, final String rejectText)
          throws FieldNotFound, SessionNotFound {
    final String beginString = message.getHeader().getString(BeginString.FIELD);
    final Message reply = getFixMessageBuilder(beginString).businessReject(message, rejectReason,
            rejectText);
    Session.sendToTarget(reply);
    logger.error("Reject: {}", reply.toString());
  }

  private void executionReport(final Message message, final SessionID sessionID) throws FieldNotFound {
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
      final Execution execution = new Execution();
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

  private Order getOrder(final String ID) throws FieldNotFound {
    return this.orderMap.get(ID);
  }

  private void addClOrdID(final Order order) {
    this.orderMap.put(order.getID(), order);
  }

  private void cancelReject(final Message message, final SessionID sessionID) throws FieldNotFound {

    final String id = message.getField(new ClOrdID()).getValue();
    Order order = getOrder(id);
    if (order == null) return;
    if (order.getOriginalID() != null) order = getOrder(order.getOriginalID());

    try {
      order.setMessage(message.getField(new Text()).getValue());
    } catch (final FieldNotFound e) {
    }
  }

  private boolean alreadyProcessed(final ExecID execID, final SessionID sessionID) {
    Set<ExecID> set = execIDs.get(sessionID);
    if (set == null) {
      set = new HashSet<>();
      set.add(execID);
      execIDs.put(sessionID, set);
      return false;
    } else {
      if (set.contains(execID)) return true;
      set.add(execID);
      return false;
    }
  }

  private void send(final quickfix.Message message, final SessionID sessionID) {
    this.messageSender.sendMessage(message, sessionID);
  }

  @Override
  public void send(final Order order) {
    final String beginString = order.getSessionID().getBeginString();
    final quickfix.examples.banzai.fix.FixMessageBuilder builder = getFixMessageBuilder(beginString);
    final quickfix.Message newOrderSingle = builder.newOrder(order);
    addClOrdID(order);
    send(newOrderSingle, order.getSessionID());
  }

  @Override
  public void cancel(final Order order) {
    final String beginString = order.getSessionID().getBeginString();
    final quickfix.examples.banzai.fix.FixMessageBuilder builder = getFixMessageBuilder(beginString);
    final quickfix.Message message = builder.cancel(order);
    addClOrdID(order);
    send(message, order.getSessionID());
  }

  @Override
  public void replace(final Order order, final Order newOrder) {
    final String beginString = order.getSessionID().getBeginString();
    final quickfix.examples.banzai.fix.FixMessageBuilder builder = getFixMessageBuilder(beginString);
    final quickfix.Message message = builder.replace(order, newOrder);
    addClOrdID(newOrder);
    send(message, order.getSessionID());
  }

  public boolean isMissingField() {
    return this.isMissingField;
  }

  public void setMissingField(final boolean isMissingField) {
    this.isMissingField = isMissingField;
  }

  public boolean isAvailable() {
    return this.isAvailable;
  }

  public void setAvailable(final boolean isAvailable) {
    this.isAvailable = isAvailable;
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

  public void addLogonObserver(final Observer observer) {
    this.observableLogon.addObserver(observer);
  }

  public void deleteLogonObserver(final Observer observer) {
    this.observableLogon.deleteObserver(observer);
  }

  private quickfix.examples.banzai.fix.FixMessageBuilder getFixMessageBuilder(final String beginString) {
    return fixMessageBuilderFactory.getFixMessageBuilder(beginString);
  }
}
