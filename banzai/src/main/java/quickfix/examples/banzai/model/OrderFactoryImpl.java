package quickfix.examples.banzai.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import quickfix.examples.utility.IdGenerator;

@Component("orderFactory")
public class OrderFactoryImpl implements OrderFactory {
  @Autowired
  @Qualifier("orderIdGenerator")
  private IdGenerator orderIdGenerator;

  @Override
  public Order newOrder() {
    final Order order = new OrderImpl();
    order.setID(this.orderIdGenerator.nextID());
    return order;
  }
}
