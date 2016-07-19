package quickfix.examples.banzai.ui;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.Order;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderTableController implements Initializable {
    @FXML
    private TableView<Order> orderTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.orderTable.setItems(FXCollections.observableArrayList(sampleOrder()));
    }

    public void onOrderSelected(Event event) {
        Order order = this.orderTable.getSelectionModel().getSelectedItem();
    }

    private Order sampleOrder() {
        Order order = new Order();
        order.setSymbol("MSFT");
        order.setQuantity(100);
        order.setOpen(order.getQuantity());
        return order;
    }
}
