package quickfix.examples.banzai.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import quickfix.DefaultMessageFactory;
import quickfix.MessageFactory;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.banzai.fix.FixMessageBuilderFactoryImpl;
import quickfix.examples.utility.DefaultMessageSender;
import quickfix.examples.utility.MessageSender;

@Configuration
@ComponentScan("quickfix.examples.banzai.application")
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
}