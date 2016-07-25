package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import quickfix.DefaultMessageFactory;
import quickfix.MessageFactory;
import quickfix.examples.banzai.application.BanzaiEventAggregator;
import quickfix.examples.banzai.application.IMarketConnectivity;
import quickfix.examples.banzai.application.MarketConnectivityImpl;
import quickfix.examples.banzai.fix.FixMessageBuilderFactory;
import quickfix.examples.banzai.fix.FixMessageBuilderFactoryImpl;
import quickfix.examples.exchange.simulator.DirectOMSConnector;
import quickfix.examples.fix.builder.execution.ExecutionReportBuilderFactory;
import quickfix.examples.utility.MessageSender;

@Configuration
@ComponentScan("quickfix.examples.exchange.simulator")
public class OMSConfig {
  @Bean
  public FixMessageBuilderFactory fixMessageBuilderFactory() {
    return new FixMessageBuilderFactoryImpl();
  }

  @Bean
  public ExecutionReportBuilderFactory executionReportBuilderFactory() {
    return new ExecutionReportBuilderFactory();
  }

  @Bean
  public BanzaiEventAggregator banzaiEventAggregator() {
    return new BanzaiEventAggregator();
  }

  @Bean
  public IMarketConnectivity marketConnectivity() {
    return new MarketConnectivityImpl();
  }

  @Bean
  public MessageSender messageSender() {
    return new DirectOMSConnector();
  }

  @Bean
  public MessageFactory messageFactory() {
    return new DefaultMessageFactory();
  }

}
