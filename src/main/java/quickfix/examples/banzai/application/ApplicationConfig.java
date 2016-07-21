package quickfix.examples.banzai.application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("quickfix.examples.banzai")
public class ApplicationConfig {
  @Bean
  public Executor executor() {
    return Executors.newCachedThreadPool(r -> {
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
    });
  }
}
