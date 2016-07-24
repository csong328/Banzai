package quickfix.examples.banzai.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import quickfix.DefaultMessageFactory;
import quickfix.MessageFactory;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.banzai.fix.FixMessageBuilderFactoryImpl;
import quickfix.examples.utility.DefaultMessageSender;
import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.IdGeneratorFactory;
import quickfix.examples.utility.MessageSender;

@Configuration
@ComponentScan("quickfix.examples.banzai")
public class ApplicationConfig {

  @Bean
  public FixMessageBuilderFactory fixMessageBuilderFactory() {
    return new FixMessageBuilderFactoryImpl();
  }

  @Bean
  public MessageSender messageSender() {
    return new DefaultMessageSender();
  }

  @Bean
  public MessageFactory messageFactory() {
    return new DefaultMessageFactory();
  }

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