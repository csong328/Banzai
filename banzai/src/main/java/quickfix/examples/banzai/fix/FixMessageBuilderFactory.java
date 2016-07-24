package quickfix.examples.banzai.fix;

public interface FixMessageBuilderFactory {

  FixMessageBuilder getFixMessageBuilder(final String beginString);
}
