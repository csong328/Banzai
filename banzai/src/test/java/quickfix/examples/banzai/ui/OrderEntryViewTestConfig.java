package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.examples.banzai.ui.impl.OrderEntryControllerImpl;
import quickfix.examples.banzai.ui.impl.OrderEntryModelImpl;
import quickfix.examples.banzai.ui.impl.OrderTableControllerImpl;
import quickfix.examples.banzai.ui.impl.OrderTableModelImpl;

@Configuration
public class OrderEntryViewTestConfig {
  @Bean
  public OrderEntryController orderEntryController() {
    return new OrderEntryControllerImpl();
  }

  @Bean
  public OrderEntryModel orderEntryModel() {
    return new OrderEntryModelImpl();
  }

  @Bean
  public OrderTableController orderTableController() {
    return new OrderTableControllerImpl();
  }

  @Bean
  public OrderTableModel orderTableModel() {
    return new OrderTableModelImpl();
  }
}
