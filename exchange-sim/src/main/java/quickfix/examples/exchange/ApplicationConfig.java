package quickfix.examples.exchange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import quickfix.DefaultMessageFactory;
import quickfix.MessageFactory;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilderFactory;
import quickfix.examples.utility.DefaultMessageSender;
import quickfix.examples.utility.IdGenerator;
import quickfix.examples.utility.IdGeneratorFactory;
import quickfix.examples.utility.MessageSender;

@Configuration
@ComponentScan("quickfix.examples.exchange")
public class ApplicationConfig {

  @Bean
  public ExecutionReportBuilderFactory executionReportBuilderFactory() {
    return new ExecutionReportBuilderFactory();
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
