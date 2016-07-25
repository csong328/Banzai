package quickfix.examples.exchange.simulator;

import org.springframework.stereotype.Component;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.examples.exchange.OMSAdapter;
import quickfix.field.ClOrdID;
import quickfix.field.OrderQty;

@Component("script2")
public class Script2 extends OMSAdapter {

  private final ActionableOMS actionableOMS;

  public Script2(final ActionableOMS actionableOMS) {
    this.actionableOMS = actionableOMS;
  }

  @Override
  public void onNewOrder(final Message request, final SessionID sessionID) throws FieldNotFound {
    final String clOrdID = request.getString(ClOrdID.FIELD);
    final double orderQty = request.getDouble(OrderQty.FIELD);
    this.actionableOMS.ack(clOrdID);
    this.actionableOMS.fill(clOrdID, orderQty, 5.0);
  }

}
