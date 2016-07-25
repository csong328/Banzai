package quickfix.examples.utility;

import java.util.ArrayList;
import java.util.List;

import quickfix.Message;
import quickfix.SessionID;

public class MockMessageSender implements MessageSender {
  private List<Message> messages = new ArrayList<>();

  public List<Message> fetchAndEmpty() {
    final List<Message> result = this.messages;
    this.messages = new ArrayList<>();
    return result;
  }

  public void sendMessage(final Message message, final SessionID sessionID) {
    this.messages.add(message);
  }
}
