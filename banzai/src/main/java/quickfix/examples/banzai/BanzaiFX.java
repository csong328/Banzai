package quickfix.examples.banzai;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileInputStream;
import java.io.InputStream;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quickfix.FileStoreFactory;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.examples.banzai.application.ApplicationConfig;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

import static com.google.common.base.Preconditions.checkNotNull;

public class BanzaiFX extends javafx.application.Application {
  private static final Logger logger = LoggerFactory.getLogger(BanzaiFX.class);

  private AnnotationConfigApplicationContext applicationContext;

  private static String[] parameters;

  private boolean initiatorStarted = false;
  private Initiator initiator;

  @Override
  public void init() throws Exception {
    super.init();
    this.applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

    final SessionSettings settings = getSessionSettings(parameters);
    final MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
    final boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));
    final LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);

    final quickfix.Application application = this.applicationContext.getBean(quickfix.Application.class);
    final MessageFactory messageFactory = this.applicationContext.getBean(MessageFactory.class);

    this.initiator =
            new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);

    final JmxExporter exporter = new JmxExporter();
    exporter.register(this.initiator);
  }

  private SessionSettings getSessionSettings(final String[] args) throws Exception {
    try (InputStream inputStream = args.length == 0
            ? BanzaiFX.class.getResourceAsStream("banzai.cfg")
            : new FileInputStream(args[0])) {

      checkNotNull(inputStream, "usage: %s [configFile].", BanzaiFX.class.getName());
      return new SessionSettings(inputStream);
    }
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final Parent root = SpringFXMLLoader.create().applicationContext(this.applicationContext)
            .location(getClass().getResource("ui/banzai.fxml")).load();

    primaryStage.setTitle("BanzaiFX");
    final Scene scene = new Scene(root, 800, 400);
    primaryStage.setScene(scene);
    primaryStage.show();

    logon();
  }

  @Override
  public void stop() {
    logout();
    if (this.applicationContext != null) {
      this.applicationContext.close();
    }
  }

  private synchronized void logon() {
    if (!this.initiatorStarted) {
      try {
        this.initiator.start();
        this.initiatorStarted = true;
      } catch (final Exception e) {
        logger.error("Logon failed", e);
      }
    } else {
      for (final SessionID sessionId : this.initiator.getSessions()) {
        Session.lookupSession(sessionId).logon();
      }
    }
  }

  private void logout() {
    for (final SessionID sessionId : this.initiator.getSessions()) {
      Session.lookupSession(sessionId).logout("user requested");
    }
  }

  public static void main(final String[] args) {
    parameters = args;
    launch(args);
  }
}
