import java.io.*;
import java.net.*;
import javax.net.*;
import java.util.logging.*;
import org.json.*;
import java.util.*;

public class DictionaryServer{

    // Default Number
    private static int defaultPort = 6666;
    private static int userCounter = 0;
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();
    private static ServerSocket serverSocket = null;
    private static DictionaryServer server = new DictionaryServer();
    private static OrcaDictionary dictionary = new OrcaDictionary();
    private static final Object syncObject = new Object();

    public static synchronized void updateUserCounter(int counter){
        userCounter = counter;
    }

    public void OrcaServer(int port){
        try
        {
            serverSocket = new ServerSocket(port);
            loger.info("Server started");
            while(true) {
                Socket socket = serverSocket.accept();
                loger.info("Client ID: " + String.valueOf(userCounter) +" accepted");
                new OrcaServerRequestHandlerThread(socket,userCounter);
                updateUserCounter(userCounter+1);
            }
        }
        catch(IOException i) {
            loger.severe(i.getMessage());
        }catch (Exception e){
            loger.severe(e.getMessage());
        }
    }

    private class OrcaServerRequestHandlerThread implements Runnable {
        private Socket socket;
        private int clientID;

        public OrcaServerRequestHandlerThread(Socket socket, int clientID) {
            this.socket = socket;
            this.clientID = clientID;
            new Thread(this).start();
        }

        public void run() {
            OrcaServerRequestHandler(socket,clientID);
        }

        public void OrcaDictionaryHandler(Map<String, String> map, OrcaDictionary.dictionaryOperation op, String op_word, String op_word_meaning){
            if(op == OrcaDictionary.dictionaryOperation.ADD){

                synchronized(dictionary)
                {
                    dictionary.add(op_word,op_word_meaning);
                    map.put("operation","ADD");
                    map.put("operation_status","success");
                    map.put("op_word",op_word);
                    map.put("op_word_meaning",op_word_meaning);
                    dictionary.saveToFile();
                }

            }else if(op == OrcaDictionary.dictionaryOperation.DELETE){

                synchronized(dictionary)
                {
                    dictionary.delete(op_word);
                    map.put("operation","DELETE");
                    map.put("op_word",op_word);
                    map.put("operation_status","success");
                    dictionary.saveToFile();
                }

            }else if(op == OrcaDictionary.dictionaryOperation.LOOKUP){

                String meaning = dictionary.lookUp(op_word);
                map.put("operation","LOOKUP");
                map.put("operation_status","success");
                map.put("op_word",op_word);
                map.put("op_word_meaning",meaning);

            }else if(op == OrcaDictionary.dictionaryOperation.UPDATE){

                synchronized(dictionary)
                {
                    dictionary.add(op_word,op_word_meaning);
                    map.put("operation","UPDATE");
                    map.put("operation_status","success");
                    map.put("op_word",op_word);
                    map.put("op_word_meaning",op_word_meaning);
                    dictionary.saveToFile();
                }

            }else if(op == OrcaDictionary.dictionaryOperation.TEST){
                map.put("operation","TEST");
                map.put("operation_status","success");
            }

        }

        public void OrcaServerRequestHandler(Socket socket, int ClientID){
            try{
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                byte[] by = new byte[2048];
                int n;
                while((n=inputStream.read(by))!=-1){
                    data.write(by,0,n);
                }
                String strInputstream = new String(data.toByteArray());
                socket.shutdownInput();
                data.close();

                /* JSON Parsing */
                JSONObject json = new JSONObject(strInputstream);
                String op_word = null;
                String op_word_meaning = null;
                if(json.has("op_word")){
                    op_word = json.getString("op_word");
                }
                if(json.has("op_word_meaning")){
                    op_word_meaning = json.getString("op_word_meaning");
                }
                String operation = json.getString("operation");
                OrcaDictionary.dictionaryOperation op = null;
                op = OrcaDictionary.dictionaryOperation.getType(operation);
                Map<String, String> return_map = new HashMap<String, String>();
                return_map.put("response","true");
                return_map.put("response_code","200");
                this.OrcaDictionaryHandler(return_map,op,op_word,op_word_meaning);
                json = new JSONObject(return_map);
                String jsonString = json.toString();
                loger.info("Responding to ClientID("+Integer.toString(clientID) + ") JSON: " + jsonString);

                /* Reply Client */
                DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream (socket.getOutputStream()));
                outputStream.writeUTF(jsonString);
                outputStream.flush();
                outputStream.close();

            }catch (Exception e) {
                loger.severe(e.getMessage());
            }finally {
                loger.info("ClientID("+clientID+") Close Client");
                updateUserCounter(userCounter-1);
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        loger.severe("Error when try to close socket: " + e.getMessage());
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        loger.info("Starting the Server ...");
        server.OrcaServer(defaultPort);
    }



}