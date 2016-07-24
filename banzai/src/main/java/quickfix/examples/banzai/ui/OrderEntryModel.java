package quickfix.examples.banzai.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.OrderTIF;
import quickfix.examples.banzai.OrderType;

public interface OrderEntryModel<T extends Order> {

  ObjectProperty<T> selectedOrderProperty();

  T getSelectedOrder();

  void setSelectedOrder(T order);

  ObservableList<OrderSide> getSideList();

  ObservableList<OrderType> getTypeList();

  ObservableList<OrderTIF> getTIFList();

  ObservableList<SessionID> getSessionList();
}
