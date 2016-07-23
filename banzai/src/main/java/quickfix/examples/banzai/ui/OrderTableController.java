package quickfix.examples.banzai.ui;

import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventListener;

public interface OrderTableController {

  void addOrderEventListener(OrderEventListener listener);

  void addOrder(Order order);

  void replaceOrder(Order order);

  void clear();
}
