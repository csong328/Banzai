package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.field.TransactTime;

public class Fix42MessageBuilder extends AbstractFixMessageBuilder {

  public Fix42MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIX42);
  }

  public Fix42MessageBuilder(MessageFactory messageFactory, String beginString) {
    super(messageFactory, beginString);
  }

  public Message createNewOrderSingle(Order order) {
    Message request = super.createNewOrderSingle(order);
    request.setField(new TransactTime());
    return request;
  }

  public Message createReplaceRequest(Order order, Order newOrder) {
    Message request = super.createReplaceRequest(order, newOrder);
    request.setField(new TransactTime());
    return request;
  }

  public Message createCancelRequest(Order order) {
    Message request = super.createCancelRequest(order);
    request.setField(new TransactTime());
    return request;
  }

}
