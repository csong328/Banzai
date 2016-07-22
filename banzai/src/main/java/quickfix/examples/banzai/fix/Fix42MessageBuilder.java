package quickfix.examples.banzai.fix;

import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.OrderCancelRequest;

import static quickfix.examples.banzai.TypeMapping.sideToFIXSide;
import static quickfix.examples.banzai.TypeMapping.typeToFIXType;

public class Fix42MessageBuilder extends AbstractFixMessageBuilder {

	public Fix42MessageBuilder(MessageFactory messageFactory) {
		super(messageFactory);
	}

	public Message createNewOrderSingle(Order order) {
		NewOrderSingle newOrderSingle = new NewOrderSingle(new ClOrdID(
				order.getID()), new HandlInst('1'), new Symbol(
				order.getSymbol()), sideToFIXSide(order.getSide()),
				new TransactTime(), typeToFIXType(order.getType()));
		newOrderSingle.set(new OrderQty(order.getQuantity()));

		return newOrderSingle;
	}

	public Message createReplaceRequest(Order order, Order newOrder) {
		OrderCancelReplaceRequest message = new OrderCancelReplaceRequest(
				new OrigClOrdID(order.getID()), new ClOrdID(newOrder.getID()),
				new HandlInst('1'), new Symbol(order.getSymbol()),
				sideToFIXSide(order.getSide()), new TransactTime(),
				typeToFIXType(newOrder.getType()));
		return message;
	}

	public Message createCancelRequest(Order order) {
		String id = order.generateID();
		OrderCancelRequest message = new OrderCancelRequest(new OrigClOrdID(
				order.getID()), new ClOrdID(id), new Symbol(order.getSymbol()),
				sideToFIXSide(order.getSide()), new TransactTime());
		message.setField(new OrderQty(order.getQuantity()));

		return message;
	}

}
