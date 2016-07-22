package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.MessageFactory;

public class Fix50MessageBuilder extends Fix42MessageBuilder {

  public Fix50MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIXT11);
  }

}
