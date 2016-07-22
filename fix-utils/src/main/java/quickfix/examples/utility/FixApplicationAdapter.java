package quickfix.examples.utility;

import quickfix.ApplicationAdapter;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

public class FixApplicationAdapter extends ApplicationAdapter {
  @Override
  public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    String msgType = message.getHeader().getString(MsgType.FIELD);
    switch (msgType) {
      case "D":
        onNewOrder(message, sessionId);
        break;
      case "F":
        onCanceleOrder(message, sessionId);
        break;
      case "G":
        onReplaceOrder(message, sessionId);
        break;
      case "8":
        onExecutionReport(message, sessionId);
        break;
      case "9":
        onCancelReject(message, sessionId);
        break;
    }
  }

  protected void onNewOrder(Message message, SessionID sessionId) throws FieldNotFound {
  }

  protected void onCanceleOrder(Message message, SessionID sessionId) throws FieldNotFound {
  }

  protected void onReplaceOrder(Message message, SessionID sessionId) throws FieldNotFound {
  }

  protected void onCancelReject(Message message, SessionID sessionId) throws FieldNotFound {
  }

  protected void onExecutionReport(Message message, SessionID sessionId) throws FieldNotFound {
  }
}
