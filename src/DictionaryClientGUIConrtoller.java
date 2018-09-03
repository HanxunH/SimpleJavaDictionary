
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.*;
import tray.animations.AnimationType;
import tray.notification.*;

import java.util.Optional;


public class DictionaryClientGUIConrtoller {

    @FXML
    private TextField op_word_TextField;
    @FXML
    private TextArea op_word_meaning_TextArea;
    @FXML
    private TextArea response_TextArea;
    @FXML
    private MenuItem menuItem_exit;
    @FXML
    private MenuItem menuItem_version;
    @FXML
    private Button search_Button;
    @FXML
    private Button delete_Button;
    @FXML
    private Button add_Button;
    @FXML
    private Button test_connection_Button;
    @FXML
    private Button clear_Button;

    private DictionaryClient client = new DictionaryClient();
    private OrcaDictionary.dictionaryOperation op = null;
    private String op_word = null;
    private String op_word_meaning = null;
    private DictionaryClient.ServerResponse sr = null;

    @FXML
    public void menuItem_exit_Action(ActionEvent event){
        System.exit(0);
    }

    @FXML
    public void menuItem_version_Action(ActionEvent event){
        showAlert(AlertType.INFORMATION,"OrcaDictionaryClient", client.version_String,client.author_String);
    }


    @FXML
    public void clear_Button_Action(ActionEvent event){
        clearUI();
    }

    @FXML
    public void test_connection_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.TEST;
        set_operation_words();
        try{
            sr = client.clientOperationHandler(op,op_word,op_word_meaning);
            showAlert(AlertType.INFORMATION,"OrcaDictionaryClient","Server Connected","Success!");
        }catch (Exception e){
            showAlert(AlertType.ERROR,"OrcaDictionaryClient","Server Error",e.getMessage());
        }
    }

    @FXML
    public void add_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.ADD;
        set_operation_words();
        if(op_word!=null && op_word.equals("")==false && op_word_meaning!=null && op_word_meaning.equals("")==false){
            try{
                sr = client.clientOperationHandler(op,op_word,op_word_meaning);
            }catch (Exception e){
                showAlert(AlertType.ERROR,"OrcaDictionaryClient","Server Error",e.getMessage());
            }

        }else{
            showAlert(AlertType.WARNING,"OrcaDictionaryClient","TextFiled and TextArea are Empty","ADD: Please input a word and its meaning.");
        }
    }

    @FXML
    public void delete_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.DELETE;
        set_operation_words();
        if(op_word!=null&& !op_word.equals("")){
            try{
                sr = client.clientOperationHandler(op,op_word,op_word_meaning);
                updateUIBaseOnServerResponse(sr);
            }catch (Exception e){
                showAlert(AlertType.ERROR,"OrcaDictionaryClient","Server Error",e.getMessage());
            }
        }else{
            showAlert(AlertType.WARNING,"OrcaDictionaryClient","TextFiled is Empty","DELETE: Please input a word.");
        }
    }

    @FXML
    public void search_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.LOOKUP;
        set_operation_words();
        DictionaryClient.ServerResponse sr = null;
        if(op_word!=null && !op_word.equals("")){
            try{
                sr = client.clientOperationHandler(op,op_word,op_word_meaning);
                updateUIBaseOnServerResponse(sr);
            }catch (Exception e){
                showAlert(AlertType.ERROR,"OrcaDictionaryClient","Server Error",e.getMessage());
            }
        }else{
            showAlert(AlertType.WARNING,"OrcaDictionaryClient","TextFiled Filed is Empty","SEARCH: Please input a word.");
        }
    }

    private void updateUIBaseOnServerResponse(DictionaryClient.ServerResponse sr){
        this.op_word_TextField.clear();
        this.op_word_meaning_TextArea.clear();
        if(sr.getOp_word_meaning() != null){
            this.op_word_meaning_TextArea.appendText(sr.getOp_word_meaning());
        }
        if(sr.getOp_word() != null){
            this.op_word_TextField.appendText(sr.getOp_word());
        }
        set_response_TextArea(sr);
    }


    private void set_response_TextArea(DictionaryClient.ServerResponse sr){
        this.response_TextArea.clear();
        String text = "";
        if(sr.getOperation_status() != null){
            text = "Operation_Status: " + sr.getOperation_status() + "\n";
        }
        text = text + "Response_Code: " + sr.getResponse_code() + "\n";
        if(sr.getOp() != null){
            text = text + "Operation: " + OrcaDictionary.dictionaryOperation.getString(sr.getOp()) + "\n";
        }
        if(sr.getError_message() != null){
            text = text + "Error_Message: " + sr.getError_message() + "\n";
        }
        if(sr.getOp_word() != null){
            text = text + "Operation_Word: " + sr.getOp_word() + "\n";
        }
        if(sr.getOp_word_meaning() != null){
            text = text + "Word_Meaning: " + sr.getOp_word_meaning() + "\n";
        }
        this.response_TextArea.appendText(text);
        if(sr.getResponse_code() != 200){
            showAlert(AlertType.WARNING,"OrcaDictionaryClient",OrcaDictionary.dictionaryOperation.getString(sr.getOp()),sr.getError_message());
        }
    }

    private void set_operation_words(){
        this.op_word = op_word_TextField.getText();
        this.op_word_meaning = op_word_meaning_TextArea.getText();
    }

    private void clearUI(){
        this.op_word_meaning_TextArea.clear();
        this.op_word_TextField.clear();
        this.response_TextArea.clear();
    }

    private void showAlert(AlertType type, String title, String header, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
