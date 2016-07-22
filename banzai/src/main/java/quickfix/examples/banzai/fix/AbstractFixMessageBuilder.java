package quickfix.examples.banzai.fix;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderType;
import quickfix.field.BeginString;
import quickfix.field.LocateReqd;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SenderCompID;
import quickfix.field.SessionRejectReason;
import quickfix.field.StopPx;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

import static quickfix.examples.banzai.TypeMapping.tifToFIXTif;
import static quickfix.examples.banzai.TypeMapping.typeToFIXType;


public abstract class AbstractFixMessageBuilder implements FixMessageBuilder {
	private final MessageFactory messageFactory;

	public AbstractFixMessageBuilder(MessageFactory messageFactory) {
		super();
		this.messageFactory = messageFactory;
	}

	public Message businessReject(Message message, int rejectReason,
                                  String rejectText) throws FieldNotFound {
		Message reply = sessionReject(message, rejectReason);
		reply.setString(Text.FIELD, rejectText);
		return reply;
	}

	public Message sessionReject(Message message, int rejectReason)
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

	private Message createMessage(Message message, String msgType)
			throws FieldNotFound {
		return messageFactory.create(
				message.getHeader().getString(BeginString.FIELD), msgType);
	}

	private void reverseRoute(Message message, Message reply)
			throws FieldNotFound {
		reply.getHeader().setString(SenderCompID.FIELD,
				message.getHeader().getString(TargetCompID.FIELD));
		reply.getHeader().setString(TargetCompID.FIELD,
				message.getHeader().getString(SenderCompID.FIELD));
	}

	public Message newOrder(Order order) {
		Message newOrderSingle = createNewOrderSingle(order);
		populateOrder(order, newOrderSingle);
		return newOrderSingle;
	}

	private Message populateOrder(Order order, Message newOrderSingle) {
		OrderType type = order.getType();

		if (type == OrderType.LIMIT)
			newOrderSingle.setField(new Price(order.getLimit().doubleValue()));
		else if (type == OrderType.STOP) {
			newOrderSingle.setField(new StopPx(order.getStop().doubleValue()));
		} else if (type == OrderType.STOP_LIMIT) {
			newOrderSingle.setField(new Price(order.getLimit().doubleValue()));
			newOrderSingle.setField(new StopPx(order.getStop().doubleValue()));
		}

		if (order.getSide() == OrderSide.SHORT_SELL
				|| order.getSide() == OrderSide.SHORT_SELL_EXEMPT) {
			newOrderSingle.setField(new LocateReqd(false));
		}

		newOrderSingle.setField(tifToFIXTif(order.getTIF()));
		return newOrderSingle;
	}

	public Message replace(Order order, Order newOrder) {
		Message message = createReplaceRequest(order, newOrder);
		populateCancelReplace(order, newOrder, message);
		return message;
	}

	private Message populateCancelReplace(Order order, Order newOrder,
                                          quickfix.Message message) {
		message.setField(new OrderQty(newOrder.getQuantity()));
		message.setField(typeToFIXType(newOrder.getType()));
		if (newOrder.getLimit() != null)
			message.setField(new Price(newOrder.getLimit().doubleValue()));
		return message;
	}

	public Message cancel(Order order) {
		return createCancelRequest(order);
	}

	protected abstract Message createNewOrderSingle(Order order);

	protected abstract Message createReplaceRequest(Order order, Order newOrder);

	protected abstract Message createCancelRequest(Order order);
}
