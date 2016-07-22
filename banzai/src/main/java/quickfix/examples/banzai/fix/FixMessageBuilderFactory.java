package quickfix.examples.banzai.fix;

import java.util.HashMap;
import java.util.Map;

import quickfix.FixVersions;
import quickfix.MessageFactory;

public class FixMessageBuilderFactory {
	private Map<String, FixMessageBuilder> fixMessageBuilders = new HashMap<String, FixMessageBuilder>();

	public FixMessageBuilderFactory(MessageFactory messageFactory) {
		super();
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX40,
				new Fix40MessageBuilder(messageFactory));
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX41,
				new Fix41MessageBuilder(messageFactory));
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX42,
				new Fix42MessageBuilder(messageFactory));
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX43,
				new Fix43MessageBuilder(messageFactory));
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIX44,
				new Fix44MessageBuilder(messageFactory));
		fixMessageBuilders.put(FixVersions.BEGINSTRING_FIXT11,
				new Fix50MessageBuilder(messageFactory));
	}

	public FixMessageBuilder getFixMessageBuilder(String beginString) {
		return fixMessageBuilders.get(beginString);
	}
}
