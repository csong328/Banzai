package quickfix.examples.banzai.application;

import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.ui.event.OrderEventSource;

public interface IBanzaiService extends OrderEventSource {

  void send(Order order);

  void cancel(Order order);

  void replace(Order order, Order newOrder);
}
