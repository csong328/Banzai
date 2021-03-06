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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import quickfix.*;
import quickfix.examples.banzai.*;
import quickfix.examples.banzai.ui.ExecutionTableModel;
import quickfix.examples.banzai.ui.OrderEntryController;
import quickfix.examples.banzai.ui.OrderEntryModel;
import quickfix.examples.banzai.ui.OrderTableModel;
import quickfix.field.*;

@Component("banzaiService")
public class BanzaiApplication implements Application, IBanzaiService {

  private static final Logger logger = LoggerFactory.getLogger(BanzaiApplication.class);

  @Autowired
  private OrderEntryModel orderEntryModel;
  @Autowired
  private OrderTableModel orderTableModel;
  @Autowired
  private ExecutionTableModel executionTableModel;
  @Autowired
  private OrderEntryController orderEntryController;

  private final DefaultMessageFactory messageFactory = new DefaultMessageFactory();

  private boolean isAvailable = true;

  private boolean isMissingField;

  static private final TwoWayMap sideMap = new TwoWayMap();

  static private final TwoWayMap typeMap = new TwoWayMap();

  static private final TwoWayMap tifMap = new TwoWayMap();

  static private final HashMap<SessionID, Set<ExecID>> execIDs = new HashMap<>();

  public void onCreate(SessionID sessionID) {}

  public void onLogon(SessionID sessionID) {
    orderEntryController.logon(sessionID);
  }

  public void onLogout(SessionID sessionID) {
    orderEntryController.logoff(sessionID);
  }

  public void toAdmin(quickfix.Message message, SessionID sessionID) {}

  public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend {}

  public void fromAdmin(quickfix.Message message, SessionID sessionID)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {}

