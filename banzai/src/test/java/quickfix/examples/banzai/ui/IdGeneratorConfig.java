package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.IdGeneratorFactory;

@Configuration
public class IdGeneratorConfig {

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
