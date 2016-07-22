package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.MessageFactory;

public class Fix44MessageBuilder extends Fix42MessageBuilder {

  public Fix44MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIX44);
  }

}
