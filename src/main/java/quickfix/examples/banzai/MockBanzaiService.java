package quickfix.examples.banzai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockBanzaiService implements IBanzaiService {
  private static final Logger logger = LoggerFactory.getLogger(MockBanzaiService.class);

  private Model model;

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
