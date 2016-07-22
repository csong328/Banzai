package quickfix.examples.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

public class DefaultMessageSender implements MessageSender {
	private final static Logger log = LoggerFactory
			.getLogger(DefaultMessageSender.class);

	public void sendMessage(Message message) {
		sendMessage(message, null);
	}

	public void sendMessage(Message message, SessionID sessionID) {
		try {
			if (sessionID == null) {
				Session.sendToTarget(message);
			} else {
				Session.sendToTarget(message, sessionID);
			}
		} catch (SessionNotFound e) {
			log.error(e.getMessage(), e);
		}
	}
}
