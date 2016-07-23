package quickfix.examples.banzai.ui.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleOrderEventSource implements OrderEventSource {

  private List<OrderEventListener> eventListenerList = new CopyOnWriteArrayList<>();

  @Override
  public void addOrderEventListener(OrderEventListener listener) {
    eventListenerList.add(listener);
  }

  public void notify(OrderEvent event) {
    eventListenerList.forEach(l -> l.handle(event));
  }
}
