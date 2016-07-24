package quickfix.examples.utility;

import org.springframework.stereotype.Component;

@Component("idGeneratorFactory")
public class IdGeneratorFactory {

  public IdGenerator idGenerator() {
    return new SimpleIdGenerator();
  }

}
