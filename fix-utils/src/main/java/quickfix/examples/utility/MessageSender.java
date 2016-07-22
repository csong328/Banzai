package quickfix.examples.utility;

import quickfix.Message;
import quickfix.SessionID;

public interface MessageSender {
	void sendMessage(Message message);
	void sendMessage(Message message, SessionID sessionID);
}
