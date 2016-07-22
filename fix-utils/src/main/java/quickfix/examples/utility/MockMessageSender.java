package quickfix.examples.utility;

import java.util.ArrayList;
import java.util.List;

import quickfix.Message;
import quickfix.SessionID;

public class MockMessageSender implements MessageSender {
	private List<Message> messages = new ArrayList<Message>();

	public List<Message> fetchAndEmpty() {
		List<Message> result = messages;
		messages = new ArrayList<Message>();
		return result;
	}

	public void sendMessage(Message message) {
		sendMessage(message, null);
	}

	public void sendMessage(Message message, SessionID sessionID) {
		this.messages.add(message);
	}
}
