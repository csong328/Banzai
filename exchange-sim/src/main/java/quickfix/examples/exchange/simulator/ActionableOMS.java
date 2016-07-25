package quickfix.examples.exchange.simulator;

import quickfix.FieldNotFound;
import quickfix.examples.exchange.OMS;

public interface ActionableOMS extends OMS {

  void pendingAck(String clOrdID) throws FieldNotFound;

  void ack(String clOrdID) throws FieldNotFound;

  void reject(String clOrdID) throws FieldNotFound;

  void fill(String clOrdID, double lastShares, double lastPx) throws FieldNotFound;

  void pendingCancel(String clOrdID) throws FieldNotFound;

  void canceled(String clOrdID) throws FieldNotFound;

  void cancelReject(String clOrdID) throws FieldNotFound;

  void pendingReplace(String clOrdID) throws FieldNotFound;

  void replaced(String clOrdID) throws FieldNotFound;

  void replaceReject(String clOrdID) throws FieldNotFound;

}
