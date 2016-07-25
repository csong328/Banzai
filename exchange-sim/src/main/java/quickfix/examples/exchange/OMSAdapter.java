package quickfix.examples.exchange;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

public class OMSAdapter implements OMS {
  @Override
  public void onNewOrder(final Message request, final SessionID sessionID) throws FieldNotFound {

  }

  @Override
  public void onCanceleOrder(final Message request, final SessionID sessionID) throws FieldNotFound {

  }

  @Override
  public void onReplaceOrder(final Message request, final SessionID sessionID) throws FieldNotFound {

  }

  @Override
  public void onCancelReject(final Message message, final SessionID sessionId) throws FieldNotFound {

  }

  @Override
  public void onExecutionReport(final Message message, final SessionID sessionId) throws FieldNotFound {

  }
}
