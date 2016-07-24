package quickfix.examples.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.examples.utility.FixApplicationAdapter;

@Component("exchangeApplication")
public class ExchangeApplication extends FixApplicationAdapter {
  private final static Logger logger = LoggerFactory.getLogger(ExchangeApplication.class);

  @Autowired
  private ExchangeOMS exchangeOMS;

  private Set<String> validOrderTypes;

  public void setValidOrderTypes(final Set<String> validOrderTypes) {
    this.validOrderTypes = validOrderTypes;
  }

  public void onCreate(final SessionID sessionID) {
    Session.lookupSession(sessionID).getLog()
            .onEvent("Valid order types: " + this.validOrderTypes);
  }

  @Override
  protected void onNewOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    this.exchangeOMS.onNewOrder(order, sessionID);
  }

  @Override
  protected void onCanceleOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    this.exchangeOMS.onCanceleOrder(order, sessionID);
  }

  @Override
  protected void onReplaceOrder(final Message order, final SessionID sessionID) throws FieldNotFound {
    this.exchangeOMS.onReplaceOrder(order, sessionID);
  }


  @Override
  protected void onCancelReject(final Message message, final SessionID sessionId) throws FieldNotFound {
    this.exchangeOMS.onCancelReject(message, sessionId);
  }

  @Override
  protected void onExecutionReport(final Message message, final SessionID sessionId) {
    this.exchangeOMS.onExecutionReport(message, sessionId);
  }
}