  public void fromApp(quickfix.Message message, SessionID sessionID)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    try {
      SwingUtilities.invokeLater(new MessageProcessor(message, sessionID));
    } catch (Exception e) {}
  }

  private class MessageProcessor implements Runnable {
    private final quickfix.Message message;

    private final SessionID sessionID;

    MessageProcessor(quickfix.Message message, SessionID sessionID) {
      this.message = message;
      this.sessionID = sessionID;
    }

    public void run() {
      try {
        MsgType msgType = new MsgType();
        if (isAvailable) {
          if (isMissingField) {
            // For OpenFIX certification testing
            sendBusinessReject(message, BusinessRejectReason.CONDITIONALLY_REQUIRED_FIELD_MISSING,
                "Conditionally required field missing");
          } else if (message.getHeader().isSetField(DeliverToCompID.FIELD)) {
            // This is here to support OpenFIX certification
            sendSessionReject(message, SessionRejectReason.COMPID_PROBLEM);
          } else if (message.getHeader().getField(msgType).valueEquals("8")) {
            executionReport(message, sessionID);
          } else if (message.getHeader().getField(msgType).valueEquals("9")) {
            cancelReject(message, sessionID);
          } else {
            sendBusinessReject(message, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                "Unsupported Message Type");
          }
        } else {
          sendBusinessReject(message, BusinessRejectReason.APPLICATION_NOT_AVAILABLE,
              "Application not available");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void sendSessionReject(Message message, int rejectReason)
      throws FieldNotFound, SessionNotFound {
    Message reply = createMessage(message, MsgType.REJECT);
    reverseRoute(message, reply);
    String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
    reply.setString(RefSeqNum.FIELD, refSeqNum);
    reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
    reply.setInt(SessionRejectReason.FIELD, rejectReason);
    Session.sendToTarget(reply);
  }

  private void sendBusinessReject(Message message, int rejectReason, String rejectText)
      throws FieldNotFound, SessionNotFound {
    Message reply = createMessage(message, MsgType.BUSINESS_MESSAGE_REJECT);
    reverseRoute(message, reply);
    String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
    reply.setString(RefSeqNum.FIELD, refSeqNum);
    reply.setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD));
    reply.setInt(BusinessRejectReason.FIELD, rejectReason);
    reply.setString(Text.FIELD, rejectText);
    Session.sendToTarget(reply);
  }

  private Message createMessage(Message message, String msgType) throws FieldNotFound {
    return messageFactory.create(message.getHeader().getString(BeginString.FIELD), msgType);
  }

  private void reverseRoute(Message message, Message reply) throws FieldNotFound {
    reply.getHeader().setString(SenderCompID.FIELD,
        message.getHeader().getString(TargetCompID.FIELD));
    reply.getHeader().setString(TargetCompID.FIELD,
        message.getHeader().getString(SenderCompID.FIELD));
  }

  private void executionReport(Message message, SessionID sessionID) throws FieldNotFound {

    ExecID execID = (ExecID) message.getField(new ExecID());
    if (alreadyProcessed(execID, sessionID)) return;

    Order order = orderTableModel.getOrder(message.getField(new ClOrdID()).getValue());
    if (order == null) {
      return;
    }

    BigDecimal fillSize;

    if (message.isSetField(LastShares.FIELD)) {
      LastShares lastShares = new LastShares();
      message.getField(lastShares);
      fillSize = new BigDecimal("" + lastShares.getValue());
    } else {
      // > FIX 4.1
      // LeavesQty leavesQty = new LeavesQty();
      // message.getField(leavesQty);
      // fillSize =
      // new BigDecimal(order.getQuantity()).subtract(new BigDecimal("" + leavesQty.getValue()));
      fillSize = new BigDecimal(0);
    }

    if (fillSize.compareTo(BigDecimal.ZERO) > 0) {
      order.setOpen(order.getOpen() - (int) Double.parseDouble(fillSize.toPlainString()));
      order.setExecuted(Integer.parseInt(message.getString(CumQty.FIELD)));
      order.setAvgPx(Double.parseDouble(message.getString(AvgPx.FIELD)));
    }

    OrdStatus ordStatus = (OrdStatus) message.getField(new OrdStatus());

    if (ordStatus.valueEquals(OrdStatus.REJECTED)) {
      order.setRejected(true);
      order.setOpen(0);
    } else if (ordStatus.valueEquals(OrdStatus.CANCELED)
        || ordStatus.valueEquals(OrdStatus.DONE_FOR_DAY)) {
      order.setCanceled(true);
      order.setOpen(0);
    } else if (ordStatus.valueEquals(OrdStatus.NEW)) {
      if (order.isNew()) {
        order.setNew(false);
      }
    }

    try {
      order.setMessage(message.getField(new Text()).getValue());
    } catch (FieldNotFound e) {}

    orderTableModel.updateOrder(order, message.getField(new ClOrdID()).getValue());

    if (fillSize.compareTo(BigDecimal.ZERO) > 0) {
      Execution execution = new Execution();
      execution.setExchangeID(sessionID + message.getField(new ExecID()).getValue());

      execution.setSymbol(message.getField(new Symbol()).getValue());
      execution.setQuantity(fillSize.intValue());
      if (message.isSetField(LastPx.FIELD)) {
        execution.setPrice(Double.parseDouble(message.getString(LastPx.FIELD)));
      }
      Side side = (Side) message.getField(new Side());
      execution.setSide(FIXSideToSide(side));
      executionTableModel.addExecution(execution);
    }
  }

  private void cancelReject(Message message, SessionID sessionID) throws FieldNotFound {

    String id = message.getField(new ClOrdID()).getValue();
    Order order = orderTableModel.getOrder(id);
    if (order == null) return;
    if (order.getOriginalID() != null) order = orderTableModel.getOrder(order.getOriginalID());

    try {
      order.setMessage(message.getField(new Text()).getValue());
    } catch (FieldNotFound e) {}
    orderTableModel.updateOrder(order, message.getField(new OrigClOrdID()).getValue());
  }

  private boolean alreadyProcessed(ExecID execID, SessionID sessionID) {
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

  private void send(quickfix.Message message, SessionID sessionID) {
    try {
      Session.sendToTarget(message, sessionID);
    } catch (SessionNotFound e) {
      logger.error(String.format("Failed to send %s", message), e);
    }
  }

  @Override
  public void send(Order order) {
    String beginString = order.getSessionID().getBeginString();
    switch (beginString) {
      case FixVersions.BEGINSTRING_FIX40:
        send40(order);
        break;
      case FixVersions.BEGINSTRING_FIX41:
        send41(order);
        break;
      case FixVersions.BEGINSTRING_FIX42:
        send42(order);
        break;
      case FixVersions.BEGINSTRING_FIX43:
        send43(order);
        break;
      case FixVersions.BEGINSTRING_FIX44:
        send44(order);
        break;
      case FixVersions.BEGINSTRING_FIXT11:
        send50(order);
        break;
    }
  }

  private void send40(Order order) {
    quickfix.fix40.NewOrderSingle newOrderSingle =
        new quickfix.fix40.NewOrderSingle(new ClOrdID(order.getID()), new HandlInst('1'),
            new Symbol(order.getSymbol()), sideToFIXSide(order.getSide()),
            new OrderQty(order.getQuantity()), typeToFIXType(order.getType()));

    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private void send41(Order order) {
    quickfix.fix41.NewOrderSingle newOrderSingle = new quickfix.fix41.NewOrderSingle(
        new ClOrdID(order.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
        sideToFIXSide(order.getSide()), typeToFIXType(order.getType()));
    newOrderSingle.set(new OrderQty(order.getQuantity()));

    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private void send42(Order order) {
    quickfix.fix42.NewOrderSingle newOrderSingle = new quickfix.fix42.NewOrderSingle(
        new ClOrdID(order.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
        sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));
    newOrderSingle.set(new OrderQty(order.getQuantity()));

    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private void send43(Order order) {
    quickfix.fix43.NewOrderSingle newOrderSingle =
        new quickfix.fix43.NewOrderSingle(new ClOrdID(order.getID()), new HandlInst('1'),
            sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));
    newOrderSingle.set(new OrderQty(order.getQuantity()));
    newOrderSingle.set(new Symbol(order.getSymbol()));
    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private void send44(Order order) {
    quickfix.fix44.NewOrderSingle newOrderSingle =
        new quickfix.fix44.NewOrderSingle(new ClOrdID(order.getID()),
            sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));
    newOrderSingle.set(new OrderQty(order.getQuantity()));
    newOrderSingle.set(new Symbol(order.getSymbol()));
    newOrderSingle.set(new HandlInst('1'));
    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private void send50(Order order) {
    quickfix.fix50.NewOrderSingle newOrderSingle =
        new quickfix.fix50.NewOrderSingle(new ClOrdID(order.getID()),
            sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));
    newOrderSingle.set(new OrderQty(order.getQuantity()));
    newOrderSingle.set(new Symbol(order.getSymbol()));
    newOrderSingle.set(new HandlInst('1'));
    send(populateOrder(order, newOrderSingle), order.getSessionID());
  }

  private quickfix.Message populateOrder(Order order, quickfix.Message newOrderSingle) {

    OrderType type = order.getType();
    switch (type) {
      case LIMIT:
        newOrderSingle.setField(new Price(order.getLimit()));
        break;

      case STOP:
        newOrderSingle.setField(new StopPx(order.getStop()));
        break;

      case STOP_LIMIT:
        newOrderSingle.setField(new Price(order.getLimit()));
        newOrderSingle.setField(new StopPx(order.getStop()));
        break;
    }

    if (order.getSide() == OrderSide.SHORT_SELL || order.getSide() == OrderSide.SHORT_SELL_EXEMPT) {
      newOrderSingle.setField(new LocateReqd(false));
    }

    newOrderSingle.setField(tifToFIXTif(order.getTIF()));
    return newOrderSingle;
  }

  @Override
  public void cancel(Order order) {
    String beginString = order.getSessionID().getBeginString();
    switch (beginString) {
      case FixVersions.BEGINSTRING_FIX40:
        cancel40(order);
        break;
      case FixVersions.BEGINSTRING_FIX41:
        cancel41(order);
        break;
      case FixVersions.BEGINSTRING_FIX42:
        cancel42(order);
        break;
    }
  }

  private void cancel40(Order order) {
    String id = order.generateID();
    quickfix.fix40.OrderCancelRequest message =
        new quickfix.fix40.OrderCancelRequest(new OrigClOrdID(order.getID()), new ClOrdID(id),
            new CxlType(CxlType.FULL_REMAINING_QUANTITY), new Symbol(order.getSymbol()),
            sideToFIXSide(order.getSide()), new OrderQty(order.getQuantity()));

    orderTableModel.addClOrdID(order, id);
    send(message, order.getSessionID());
  }

  private void cancel41(Order order) {
    String id = order.generateID();
    quickfix.fix41.OrderCancelRequest message =
        new quickfix.fix41.OrderCancelRequest(new OrigClOrdID(order.getID()), new ClOrdID(id),
            new Symbol(order.getSymbol()), sideToFIXSide(order.getSide()));
    message.setField(new OrderQty(order.getQuantity()));

    orderTableModel.addClOrdID(order, id);
    send(message, order.getSessionID());
  }

  private void cancel42(Order order) {
    String id = order.generateID();
    quickfix.fix42.OrderCancelRequest message =
        new quickfix.fix42.OrderCancelRequest(new OrigClOrdID(order.getID()), new ClOrdID(id),
            new Symbol(order.getSymbol()), sideToFIXSide(order.getSide()), new TransactTime());
    message.setField(new OrderQty(order.getQuantity()));

    orderTableModel.addClOrdID(order, id);
    send(message, order.getSessionID());
  }

  @Override
  public void replace(Order order, Order newOrder) {
    String beginString = order.getSessionID().getBeginString();
    switch (beginString) {
      case FixVersions.BEGINSTRING_FIX40:
        replace40(order, newOrder);
        break;
      case FixVersions.BEGINSTRING_FIX41:
        replace41(order, newOrder);
        break;
      case FixVersions.BEGINSTRING_FIX42:
        replace42(order, newOrder);
        break;
    }
  }

  private void replace40(Order order, Order newOrder) {
    quickfix.fix40.OrderCancelReplaceRequest message = new quickfix.fix40.OrderCancelReplaceRequest(
        new OrigClOrdID(order.getID()), new ClOrdID(newOrder.getID()), new HandlInst('1'),
        new Symbol(order.getSymbol()), sideToFIXSide(order.getSide()),
        new OrderQty(newOrder.getQuantity()), typeToFIXType(order.getType()));

    orderTableModel.addClOrdID(order, newOrder.getID());
    send(populateCancelReplace(order, newOrder, message), order.getSessionID());
  }

  private void replace41(Order order, Order newOrder) {
    quickfix.fix41.OrderCancelReplaceRequest message =
        new quickfix.fix41.OrderCancelReplaceRequest(new OrigClOrdID(order.getID()),
            new ClOrdID(newOrder.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
            sideToFIXSide(order.getSide()), typeToFIXType(order.getType()));

    orderTableModel.addClOrdID(order, newOrder.getID());
    send(populateCancelReplace(order, newOrder, message), order.getSessionID());
  }

  private void replace42(Order order, Order newOrder) {
    quickfix.fix42.OrderCancelReplaceRequest message =
        new quickfix.fix42.OrderCancelReplaceRequest(new OrigClOrdID(order.getID()),
            new ClOrdID(newOrder.getID()), new HandlInst('1'), new Symbol(order.getSymbol()),
            sideToFIXSide(order.getSide()), new TransactTime(), typeToFIXType(order.getType()));

    orderTableModel.addClOrdID(order, newOrder.getID());
    send(populateCancelReplace(order, newOrder, message), order.getSessionID());
  }

  private Message populateCancelReplace(Order order, Order newOrder, quickfix.Message message) {

    if (order.getQuantity() != newOrder.getQuantity())
      message.setField(new OrderQty(newOrder.getQuantity()));
    if (!order.getLimit().equals(newOrder.getLimit()))
      message.setField(new Price(newOrder.getLimit()));
    return message;
  }

  private Side sideToFIXSide(OrderSide side) {
    return (Side) sideMap.getFirst(side);
  }

  private OrderSide FIXSideToSide(Side side) {
    return (OrderSide) sideMap.getSecond(side);
  }

  private OrdType typeToFIXType(OrderType type) {
    return (OrdType) typeMap.getFirst(type);
  }

  public OrderType FIXTypeToType(OrdType type) {
    return (OrderType) typeMap.getSecond(type);
  }

  private TimeInForce tifToFIXTif(OrderTIF tif) {
    return (TimeInForce) tifMap.getFirst(tif);
  }

  public OrderTIF FIXTifToTif(TimeInForce tif) {
    return (OrderTIF) typeMap.getSecond(tif);
  }

  static {
    sideMap.put(OrderSide.BUY, new Side(Side.BUY));
    sideMap.put(OrderSide.SELL, new Side(Side.SELL));
    sideMap.put(OrderSide.SHORT_SELL, new Side(Side.SELL_SHORT));
    sideMap.put(OrderSide.SHORT_SELL_EXEMPT, new Side(Side.SELL_SHORT_EXEMPT));
    sideMap.put(OrderSide.CROSS, new Side(Side.CROSS));
    sideMap.put(OrderSide.CROSS_SHORT, new Side(Side.CROSS_SHORT));

    typeMap.put(OrderType.MARKET, new OrdType(OrdType.MARKET));
    typeMap.put(OrderType.LIMIT, new OrdType(OrdType.LIMIT));
    typeMap.put(OrderType.STOP, new OrdType(OrdType.STOP));
    typeMap.put(OrderType.STOP_LIMIT, new OrdType(OrdType.STOP_LIMIT));

    tifMap.put(OrderTIF.DAY, new TimeInForce(TimeInForce.DAY));
    tifMap.put(OrderTIF.IOC, new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL));
    tifMap.put(OrderTIF.OPG, new TimeInForce(TimeInForce.AT_THE_OPENING));
    tifMap.put(OrderTIF.GTC, new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
    tifMap.put(OrderTIF.GTX, new TimeInForce(TimeInForce.GOOD_TILL_CROSSING));
  }

  public boolean isMissingField() {
    return isMissingField;
  }

  public void setMissingField(boolean isMissingField) {
    this.isMissingField = isMissingField;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean isAvailable) {
    this.isAvailable = isAvailable;
  }
}
