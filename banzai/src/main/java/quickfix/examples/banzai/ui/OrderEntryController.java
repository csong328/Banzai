package quickfix.examples.banzai.ui;

import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventListener;

public interface OrderEntryController {

  void addOrderEventListener(OrderEventListener listener);

  void setSelectedOrder(Order order);
}
