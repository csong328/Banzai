package quickfix.examples.fix.builder.execution;

import quickfix.DefaultMessageFactory;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageFactory;
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

	protected static void reverseRoute(Message message, Message reply)
			throws FieldNotFound {
		reverseRoute(message.getHeader(), reply.getHeader());
	}

	protected static void reverseRoute(Message.Header messageHdr,
                                       Message.Header replyHdr) throws FieldNotFound {
		replyHdr.setString(SenderCompID.FIELD,
				messageHdr.getString(TargetCompID.FIELD));
		replyHdr.setString(TargetCompID.FIELD,
				messageHdr.getString(SenderCompID.FIELD));
		if (messageHdr.isSetField(TargetSubID.FIELD)) {
			replyHdr.setString(SenderSubID.FIELD,
					messageHdr.getString(TargetSubID.FIELD));
		}
		if (messageHdr.isSetField(SenderSubID.FIELD)) {
			replyHdr.setString(TargetSubID.FIELD,
					messageHdr.getString(SenderSubID.FIELD));
		}
		if (messageHdr.isSetField(OnBehalfOfCompID.FIELD)) {
			replyHdr.setString(DeliverToCompID.FIELD,
					messageHdr.getString(OnBehalfOfCompID.FIELD));
		}
		if (messageHdr.isSetField(DeliverToCompID.FIELD)) {
			replyHdr.setString(OnBehalfOfCompID.FIELD,
					messageHdr.getString(DeliverToCompID.FIELD));
		}
		if (messageHdr.isSetField(OnBehalfOfSubID.FIELD)) {
			replyHdr.setString(DeliverToSubID.FIELD,
					messageHdr.getString(OnBehalfOfSubID.FIELD));
		}
		if (messageHdr.isSetField(DeliverToSubID.FIELD)) {
			replyHdr.setString(OnBehalfOfSubID.FIELD,
					messageHdr.getString(DeliverToSubID.FIELD));
		}
	}

	public Message rejectMessage(Message message, int rejectReason)
			throws FieldNotFound {
		Message reply = createMessage(message, MsgType.REJECT);
		reverseRoute(message, reply);
		String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
		reply.setString(RefSeqNum.FIELD, refSeqNum);
		reply.setString(RefMsgType.FIELD,
				message.getHeader().getString(MsgType.FIELD));
		reply.setInt(SessionRejectReason.FIELD, rejectReason);
		return reply;
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
		if (order.isSetField(OrderID.FIELD)) {
			exec.setField(order.getField(new OrderID()));
		}
		exec.setField(order.getField(new ClOrdID()));
		if (order.isSetField(OrigClOrdID.FIELD)) {
			exec.setField(order.getField(new OrigClOrdID()));
		}
		exec.setField(order.getField(new Symbol()));
		exec.setField(order.getField(new Side()));
		exec.setField(order.getField(new OrderQty()));
		exec.setField(order.getField(new OrdType()));
		if (order.isSetField(Account.FIELD)) {
			exec.setField(order.getField(new Account()));
		}
		if (order.isSetField(Price.FIELD)) {
			exec.setField(order.getField(new Price()));
		}
		if (order.isSetField(StopPx.FIELD)) {
			exec.setField(order.getField(new StopPx()));
		}
	}

	protected Message createExecutionReport(Message order, String orderID,
                                            String execID) throws FieldNotFound {
		Message exec = createMessage(order, MsgType.EXECUTION_REPORT);
		exec.setField(new OrderID(orderID));
		exec.setField(new ExecID(execID));
		reverseRoute(order, exec);
		copyToExecution(order, exec);

		return exec;
	}

	public Message cancelRejected(Message order, String orderID,
                                  char ordStatus, double cumQty, double avgPx, int cxlRejReason) throws FieldNotFound {
		Message exec = createMessage(order, MsgType.ORDER_CANCEL_REJECT);
		exec.setField(new OrderID(orderID));
		reverseRoute(order, exec);

		if (order.isSetField(OrderID.FIELD)) {
			exec.setField(order.getField(new OrderID()));
		}
		exec.setField(order.getField(new ClOrdID()));
		if (order.isSetField(OrigClOrdID.FIELD)) {
			exec.setField(order.getField(new OrigClOrdID()));
		}
		exec.setField(new OrdStatus(ordStatus));

		String msgType = exec.getHeader().getString(MsgType.FIELD);
		char cxlRejResponseTo = '1';
		if ("G".equals(msgType) || "AC".equals(msgType)) {
			cxlRejResponseTo = '2';
		}
		exec.setField(new CxlRejResponseTo(cxlRejResponseTo));
		exec.setField(new CxlRejReason(cxlRejReason));

		return exec;
	}
	
	public Message cancelRejectedForUnknownOrder(Message order) throws FieldNotFound {
		Message exec = createMessage(order, MsgType.ORDER_CANCEL_REJECT);
		reverseRoute(order, exec);

		if (order.isSetField(OrderID.FIELD)) {
			exec.setField(order.getField(new OrderID()));
		}
		exec.setField(order.getField(new ClOrdID()));
		if (order.isSetField(OrigClOrdID.FIELD)) {
			exec.setField(order.getField(new OrigClOrdID()));
		}
		exec.setField(new OrdStatus(OrdStatus.REJECTED));

		String msgType = exec.getHeader().getString(MsgType.FIELD);
		char cxlRejResponseTo = '1';
		if ("G".equals(msgType) || "AC".equals(msgType)) {
			cxlRejResponseTo = '2';
		}
		exec.setField(new CxlRejResponseTo(cxlRejResponseTo));
		exec.setField(new CxlRejReason(1));

		return exec;
	}
}
