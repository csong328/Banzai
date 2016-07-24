package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.examples.banzai.ui.impl.OrderEntryControllerImpl;
import quickfix.examples.banzai.ui.impl.OrderEntryModelImpl;

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
}
