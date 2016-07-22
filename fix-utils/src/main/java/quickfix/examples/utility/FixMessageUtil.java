package quickfix.examples.utility;

import quickfix.DataDictionary;
import quickfix.DataDictionaryProvider;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.DefaultMessageFactory;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageUtils;

/**
 * Parse text message into FIX message. For testing purpose only.
 */
public class FixMessageUtil {
	private static MessageFactory messageFactory = new DefaultMessageFactory();
	private static DataDictionaryProvider dataDictionaryProvider = new DefaultDataDictionaryProvider();

	private static final char FIX_SEPARATOR = '\001';

	public static Message parse(String messageStr, char delimiter)
			throws InvalidMessage {
		String normalizedStr = normalize(messageStr, delimiter);

		String beginString = getBeginString(normalizedStr);
		DataDictionary dataDictionary = dataDictionaryProvider
				.getSessionDataDictionary(beginString);

		if (dataDictionary != null) {
			return parse(messageFactory, dataDictionary, normalizedStr,
					beginString);
		} else {
			throw new InvalidMessage(beginString + " not supported");
		}
	}

	private static Message parse(MessageFactory messageFactory,
                                 DataDictionary dataDictionary, String messageString,
                                 String beginString) throws InvalidMessage {
		final String messageType = MessageUtils.getMessageType(messageString);
		final quickfix.Message message = messageFactory.create(beginString,
				messageType);
		message.fromString(messageString, dataDictionary, false);
		return message;
	}

	private static String normalize(String messageStr, char delimiter) {
		String normalized = messageStr;
		if (delimiter != FIX_SEPARATOR) {
			normalized = messageStr.replace(delimiter, FIX_SEPARATOR);
		}
		if (normalized.length() > 0
				&& normalized.charAt(normalized.length() - 1) != FIX_SEPARATOR) {
			normalized += FIX_SEPARATOR;
		}
		return normalized;
	}

	private static String getBeginString(String messageString)
			throws InvalidMessage {
		if (messageString.startsWith("8=")) {
			final int index = messageString.indexOf(FIX_SEPARATOR);
			if (index < 0) {
				throw new InvalidMessage(
						"Message does not contain any field separator");
			}
			return messageString.substring(2, index);
		} else {
			return "FIX.4.2";
		}
	}
}
