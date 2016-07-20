package quickfix.examples.banzai;

import java.io.FileInputStream;
import java.io.InputStream;

import org.quickfixj.jmx.JmxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quickfix.*;
import quickfix.examples.banzai.application.ApplicationConfig;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

public class BanzaiFX extends Application {
	private static final Logger logger = LoggerFactory
			.getLogger(BanzaiFX.class);

	private AnnotationConfigApplicationContext applicationContext;

	private static String[] parameters;

    private boolean initiatorStarted = false;
	private Initiator initiator;

	@Override
	public void init() throws Exception {
		super.init();
		this.applicationContext = new AnnotationConfigApplicationContext(
				ApplicationConfig.class);
		quickfix.Application application = applicationContext
				.getBean(quickfix.Application.class);

		SessionSettings settings = getSessionSettings(parameters);
		boolean logHeartbeats = Boolean
				.valueOf(System.getProperty("logHeartbeats", "true"));
		MessageStoreFactory messageStoreFactory = new FileStoreFactory(
				settings);
		LogFactory logFactory = new ScreenLogFactory(true, true, true,
				logHeartbeats);
		MessageFactory messageFactory = new DefaultMessageFactory();

		this.initiator = new SocketInitiator(application, messageStoreFactory,
				settings, logFactory, messageFactory);

		JmxExporter exporter = new JmxExporter();
		exporter.register(initiator);
	}

	private SessionSettings getSessionSettings(String[] args) throws Exception {
		try (InputStream inputStream = args.length == 0
				? BanzaiFX.class.getResourceAsStream("banzai.cfg")
				: new FileInputStream(args[0])) {
			if (inputStream == null) {
				throw new RuntimeException(String.format(
						"usage: %s [configFile].", BanzaiFX.class.getName()));
			}
			return new SessionSettings(inputStream);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = SpringFXMLLoader.create()
				.applicationContext(applicationContext)
				.location(getClass().getResource("ui/banzai.fxml")).load();

		primaryStage.setTitle("BanzaiFX");
		Scene scene = new Scene(root, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.show();

//		Model model = applicationContext.getBean(Model.class);
//		model.addOrder(sampleOrder());
//		model.addExecution(sampleExecution());

        logon();
	}

	@Override
	public void stop() {
        logout();
		if (applicationContext != null) {
			applicationContext.close();
		}
	}

	public synchronized void logon() {
		if (!initiatorStarted) {
			try {
				initiator.start();
				initiatorStarted = true;
			}
			catch (Exception e) {
				logger.error("Logon failed", e);
			}
		}
		else {
			for (SessionID sessionId : initiator.getSessions()) {
				Session.lookupSession(sessionId).logon();
			}
		}
	}

	public void logout() {
		for (SessionID sessionId : initiator.getSessions()) {
			Session.lookupSession(sessionId).logout("user requested");
		}
	}

	private Order sampleOrder() {
		Order order = new Order();
		order.setSymbol("MSFT");
		order.setQuantity(100);
		order.setOpen(order.getQuantity());
		return order;
	}

	private Execution sampleExecution() {
		Execution execution = new Execution();
		execution.setSymbol("MSFT");
		execution.setQuantity(100);
		execution.setPrice(10.02);
		execution.setSide(OrderSide.BUY);
		return execution;
	}

	public static void main(String[] args) {
		parameters = args;
		launch(args);
	}
}
