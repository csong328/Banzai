package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.MessageFactory;


public class Fix43MessageBuilder extends Fix42MessageBuilder {

  public Fix43MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIX43);
  }

}
