package quickfix.examples.banzai.ui;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.OrderSide;

import java.net.URL;
import java.util.ResourceBundle;

public class ExecutionTableController implements Initializable {
    @FXML
    private TableView<Execution> executionTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.executionTable.setItems(FXCollections.observableArrayList(sampleExecution()));
    }


    public void onExecutionSelected(Event event) {
        Execution execution = this.executionTable.getSelectionModel().getSelectedItem();
    }

    private Execution sampleExecution() {
        Execution execution = new Execution();
        execution.setSymbol("MSFT");
        execution.setQuantity(100);
        execution.setPrice(10.02);
        execution.setSide(OrderSide.BUY);
        return execution;
    }
}
