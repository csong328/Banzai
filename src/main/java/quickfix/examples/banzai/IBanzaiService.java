package quickfix.examples.banzai;

public interface IBanzaiService {
  void send(Order order);

  void cancel(Order order);

  void replace(Order order, Order newOrder);
}
