package quickfix.examples.banzai.ui;

import javafx.collections.ObservableList;
import quickfix.examples.banzai.Order;

public interface OrderTableModel {

  ObservableList<Order> getOrderList();

  void addOrder(Order order);

  void replaceOrder(Order newOrder);

  void clear();
}
