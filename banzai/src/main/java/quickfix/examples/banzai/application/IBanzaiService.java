package quickfix.examples.banzai.application;

import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventListener;

public interface IBanzaiService {

  void send(Order order);

  void cancel(Order order);

  void replace(Order order, Order newOrder);

  void addOrderEventListener(OrderEventListener listener);
}
