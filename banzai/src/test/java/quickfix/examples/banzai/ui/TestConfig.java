package quickfix.examples.banzai.ui;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import quickfix.examples.banzai.application.ModelConfig;
import quickfix.examples.banzai.application.UIControlConfig;

@Configuration

@Import({UIControlConfig.class, ModelConfig.class, IdGeneratorConfig.class})
public class TestConfig {

}
