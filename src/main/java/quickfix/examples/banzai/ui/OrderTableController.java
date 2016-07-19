package quickfix.examples.banzai.ui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.examples.banzai.Model;
import quickfix.examples.banzai.Order;

import java.net.URL;
import java.util.ResourceBundle;

@Component("orderTableController")
public class OrderTableController implements Initializable {
    @FXML
    private TableView<Order> orderTable;

    @Autowired
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.orderTable.setItems(model.getOrderList());
    }

    public void onOrderSelected(Event event) {
        Order order = this.orderTable.getSelectionModel().getSelectedItem();
        model.setSelectedOrder(order);
    }

}
