package quickfix.examples.utility;

import quickfix.ApplicationAdapter;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.MessageCracker;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class CrackedApplicationAdapter extends ApplicationAdapter {
	private MessageCracker messageCracker = new MessageCracker(this);
	
	public void fromApp(quickfix.Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            UnsupportedMessageType {
		messageCracker.crack(message, sessionID);
	}
}
