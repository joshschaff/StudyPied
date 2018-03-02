package GUI.Elements;



import DriveAPI.GeneralTerm;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides an organized control of a comboBox and a button, and abstracts their action methods.
 * This class just saves a few lines, since this type of Control is reused both for SchoolMemberForm and in ClassForm
 * Created by josh on 6/9/17.
 */
public class ListManager extends HBox {

    public ComboBox getComboBox() {
        return comboBox;
    }


    public Button getButton() {
        return button;
    }

    public CheckBox getCheckBox() { return checkBox; }


    private ComboBox comboBox;
    private Button button;
    private boolean showButton;
    private CheckBox checkBox;


    private ListManager(List options) {
        showButton = false;
        setAlignment(Pos.CENTER);
        comboBox = new ComboBox(FXCollections.observableArrayList());


        updateOptions(options);
        setHgrow(comboBox, Priority.ALWAYS);

        FxUtilTest.autoCompleteComboBoxPlus(comboBox, (typedText, itemToCompare) ->
                ((GeneralTerm) itemToCompare).toString().toLowerCase().contains(typedText.toLowerCase()));


        getChildren().addAll(comboBox);
    }

    public ListManager(List options, String buttonText) {
        this(options);
        showButton = true;
        button = new Button(buttonText);
        checkBox = new CheckBox("Term Complete");
        checkBox.setIndeterminate(false);
        this.getChildren().addAll(button, checkBox);

    }

    public void updateOptions(List options) {

        comboBox.setItems(FXCollections.observableArrayList(options));
        //comboBox.getSelectionModel().select(0);


        FxUtilTest.autoCompleteComboBoxPlus(comboBox, (typedText, itemToCompare) ->
                ((GeneralTerm) itemToCompare).toString().toLowerCase().contains(typedText.toLowerCase()));
    }

    public void setButtonAction(EventHandler<ActionEvent> eventHandler) {
        button.setOnAction(eventHandler);
    }

    public void setSelectAction(EventHandler<ActionEvent> eventHandler) {
        comboBox.setOnAction(eventHandler);
    }

    public void setCheckAction(EventHandler<ActionEvent> eventHandler) { checkBox.setOnAction(eventHandler);}

    public boolean isButtonShown() {
        return showButton;
    }
}