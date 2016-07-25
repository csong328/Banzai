package quickfix.examples.exchange.simulator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.examples.exchange.OMS;
import quickfix.field.Symbol;

@Component("sampleScriptFactory")
public class SampleScriptFactory implements ScriptFactory {

  @Autowired
  private ActionableOMS actionableOMS;

  private OMS defaultScript;
  private final Map<String, OMS> config = new HashMap<>();

  @PostConstruct
  public void init() {
    this.defaultScript = new Script1(this.actionableOMS);
    this.config.put("MSFT", this.defaultScript);
    this.config.put("CSCO", new Script2(this.actionableOMS));
    this.config.put("IBM", new Script3(this.actionableOMS));
  }

  @Override
  public OMS getHandlingInstructions(final Message message, final ActionableOMS actionableOMS) {
    try {
      final String symbol = message.getString(Symbol.FIELD);
      final OMS script = this.config.get(symbol);

      return script != null ? script : this.defaultScript;
    } catch (final FieldNotFound ex) {
      return this.defaultScript;
    }
  }

}
