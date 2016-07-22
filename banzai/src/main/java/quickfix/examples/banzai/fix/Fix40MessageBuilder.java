package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.field.CxlType;


public class Fix40MessageBuilder extends AbstractFixMessageBuilder {

  public Fix40MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIX40);
  }

  public Message createCancelRequest(Order order) {
    Message cancel = super.createCancelRequest(order);
    cancel.setField(new CxlType(
            CxlType.FULL_REMAINING_QUANTITY));
    return cancel;
  }

}
