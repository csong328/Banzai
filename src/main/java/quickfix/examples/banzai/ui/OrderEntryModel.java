package quickfix.examples.banzai.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;
import quickfix.examples.banzai.*;

public interface OrderEntryModel {
  ObjectProperty<Order> selectedOrderProperty();

  Order getSelectedOrder();

  void setSelectedOrder(Order order);

  ObservableList<OrderSide> getSideList();

  ObservableList<OrderType> getTypeList();

  ObservableList<OrderTIF> getTIFList();

  ObservableList<SessionID> getSessionList();

  void logon(SessionID sessionID);

  void logoff(SessionID sessionID);

  ObservableList<Order> getOrderList();

  void addOrder(Order order);

  void addClOrdID(Order order, String id);

  Order getOrder(String ID);

  void updateOrder(Order order, String value);

  ObservableList<Execution> getExecutionList();

  void addExecution(Execution execution);

  Execution getExchangeExecution(String exchangeID);


}
