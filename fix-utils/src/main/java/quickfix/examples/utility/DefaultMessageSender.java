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

  public void sendMessage(final Message message, final SessionID sessionID) {
    try {
      if (sessionID == null) {
        Session.sendToTarget(message);
      } else {
        Session.sendToTarget(message, sessionID);
      }
    } catch (final SessionNotFound e) {
      log.error(e.getMessage(), e);
    }
  }
}
