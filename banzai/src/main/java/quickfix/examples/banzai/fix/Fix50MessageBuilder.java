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
import quickfix.fix50.NewOrderSingle;
import quickfix.fix50.OrderCancelReplaceRequest;
import quickfix.fix50.OrderCancelRequest;

import static quickfix.examples.banzai.TypeMapping.sideToFIXSide;
import static quickfix.examples.banzai.TypeMapping.typeToFIXType;

public class Fix50MessageBuilder extends AbstractFixMessageBuilder {

    public Fix50MessageBuilder(MessageFactory messageFactory) {
        super(messageFactory);
    }

    public Message createNewOrderSingle(Order order) {
        NewOrderSingle newOrderSingle = new NewOrderSingle(new ClOrdID(
                order.getID()), sideToFIXSide(order.getSide()),
                new TransactTime(), typeToFIXType(order.getType()));
        newOrderSingle.set(new OrderQty(order.getQuantity()));
        newOrderSingle.set(new Symbol(order.getSymbol()));
        newOrderSingle.set(new HandlInst('1'));

        return newOrderSingle;
    }

    public Message createReplaceRequest(Order order, Order newOrder) {
        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest(
                new OrigClOrdID(order.getID()), new ClOrdID(newOrder.getID()),
                sideToFIXSide(order.getSide()), new TransactTime(),
                typeToFIXType(order.getType()));

        return message;
    }

    public Message createCancelRequest(Order order) {
        String id = order.generateID();
        OrderCancelRequest message = new OrderCancelRequest(new OrigClOrdID(
                order.getID()), new ClOrdID(id),
                sideToFIXSide(order.getSide()), new TransactTime());
        message.setField(new OrderQty(order.getQuantity()));

        return message;
    }

}
