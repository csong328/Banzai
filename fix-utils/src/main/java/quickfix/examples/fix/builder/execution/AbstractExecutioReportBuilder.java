package quickfix.examples.fix.builder.execution;

import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.utility.MessageBuilder;
import quickfix.field.Account;
import quickfix.field.BeginString;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.DeliverToCompID;
import quickfix.field.DeliverToSubID;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.OnBehalfOfSubID;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SenderCompID;
import quickfix.field.SenderSubID;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TargetSubID;

public abstract class AbstractExecutioReportBuilder implements
        ExecutionReportBuilder {
  private final MessageFactory messageFactory = new DefaultMessageFactory();

  protected void reverseRoute(Message message, Message reply)
          throws FieldNotFound {
    reverseRoute(message.getHeader(), reply.getHeader());
  }

  protected static void reverseRoute(Message.Header messageHdr, Message.Header reply) throws FieldNotFound {
    MessageBuilder<Message.Header> replyBuilder = MessageBuilder.newBuilder(reply);

    replyBuilder.setString(SenderCompID.FIELD, messageHdr.getString(TargetCompID.FIELD))
            .setString(TargetCompID.FIELD, messageHdr.getString(SenderCompID.FIELD));

    if (messageHdr.isSetField(TargetSubID.FIELD)) {
      replyBuilder.setString(SenderSubID.FIELD,
              messageHdr.getString(TargetSubID.FIELD));
    }
    if (messageHdr.isSetField(SenderSubID.FIELD)) {
      replyBuilder.setString(TargetSubID.FIELD,
              messageHdr.getString(SenderSubID.FIELD));
    }
    if (messageHdr.isSetField(OnBehalfOfCompID.FIELD)) {
      replyBuilder.setString(DeliverToCompID.FIELD,
              messageHdr.getString(OnBehalfOfCompID.FIELD));
    }
    if (messageHdr.isSetField(DeliverToCompID.FIELD)) {
      replyBuilder.setString(OnBehalfOfCompID.FIELD,
              messageHdr.getString(DeliverToCompID.FIELD));
    }
    if (messageHdr.isSetField(OnBehalfOfSubID.FIELD)) {
      replyBuilder.setString(DeliverToSubID.FIELD,
              messageHdr.getString(OnBehalfOfSubID.FIELD));
    }
    if (messageHdr.isSetField(DeliverToSubID.FIELD)) {
      replyBuilder.setString(OnBehalfOfSubID.FIELD,
              messageHdr.getString(DeliverToSubID.FIELD));
    }
  }

  public Message rejectMessage(Message message, int rejectReason)
          throws FieldNotFound {
    Message reply = createMessage(message, MsgType.REJECT);
    reverseRoute(message, reply);

    MessageBuilder<Message> replyBuilder = MessageBuilder.newBuilder(reply);

    replyBuilder
            .setString(RefSeqNum.FIELD, message.getHeader().getString(MsgSeqNum.FIELD))
            .setString(RefMsgType.FIELD, message.getHeader().getString(MsgType.FIELD))
            .setInt(SessionRejectReason.FIELD, rejectReason);
    return replyBuilder.build();
  }

  protected Message createMessage(Message message, String msgType)
          throws FieldNotFound {
    return messageFactory.create(
            message.getHeader().getString(BeginString.FIELD), msgType);
  }

  public Message pendingAck(Message newOrderSingle, String orderID,
                            String execID) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message pendingCancel(Message cancelRequest, String orderID,
                               String execID, double orderQty, double cumQty, double avgPx) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message pendingReplace(Message replaceRequest, String orderID,
                                String execID, double orderQty, double cumQty, double avgPx)
          throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message orderAcked(Message newOrderSingle, String orderID,
                            String execID) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message orderRejected(Message newOrderSingle, String orderID,
                               String execID, String text) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message fillOrder(Message newOrderSingle, String orderID,
                           String execID, char ordStatus, double cumQty, double avgPx,
                           double lastShares, double lastPx) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message orderCanceled(Message cancelRequest, String orderID,
                               String execID, double cumQty, double avgPx) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  public Message orderReplaced(Message replaceRequest, String orderID,
                               String execID, double cumQty, double avgPx) throws FieldNotFound {
    throw new UnsupportedOperationException();
  }

  protected char getFillType(Message message, double cumQty)
          throws FieldNotFound {
    OrderQty orderQty = new OrderQty();
    message.getField(orderQty);
    char execType = cumQty < orderQty.getValue() ? ExecType.PARTIAL_FILL
            : ExecType.FILL;
    return execType;
  }

  protected void copyToExecution(Message order, Message exec)
          throws FieldNotFound {
    MessageBuilder<Message> execBuilder = MessageBuilder.newBuilder(exec);

    if (order.isSetField(OrderID.FIELD)) {
      execBuilder.setField(order.getField(new OrderID()));
    }
    execBuilder.setField(order.getField(new ClOrdID()));
    if (order.isSetField(OrigClOrdID.FIELD)) {
      execBuilder.setField(order.getField(new OrigClOrdID()));
    }
    execBuilder.setField(order.getField(new Symbol()))
            .setField(order.getField(new Side()))
            .setField(order.getField(new OrderQty()))
            .setField(order.getField(new OrdType()));
    if (order.isSetField(Account.FIELD)) {
      execBuilder.setField(order.getField(new Account()));
    }
    if (order.isSetField(Price.FIELD)) {
      execBuilder.setField(order.getField(new Price()));
    }
    if (order.isSetField(StopPx.FIELD)) {
      execBuilder.setField(order.getField(new StopPx()));
    }
  }

  protected Message createExecutionReport(Message order, String orderID,
                                          String execID) throws FieldNotFound {
    Message exec = createMessage(order, MsgType.EXECUTION_REPORT);

    MessageBuilder.newBuilder(exec)
            .setField(new OrderID(orderID))
            .setField(new ExecID(execID));
    reverseRoute(order, exec);
    copyToExecution(order, exec);

    return exec;
  }

  public Message cancelRejected(Message order, String orderID,
                                char ordStatus, double cumQty, double avgPx, int cxlRejReason) throws FieldNotFound {
    Message exec = createMessage(order, MsgType.ORDER_CANCEL_REJECT);
    MessageBuilder<Message> execBuilder = MessageBuilder.newBuilder(exec);
    execBuilder.setField(new OrderID(orderID));
    reverseRoute(order, exec);

    if (order.isSetField(OrderID.FIELD)) {
      execBuilder.setField(order.getField(new OrderID()));
    }
    execBuilder.setField(order.getField(new ClOrdID()));
    if (order.isSetField(OrigClOrdID.FIELD)) {
      execBuilder.setField(order.getField(new OrigClOrdID()));
    }
    execBuilder.setField(new OrdStatus(ordStatus));

    String msgType = exec.getHeader().getString(MsgType.FIELD);
    char cxlRejResponseTo = '1';
    if ("G".equals(msgType) || "AC".equals(msgType)) {
      cxlRejResponseTo = '2';
    }
    execBuilder.setField(new CxlRejResponseTo(cxlRejResponseTo))
            .setField(new CxlRejReason(cxlRejReason));

    return execBuilder.build();
  }

  public Message cancelRejectedForUnknownOrder(Message order) throws FieldNotFound {
    Message exec = createMessage(order, MsgType.ORDER_CANCEL_REJECT);
    MessageBuilder<Message> execBuilder = MessageBuilder.newBuilder(exec);
    reverseRoute(order, exec);

    if (order.isSetField(OrderID.FIELD)) {
      execBuilder.setField(order.getField(new OrderID()));
    }
    execBuilder.setField(order.getField(new ClOrdID()));
    if (order.isSetField(OrigClOrdID.FIELD)) {
      execBuilder.setField(order.getField(new OrigClOrdID()));
    }
    execBuilder.setField(new OrdStatus(OrdStatus.REJECTED));

    String msgType = exec.getHeader().getString(MsgType.FIELD);
    char cxlRejResponseTo = '1';
    if ("G".equals(msgType) || "AC".equals(msgType)) {
      cxlRejResponseTo = '2';
    }
    return execBuilder.setField(new CxlRejResponseTo(cxlRejResponseTo))
            .setField(new CxlRejReason(1)).build();
  }
}
