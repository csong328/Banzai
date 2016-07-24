package quickfix.examples.banzai.ui.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleOrderEventSource implements OrderEventSource {

  private final List<OrderEventListener> eventListenerList = new CopyOnWriteArrayList<>();

  @Override
  public void addOrderEventListener(final OrderEventListener listener) {
    this.eventListenerList.add(listener);
  }

  public void notify(final OrderEvent event) {
    this.eventListenerList.forEach(l -> l.handle(event));
  }
}