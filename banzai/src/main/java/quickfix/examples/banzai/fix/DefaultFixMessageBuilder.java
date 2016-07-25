package quickfix.examples.banzai.fix;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.model.OrderSide;
import quickfix.examples.banzai.model.OrderType;
import quickfix.examples.utility.MessageBuilder;
import quickfix.field.BeginString;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.LocateReqd;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SenderCompID;
import quickfix.field.SessionRejectReason;
import quickfix.field.StopPx;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

import static com.google.common.base.Preconditions.checkArgument;
import static quickfix.examples.banzai.model.TypeMapping.sideToFIXSide;
import static quickfix.examples.banzai.model.TypeMapping.tifToFIXTif;
import static quickfix.examples.banzai.model.TypeMapping.typeToFIXType;

public class DefaultFixMessageBuilder implements FixMessageBuilder {
  private final MessageFactory messageFactory;
  private final String beginString;

  public DefaultFixMessageBuilder(final MessageFactory messageFactory, final String beginString) {
    super();
    this.messageFactory = messageFactory;
    this.beginString = beginString;
  }

  public Message businessReject(final Message message, final int rejectReason,
                                final String rejectText) throws FieldNotFound {
    final Message reply = sessionReject(message, rejectReason);
    reply.setString(Text.FIELD, rejectText);
    return reply;
  }

  public Message sessionReject(final Message message, final int rejectReason)
          throws FieldNotFound {

    checkArgument(this.beginString.equals(message.getHeader().getString(BeginString.FIELD)), "version mismatch");
    final Message reply = createMessage(MsgType.REJECT);
    reverseRoute(message, reply);
    final String refSeqNum = message.getHeader().getString(MsgSeqNum.FIELD);
    reply.setString(RefSeqNum.FIELD, refSeqNum);
    reply.setString(RefMsgType.FIELD,
            message.getHeader().getString(MsgType.FIELD));
    reply.setInt(SessionRejectReason.FIELD, rejectReason);
    return reply;
  }

  protected Message createMessage(final String msgType) {
    return this.messageFactory.create(this.beginString, msgType);
  }

  private void reverseRoute(final Message message, final Message reply)
          throws FieldNotFound {
    reply.getHeader().setString(SenderCompID.FIELD,
            message.getHeader().getString(TargetCompID.FIELD));
    reply.getHeader().setString(TargetCompID.FIELD,
            message.getHeader().getString(SenderCompID.FIELD));
  }

  public Message newOrder(final Order order) {
    final Message newOrderSingle = createNewOrderSingle(order);
    populateOrder(order, newOrderSingle);
    return newOrderSingle;
  }

  private Message populateOrder(final Order order, final Message newOrderSingle) {
    final OrderType type = order.getType();

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

  public Message replace(final Order order, final Order newOrder) {
    final Message message = createReplaceRequest(order, newOrder);
    populateCancelReplace(order, newOrder, message);
    return message;
  }

  private Message populateCancelReplace(final Order order, final Order newOrder,
                                        final quickfix.Message message) {
    message.setField(new OrderQty(newOrder.getQuantity()));
    message.setField(typeToFIXType(newOrder.getType()));
    if (newOrder.getLimit() != null)
      message.setField(new Price(newOrder.getLimit().doubleValue()));
    return message;
  }

  public Message cancel(final Order order) {
    return createCancelRequest(order);
  }

  protected Message createNewOrderSingle(final Order order) {
    return MessageBuilder.newBuilder(createMessage("D"))
            .setField(new ClOrdID(order.getID()))
            .setField(new HandlInst('1'))
            .setField(new Symbol(order.getSymbol()))
            .setField(sideToFIXSide(order.getSide()))
            .setField(new OrderQty(order.getQuantity()))
            .setField(typeToFIXType(order.getType())).build();
  }

  protected Message createReplaceRequest(final Order order, final Order newOrder) {
    return MessageBuilder.newBuilder(createMessage("G"))
            .setField(new OrigClOrdID(order.getID()))
            .setField(new ClOrdID(newOrder.getID()))
            .setField(new OrderID(order.getOrderID()))
            .setField(new HandlInst('1'))
            .setField(new Symbol(order.getSymbol()))
            .setField(sideToFIXSide(newOrder.getSide()))
            .setField(new OrderQty(newOrder.getQuantity()))
            .setField(typeToFIXType(newOrder.getType())).build();
  }

  protected Message createCancelRequest(final Order order) {
    return MessageBuilder.newBuilder(createMessage("F"))
            .setField(new OrigClOrdID(order.getOriginalID()))
            .setField(new ClOrdID(order.getID()))
            .setField(new OrderID(order.getOrderID()))
            .setField(new Symbol(order.getSymbol()))
            .setField(sideToFIXSide(order.getSide()))
            .setField(new OrderQty(order.getQuantity())).build();
  }
}
