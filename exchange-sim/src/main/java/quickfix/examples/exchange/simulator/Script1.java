package quickfix.examples.exchange.simulator;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.exchange.OMSAdapter;
import quickfix.field.ClOrdID;
import quickfix.field.OrderQty;

public class Script1 extends OMSAdapter {

  private final ActionableOMS actionableOMS;

  public Script1(final ActionableOMS actionableOMS) {
    this.actionableOMS = actionableOMS;
  }

  @Override
  public void onNewOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String clOrdID = request.getString(ClOrdID.FIELD);
    final double orderQty = request.getDouble(OrderQty.FIELD);
    this.actionableOMS.ack(clOrdID);
    this.actionableOMS.fill(clOrdID, orderQty / 10, 5.0);
  }

  @Override
  public void onCanceleOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String clOrdID = request.getString(ClOrdID.FIELD);
    this.actionableOMS.pendingCancel(clOrdID);
    this.actionableOMS.canceled(clOrdID);
  }

  @Override
  public void onReplaceOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String clOrdID = request.getString(ClOrdID.FIELD);
    this.actionableOMS.pendingReplace(clOrdID);
    this.actionableOMS.replaced(clOrdID);
  }
}
