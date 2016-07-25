package quickfix.examples.exchange;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

public interface OMS {

  void onNewOrder(Message request, SessionID sessionID) throws FieldNotFound;

  void onCanceleOrder(Message request, SessionID sessionID) throws FieldNotFound;

  void onReplaceOrder(Message request, SessionID sessionID) throws FieldNotFound;

  void onCancelReject(Message message, SessionID sessionId) throws FieldNotFound;

  void onExecutionReport(Message message, SessionID sessionId) throws FieldNotFound;
}
