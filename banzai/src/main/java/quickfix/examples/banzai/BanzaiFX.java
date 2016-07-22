package quickfix.examples.banzai;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileInputStream;
import java.io.InputStream;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quickfix.DefaultMessageFactory;
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
import quickfix.examples.banzai.application.BanzaiApplication;
import quickfix.examples.banzai.ui.OrderEntryController;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

public class BanzaiFX extends Application {
  private static final Logger logger = LoggerFactory.getLogger(BanzaiFX.class);

  private AnnotationConfigApplicationContext applicationContext;

  private static String[] parameters;

  private boolean initiatorStarted = false;
  private Initiator initiator;

  @Override
  public void init() throws Exception {
    super.init();
    this.applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    quickfix.Application application = applicationContext.getBean(quickfix.Application.class);

    OrderEntryController orderEntryController =
            applicationContext.getBean(OrderEntryController.class);
    BanzaiApplication banzaiApplication = applicationContext.getBean(BanzaiApplication.class);
    banzaiApplication.addLogonObserver(orderEntryController);

    SessionSettings settings = getSessionSettings(parameters);
    boolean logHeartbeats = Boolean.valueOf(System.getProperty("logHeartbeats", "true"));
    MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
    LogFactory logFactory = new ScreenLogFactory(true, true, true, logHeartbeats);
    MessageFactory messageFactory = new DefaultMessageFactory();

    this.initiator =
            new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);

    JmxExporter exporter = new JmxExporter();
    exporter.register(initiator);
  }

  private SessionSettings getSessionSettings(String[] args) throws Exception {
    try (InputStream inputStream = args.length == 0
            ? BanzaiFX.class.getResourceAsStream("banzai.cfg")
            : new FileInputStream(args[0])) {
      if (inputStream == null) {
        throw new RuntimeException(
                String.format("usage: %s [configFile].", BanzaiFX.class.getName()));
      }
      return new SessionSettings(inputStream);
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = SpringFXMLLoader.create().applicationContext(applicationContext)
            .location(getClass().getResource("ui/banzai.fxml")).load();

    primaryStage.setTitle("BanzaiFX");
    Scene scene = new Scene(root, 800, 400);
    primaryStage.setScene(scene);
    primaryStage.show();

    logon();
  }

  @Override
  public void stop() {
    logout();
    if (applicationContext != null) {
      applicationContext.close();
    }
  }

  private synchronized void logon() {
    if (!initiatorStarted) {
      try {
        initiator.start();
        initiatorStarted = true;
      } catch (Exception e) {
        logger.error("Logon failed", e);
      }
    } else {
      for (SessionID sessionId : initiator.getSessions()) {
        Session.lookupSession(sessionId).logon();
      }
    }
  }

  private void logout() {
    for (SessionID sessionId : initiator.getSessions()) {
      Session.lookupSession(sessionId).logout("user requested");
    }
  }

  public static void main(String[] args) {
    parameters = args;
    launch(args);
  }
}
