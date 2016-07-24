package quickfix.examples.banzai.ui;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.model.Order;

public interface OrderTableModel<T extends Order> {

  ObservableList<T> getOrderList();

  void addOrder(T order);

  void replaceOrder(T newOrder);

  void clear();
}