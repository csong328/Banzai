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
  private final Map<String, FixMessageBuilder> fixMessageBuilders = new HashMap<>();

  public FixMessageBuilderFactory(final MessageFactory messageFactory) {
    super();
    final FixMessageBuilder fix40 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX40) {
      @Override
      public Message createCancelRequest(final Order order) {
        final Message cancel = super.createCancelRequest(order);
        cancel.setField(new CxlType(
                CxlType.FULL_REMAINING_QUANTITY));
        return cancel;
      }
    };
    
    final FixMessageBuilder fix41 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX41);

    final FixMessageBuilder fix42 = new DefaultFixMessageBuilder(messageFactory, FixVersions.BEGINSTRING_FIX42) {
      @Override
      public Message createNewOrderSingle(final Order order) {
        final Message request = super.createNewOrderSingle(order);
        request.setField(new TransactTime());
        return request;
      }

      @Override
      public Message createReplaceRequest(final Order order, final Order newOrder) {
        final Message request = super.createReplaceRequest(order, newOrder);
        request.setField(new TransactTime());
        return request;
      }

      @Override
      public Message createCancelRequest(final Order order) {
        final Message request = super.createCancelRequest(order);
        request.setField(new TransactTime());
        return request;
      }
    };

    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX40, fix40);
    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX41, fix41);
    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX42, fix42);
    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX43, fix42);
    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX44, fix42);
    this.fixMessageBuilders.put(FixVersions.BEGINSTRING_FIXT11, fix42);
  }

  public FixMessageBuilder getFixMessageBuilder(final String beginString) {
    return this.fixMessageBuilders.get(beginString);
  }
}
