package quickfix.examples.banzai.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.banzai.application.IMarketConnectivity;
import quickfix.examples.exchange.OMS;
import quickfix.examples.utility.MessageSender;
import quickfix.field.MsgType;

public class DirectCoupling implements MessageSender {
  @Autowired
  @Qualifier("actionableOMS")
  private OMS oms;

  @Autowired
  private IMarketConnectivity marketConnectivity;

  @Override
  public void sendMessage(final Message message, final SessionID sessionID) {
    try {
      final String msgType = message.getHeader().getString(MsgType.FIELD);

      switch (msgType) {
        /* Request to OMS */
        case MsgType.ORDER_SINGLE:
          this.oms.onNewOrder(message, sessionID);
          break;
        case MsgType.ORDER_CANCEL_REQUEST:
          this.oms.onCanceleOrder(message, sessionID);
          break;
        case MsgType.ORDER_CANCEL_REPLACE_REQUEST:
          this.oms.onReplaceOrder(message, sessionID);
          break;

        /* Respond from OMS */
        case MsgType.EXECUTION_REPORT:
          this.marketConnectivity.executionReport(message, sessionID);
          break;
        case MsgType.ORDER_CANCEL_REJECT:
          this.marketConnectivity.cancelReject(message, sessionID);
      }
    } catch (final FieldNotFound ex) {
      throw new RuntimeException(ex);
    }
  }
}
