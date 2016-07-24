package quickfix.examples.banzai.ui.event;

public interface OrderEventSource {
  void addOrderEventListener(OrderEventListener listener);
}