import java.util.logging.*;
import org.json.*;
import java.io.*;
import java.net.*;
import javax.net.*;
import java.util.*;

public class DictionaryClient {
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();
    private static String defaultAddress = "localhost";
    private static int defaultPort = 6666;

    private class ServerResponse{
        private OrcaDictionary.dictionaryOperation op = null;
        private String op_word = null;
        private String op_word_meaning = null;
        private String operation_status = null;
        private int response_code = 0;
        private boolean response = false;

        public ServerResponse(JSONObject js){
            if(js.has("response")){
                try {
                    response = js.getBoolean("response");
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
            if(js.has("response_code")){
                try {
                    response_code = js.getInt("response_code");
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
            if(js.has("operation")){
                try {
                    op = OrcaDictionary.dictionaryOperation.getType(js.getString("operation"));
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
            if(js.has("op_word")){
                try {
                    op_word = js.getString("op_word");
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
            if(js.has("op_word_meaning")){
                try {
                    op_word_meaning = js.getString("op_word_meaning");
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
            if(js.has("operation_status")){
                try {
                    operation_status = js.getString("operation_status");
                }catch (Exception e){
                    loger.severe(e.getMessage());
                }
            }
        }

        public void printObject(){
            System.out.print("response: "+response+"\n");
            System.out.print("response_code: "+response_code+"\n");
            System.out.print("operation_status: "+operation_status + "\n");
            System.out.print("operation: "+op + "\n");
            System.out.print("op_word: "+op_word + "\n");
            System.out.print("op_word_meaning: "+op_word_meaning + "\n");
        }

    }

    public void OrcaClient(Map<String, String> map) {
        String address = defaultAddress;
        int port = defaultPort;
        try {
            /* Connect with Server */
            Socket socket = new Socket(address, port);
            loger.info(address+":"+String.valueOf(port)+" - "+"Server Connected");

            /* Json */
            JSONObject json = new JSONObject(map);
            String jsonString = json.toString();
            loger.info(jsonString);

            /* Send To Server*/
            byte[] jsonByte = jsonString.getBytes();
            DataOutputStream outputStream = null;
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(jsonByte);
            outputStream.flush();
            socket.shutdownOutput();

            /* Receive From Server */
            DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String strInputstream = inputStream.readUTF();
            JSONObject js = new JSONObject(strInputstream);
            ServerResponse sr = new ServerResponse(js);
            sr.printObject();
        }
        catch (Exception e) {
            loger.severe(e.getMessage());
        }
    }


    public static void clientOperationHandler(OrcaDictionary.dictionaryOperation op, String op_word, String op_word_meaning){
        DictionaryClient client = new DictionaryClient();
        Map<String, String> map = new HashMap<String, String>();
        /* Add Operation */
        if(op == OrcaDictionary.dictionaryOperation.ADD){
            if(op_word==null && op_word_meaning==null){
                loger.severe("Not enough arguments for Add operation");
            }else{
                map.put("operation","ADD");
                map.put("op_word", op_word);
                map.put("op_word_meaning", op_word_meaning);
                client.OrcaClient(map);
            }
        }
        /* DELETE Operation */
        else if(op== OrcaDictionary.dictionaryOperation.DELETE){
            if(op_word==null){
                loger.severe("Not enough arguments for DELETE operation");
            }else{
                map.put("operation","DELETE");
                map.put("op_word", op_word);
                client.OrcaClient(map);
            }
        }
        /* LOOPUP Operation */
        else if(op== OrcaDictionary.dictionaryOperation.LOOKUP){
            if(op_word==null){
                loger.severe("Not enough arguments for LOOKUP operation");
            }else{
                map.put("operation","LOOKUP");
                map.put("op_word", op_word);
                client.OrcaClient(map);
            }
        }
        /* UPDATE Operation */
        else if(op== OrcaDictionary.dictionaryOperation.UPDATE){
            if(op_word==null && op_word_meaning==null){
                loger.severe("Not enough arguments for UPDATE operation");
            }else{
                map.put("operation","UPDATE");
                map.put("op_word", op_word);
                map.put("op_word_meaning", op_word_meaning);
                client.OrcaClient(map);
            }
        }
        /* error Test Operation */
        else{
            map.put("operation","test");
            client.OrcaClient(map);
        }
    }

    public static boolean checkNextArgumentStatus(String[] args, int i){
        if(i+1<args.length && args[i+1] == null){
            loger.severe("Bad argument, Check Manual!");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String address = defaultAddress;
        int port = defaultPort;
        OrcaDictionary.dictionaryOperation op = null;
        String op_word = null;
        String op_word_meaning = null;
        for(int i=0; i<args.length; i++){
            if(args[i].equals("-a")){
                if(checkNextArgumentStatus(args,i)){
                    defaultAddress = args[i+1];
                }
            }
            if(args[i].equals("-p")){
                if(checkNextArgumentStatus(args,i)){
                    defaultPort = Integer.parseInt(args[i+1]);
                }
            }
            if(args[i].equals("-o")){
                if(checkNextArgumentStatus(args,i)) {
                    String arg_operation = args[i+1];
                    if(arg_operation.equals("add")){
                        op = OrcaDictionary.dictionaryOperation.ADD;
                    }else if (arg_operation.equals("delete")){
                        op = OrcaDictionary.dictionaryOperation.DELETE;
                    }else if (arg_operation.equals("lookup")){
                        op = OrcaDictionary.dictionaryOperation.LOOKUP;
                    }else if (arg_operation.equals("update")){
                        op = OrcaDictionary.dictionaryOperation.UPDATE;
                    }
                    if(checkNextArgumentStatus(args,i+1)) {
                        op_word = args[i+2];
                        if(i+3<args.length && args[i+3] != null){
                            op_word_meaning = args[i+3];
                        }
                    }
                }
            }
        }
        clientOperationHandler(op,op_word,op_word_meaning);
    }
}
