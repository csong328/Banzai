package quickfix.examples.banzai.fix;

import java.util.HashMap;
import java.util.Map;

import quickfix.FixVersions;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.examples.banzai.Order;
import quickfix.field.CxlType;
import quickfix.field.TransactTime;

public class FixMessageBuilderFactory {
  private Map<String, FixMessageBuilder> fixMessageBuilders = new HashMap<String, FixMessageBuilder>();

  public FixMessageBuilderFactory(MessageFactory messageFactory) {
    super();
    FixMessageBuilder fix40 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX40) {
      @Override
      public Message createCancelRequest(Order order) {
        Message cancel = super.createCancelRequest(order);
        cancel.setField(new CxlType(
                CxlType.FULL_REMAINING_QUANTITY));
        return cancel;
      }
    };

    FixMessageBuilder fix41 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX41);

    FixMessageBuilder fix42 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX42) {
      @Override
      public Message createNewOrderSingle(Order order) {
        Message request = super.createNewOrderSingle(order);
        request.setField(new TransactTime());
        return request;
      }

      @Override
      public Message createReplaceRequest(Order order, Order newOrder) {
        Message request = super.createReplaceRequest(order, newOrder);
        request.setField(new TransactTime());
        return request;
      }

      @Override
      public Message createCancelRequest(Order order) {
        Message request = super.createCancelRequest(order);
        request.setField(new TransactTime());
        return request;
      }
    };

    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX40, fix40);
    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX41, fix41);
    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX42, fix42);
    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX43, fix42);
    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX44, fix42);
    fixMessageBuilders.put(FixVersions.BEGINSTRING_FIXT11, fix42);
  }

  public FixMessageBuilder getFixMessageBuilder(String beginString) {
    return fixMessageBuilders.get(beginString);
  }
}
