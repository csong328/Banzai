package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import quickfix.examples.banzai.application.ModelConfig;
import quickfix.examples.banzai.application.UIControlConfig;
import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.IdGeneratorFactory;

@Configuration
@Import({UIControlConfig.class, ModelConfig.class})
public class TestConfig {

  @Bean(name = "orderIdGenerator")
  public IdGenerator orderIdGenerator() {
    return idGeneratorFactory().idGenerator();
  }

  @Bean(name = "execIdGenerator")
  public IdGenerator execIdGenerator() {
    return idGeneratorFactory().idGenerator();
  }

  private IdGeneratorFactory idGeneratorFactory() {
    return new IdGeneratorFactory();
  }
}
