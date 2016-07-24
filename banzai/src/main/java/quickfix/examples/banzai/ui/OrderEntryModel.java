package quickfix.examples.banzai.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import quickfix.SessionID;
import quickfix.examples.banzai.model.Order;
import quickfix.examples.banzai.model.OrderSide;
import quickfix.examples.banzai.model.OrderTIF;
import quickfix.examples.banzai.model.OrderType;

public interface OrderEntryModel<T extends Order> {

  ObjectProperty<T> selectedOrderProperty();

  T getSelectedOrder();

  void setSelectedOrder(T order);

  ObservableList<OrderSide> getSideList();

  ObservableList<OrderType> getTypeList();

  ObservableList<OrderTIF> getTIFList();

  ObservableList<SessionID> getSessionList();
}
