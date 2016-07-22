package quickfix.examples.banzai.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.examples.banzai.ui.OrderEntryModel;
import quickfix.examples.banzai.Order;

public class MockBanzaiService implements IBanzaiService {
  private static final Logger logger = LoggerFactory.getLogger(MockBanzaiService.class);

  private OrderEntryModel orderEntryModel;

  @Override
  public void send(Order order) {
    logger.info("Send order");
  }

  @Override
  public void cancel(Order order) {
    logger.info("Cancel order");
  }

  @Override
  public void replace(Order order, Order newOrder) {
    logger.info("Replace order");
  }
}
