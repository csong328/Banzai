package quickfix.examples.banzai.fix;

import quickfix.FixVersions;
import quickfix.MessageFactory;


public class Fix41MessageBuilder extends AbstractFixMessageBuilder {

  public Fix41MessageBuilder(MessageFactory messageFactory) {
    super(messageFactory, FixVersions.BEGINSTRING_FIX41);
  }
}
