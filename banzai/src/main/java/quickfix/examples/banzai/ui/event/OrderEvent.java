package quickfix.examples.banzai.ui.event;

import quickfix.examples.banzai.Order;

public class OrderEvent {
  private final Order order;
  private final OrderEventType eventType;
  private final Object arg;

  public OrderEvent(Order order, OrderEventType eventType) {
    this(order, eventType, null);
  }

  public OrderEvent(Order order, OrderEventType eventType, Object arg) {
    this.order = order;
    this.eventType = eventType;
    this.arg = arg;
  }

  public Order getOrder() {
    return order;
  }

  public OrderEventType getEventType() {
    return eventType;
  }

  public Object getArg() {
    return arg;
  }
}
