package quickfix.examples.banzai.model;

public interface Execution {
  String getSymbol();

  void setSymbol(String symbol);

  int getQuantity();

  void setQuantity(int quantity);

  OrderSide getSide();

  void setSide(OrderSide side);

  double getPrice();

  void setPrice(double price);

  String getID();

  void setID(String ID);

  void setExchangeID(String exchangeID);

  String getExchangeID();
}
