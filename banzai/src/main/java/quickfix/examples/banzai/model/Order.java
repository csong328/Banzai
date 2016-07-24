package quickfix.examples.banzai.model;

import quickfix.SessionID;

public interface Order {
  SessionID getSessionID();

  void setSessionID(SessionID sessionID);

  String getSymbol();

  void setSymbol(String symbol);

  int getQuantity();

  void setQuantity(int quantity);

  int getOpen();

  void setOpen(int open);

  int getExecuted();

  void setExecuted(int executed);

  OrderSide getSide();

  void setSide(OrderSide side);

  OrderType getType();

  void setType(OrderType type);

  OrderTIF getTIF();

  void setTIF(OrderTIF tif);

  Double getLimit();

  void setLimit(Double limit);

  void setLimit(String limit);

  Double getStop();

  void setStop(Double stop);

  void setAvgPx(double avgPx);

  double getAvgPx();

  void setRejected(boolean rejected);

  boolean getRejected();

  void setCanceled(boolean canceled);

  boolean getCanceled();

  void setNew(boolean isNew);

  boolean isNew();

  void setMessage(String message);

  String getMessage();

  String getOrderID();

  void setOrderID(String orderID);

  void setID(String ID);

  String getID();

  void setOriginalID(String originalID);

  String getOriginalID();

  void copy(Order other);
}
