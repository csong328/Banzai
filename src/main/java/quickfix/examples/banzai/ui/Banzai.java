package quickfix.examples.banzai.ui;

import com.sun.javafx.sg.prism.NGShape;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.Model;
import quickfix.examples.banzai.Order;
import quickfix.examples.banzai.OrderSide;
import quickfix.examples.banzai.application.ApplicationConfig;
import quickfix.examples.banzai.utils.SpringFXMLLoader;

public class Banzai extends Application {

    private AnnotationConfigApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        super.init();
        applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = SpringFXMLLoader.create()
                .applicationContext(applicationContext)
                .location(getClass().getResource("banzai.fxml"))
                .load();

        primaryStage.setTitle("Banzai!");
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        Model model = applicationContext.getBean(Model.class);
        model.addOrder(sampleOrder());
        model.addExecution(sampleExecution());
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
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
        launch(args);
    }
}
