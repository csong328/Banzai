package quickfix.examples.exchange;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

public interface ExchangeOMS {

  void onNewOrder(Message order, SessionID sessionID) throws FieldNotFound;

  void onCanceleOrder(Message order, SessionID sessionID) throws FieldNotFound;

  void onReplaceOrder(Message order, SessionID sessionID) throws FieldNotFound;

  void onCancelReject(Message message, SessionID sessionId);

  void onExecutionReport(Message message, SessionID sessionId);
}
