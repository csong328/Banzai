package quickfix.examples.banzai.ui.event;

import quickfix.examples.banzai.model.Order;

public class OrderEvent {
  private final Order order;
  private final OrderEventType eventType;
  private final Object arg;

  public OrderEvent(final Order order, final OrderEventType eventType) {
    this(order, eventType, null);
  }

  public OrderEvent(final Order order, final OrderEventType eventType, final Object arg) {
    this.order = order;
    this.eventType = eventType;
    this.arg = arg;
  }

  public Order getOrder() {
    return this.order;
  }

  public OrderEventType getEventType() {
    return this.eventType;
  }

  public Object getArg() {
    return this.arg;
  }
}