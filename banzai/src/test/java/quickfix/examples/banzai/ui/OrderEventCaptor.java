package quickfix.examples.banzai.ui;

import quickfix.examples.banzai.ui.event.OrderEvent;
import quickfix.examples.banzai.ui.event.OrderEventListener;

public class OrderEventCaptor implements OrderEventListener {
  private OrderEvent event;

  @Override
  public void handle(final OrderEvent event) {
    this.event = event;
  }

  public OrderEvent getValue() {
    return this.event;
  }
}
