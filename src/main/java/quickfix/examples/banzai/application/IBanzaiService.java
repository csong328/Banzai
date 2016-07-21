package quickfix.examples.banzai.application;

import quickfix.examples.banzai.Order;

public interface IBanzaiService {

    void send(Order order);

    void cancel(Order order);

    void replace(Order order, Order newOrder);
}
