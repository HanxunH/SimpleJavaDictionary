import com.sun.security.ntlm.Server;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.*;

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

    @FXML
    public void menuItem_exit_Action(ActionEvent event){
        System.exit(0);
    }


    @FXML
    public void clear_Button_Action(ActionEvent event){
        clearUI();
    }

    @FXML
    public void test_connection_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.TEST;
        set_operation_words();
        client.clientOperationHandler(op,op_word,op_word_meaning);
    }

    @FXML
    public void add_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.ADD;
        set_operation_words();
        DictionaryClient.ServerResponse sr = null;
        if(op_word!=null && !op_word.equals("") && op_word_meaning!=null){
            sr = client.clientOperationHandler(op,op_word,op_word_meaning);
            clearUI();
        }else{

        }
    }

    @FXML
    public void delete_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.DELETE;
        set_operation_words();
        DictionaryClient.ServerResponse sr = null;
        if(op_word!=null){
            sr = client.clientOperationHandler(op,op_word,op_word_meaning);
            updateUIBaseOnServerResponse(sr);
        }else{

        }
    }

    @FXML
    public void search_Button_Action(ActionEvent event){
        op = OrcaDictionary.dictionaryOperation.LOOKUP;
        set_operation_words();
        DictionaryClient.ServerResponse sr = null;
        if(op_word!=null){
            sr = client.clientOperationHandler(op,op_word,op_word_meaning);
            updateUIBaseOnServerResponse(sr);
        }else{

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
            text = "Status: " + sr.getOperation_status() + "\n";
        }
        text = text + "ResponseCode: " + sr.getResponse_code() + "\n";
        if(sr.getOp() != null){
            text = text + "Operation: " + OrcaDictionary.dictionaryOperation.getString(sr.getOp()) + "\n";
        }
        if(sr.getOp_word() != null){
            text = text + "Word: " + sr.getOp_word() + "\n";
        }
        if(sr.getOp_word_meaning() != null){
            text = text + "Word Meaning: " + sr.getOp_word_meaning() + "\n";
        }
        this.response_TextArea.appendText(text);
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


}
