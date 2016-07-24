package quickfix.examples.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleIdGenerator implements IdGenerator {
  private final String prefix;
  private final AtomicInteger idSeed = new AtomicInteger();

  public SimpleIdGenerator() {
    final DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    this.prefix = dateFormat.format(new Date()) + "/";
  }

  @Override
  public String nextID() {
    final int nextId = this.idSeed.incrementAndGet();
    return this.prefix + nextId;
  }
}
