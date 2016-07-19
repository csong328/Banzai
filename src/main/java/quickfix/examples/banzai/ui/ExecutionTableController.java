package quickfix.examples.banzai.ui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import quickfix.examples.banzai.Execution;
import quickfix.examples.banzai.Model;

import java.net.URL;
import java.util.ResourceBundle;

@Component("executionTableController")
public class ExecutionTableController implements Initializable {
    @FXML
    private TableView<Execution> executionTable;

    @Autowired
    private Model model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.executionTable.setItems(model.getExecutionList());
    }

    public void onExecutionSelected(Event event) {
    }
}
