package quickfix.examples.banzai.fix;

import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.field.ClOrdID;
import quickfix.field.CxlType;
import quickfix.field.HandlInst;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Symbol;
import quickfix.fix40.NewOrderSingle;
import quickfix.fix40.OrderCancelReplaceRequest;
import quickfix.fix40.OrderCancelRequest;

import static quickfix.examples.banzai.TypeMapping.sideToFIXSide;
import static quickfix.examples.banzai.TypeMapping.typeToFIXType;


public class Fix40MessageBuilder extends AbstractFixMessageBuilder {

    public Fix40MessageBuilder(MessageFactory messageFactory) {
        super(messageFactory);
    }

    public Message createNewOrderSingle(Order order) {
        NewOrderSingle newOrderSingle = new NewOrderSingle(new ClOrdID(
                order.getID()), new HandlInst('1'), new Symbol(
                order.getSymbol()), sideToFIXSide(order.getSide()),
                new OrderQty(order.getQuantity()),
                typeToFIXType(order.getType()));

        return newOrderSingle;
    }

    public Message createReplaceRequest(Order order, Order newOrder) {
        OrderCancelReplaceRequest message = new OrderCancelReplaceRequest(
                new OrigClOrdID(order.getID()), new ClOrdID(newOrder.getID()),
                new HandlInst('1'), new Symbol(order.getSymbol()),
                sideToFIXSide(order.getSide()), new OrderQty(
                newOrder.getQuantity()), typeToFIXType(order.getType()));

        return message;
    }

    public Message createCancelRequest(Order order) {
        String id = order.generateID();
        OrderCancelRequest message = new OrderCancelRequest(new OrigClOrdID(
                order.getID()), new ClOrdID(id), new CxlType(
                CxlType.FULL_REMAINING_QUANTITY),
                new Symbol(order.getSymbol()), sideToFIXSide(order.getSide()),
                new OrderQty(order.getQuantity()));

        return message;
    }

}
