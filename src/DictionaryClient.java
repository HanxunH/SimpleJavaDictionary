/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DictionaryClient extends Application {
    /* Initialization */
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();
    private static String defaultAddress = "localhost";
    private static int defaultPort = 6666;
    public static final String version_String = "V 1.02";
    public static final String author_String = "HanxunHuang(LemonBear)\n" + "https://github.com/HanxunHuangLemonBear";
    private static int deleyMillSec = 0;
    private static boolean isTesting = false;

    public static String getDefaultAddress() {
        return defaultAddress;
    }

    public static void setDefaultAddress(String defaultAddress) {
        DictionaryClient.defaultAddress = defaultAddress;
    }

    public static int getDefaultPort() {
        return defaultPort;
    }

    public static void setDefaultPort(int defaultPort) {
        DictionaryClient.defaultPort = defaultPort;
    }

    public class ServerResponse {
        private DictionaryOperation op = null;
        private String op_word = null;
        private String op_word_meaning = null;
        private String operation_status = null;
        private int response_code = 0;
        private boolean response = false;
        private String error_message = null;

        public String getError_message() {
            return error_message;
        }

        public DictionaryOperation getOp() {
            return op;
        }

        public String getOp_word() {
            return op_word;
        }

        public String getOp_word_meaning() {
            return op_word_meaning;
        }

        public String getOperation_status() {
            return operation_status;
        }

        public int getResponse_code() {
            return response_code;
        }

        public boolean isResponse() {
            return response;
        }

        public ServerResponse(JSONObject js) {
            if (js.has("response")) {
                try {
                    response = js.getBoolean("response");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("response_code")) {
                try {
                    response_code = js.getInt("response_code");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("operation")) {
                try {
                    op = DictionaryOperation.getType(js.getString("operation"));
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("op_word")) {
                try {
                    op_word = js.getString("op_word");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("op_word_meaning")) {
                try {
                    op_word_meaning = js.getString("op_word_meaning");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("operation_status")) {
                try {
                    operation_status = js.getString("operation_status");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
            if (js.has("error_message")) {
                try {
                    error_message = js.getString("error_message");
                } catch (Exception e) {
                    loger.severe(e.getMessage());
                }
            }
        }

        public void printObject() {
            System.out.print("response: " + response + "\n");
            System.out.print("response_code: " + response_code + "\n");
            System.out.print("operation_status: " + operation_status + "\n");
            System.out.print("operation: " + op + "\n");
            System.out.print("op_word: " + op_word + "\n");
            System.out.print("op_word_meaning: " + op_word_meaning + "\n");
        }

    }

    public ServerResponse OrcaClient(Map<String, String> map) throws Exception {
        String address = defaultAddress;
        int port = defaultPort;
        loger.info("OrcaClient Started, Server Address: " + address + " Port: " + String.valueOf(port));
        try {
            /* Connect with Server */
            Socket socket = new Socket(address, port);
            loger.info(address + ":" + String.valueOf(port) + " - " + "Server Connected");

            /* Json */
            JSONObject json = new JSONObject(map);
            String jsonString = json.toString();
            loger.info(jsonString);
            if (isTesting) {
                Thread.currentThread().sleep(deleyMillSec);
            }
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
            return sr;
        } catch (Exception e) {
            loger.severe(e.getClass().getSimpleName() + ": " + e.getMessage());
            throw e;
        }
    }


    public static ServerResponse clientOperationHandler(DictionaryOperation op, String op_word, String op_word_meaning) throws Exception {
        DictionaryClient client = new DictionaryClient();
        Map<String, String> map = new HashMap<String, String>();
        /* Add Operation */
        if (op == DictionaryOperation.ADD) {
            if (op_word == null && op_word_meaning == null) {
                loger.severe("Not enough arguments for Add operation");
            } else {
                map.put("operation", "ADD");
                map.put("op_word", op_word);
                map.put("op_word_meaning", op_word_meaning);
            }
        }
        /* DELETE Operation */
        else if (op == DictionaryOperation.DELETE) {
            if (op_word == null) {
                loger.severe("Not enough arguments for DELETE operation");
            } else {
                map.put("operation", "DELETE");
                map.put("op_word", op_word);
            }
        }
        /* LOOPUP Operation */
        else if (op == DictionaryOperation.LOOKUP) {
            if (op_word == null) {
                loger.severe("Not enough arguments for LOOKUP operation");
            } else {
                map.put("operation", "LOOKUP");
                map.put("op_word", op_word);
            }
        }
        /* UPDATE Operation */
        else if (op == DictionaryOperation.UPDATE) {
            if (op_word == null && op_word_meaning == null) {
                loger.severe("Not enough arguments for UPDATE operation");
            } else {
                map.put("operation", "UPDATE");
                map.put("op_word", op_word);
                map.put("op_word_meaning", op_word_meaning);
            }
        }
        /* error Test Operation */
        else {
            map.put("operation", "test");
        }
        try {
            return client.OrcaClient(map);
        } catch (Exception e) {
            throw e;
        }
    }

    public static boolean checkNextArgumentStatus(String[] args, int i) {
        if (i + 1 >= args.length || args[i + 1] == null) {
            loger.severe("Bad argument, Check Manual!");
            System.exit(0);
            return false;
        }
        return true;
    }

    public static void setClientBaseOnConfig(String filePath) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(filePath));
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("server_address")) {
                defaultAddress = (String) jsonObject.get("server_address");
            }
            if (jsonObject.has("server_port")) {
                defaultPort = jsonObject.getInt("server_port");
            }
        } catch (Exception e) {
            loger.severe(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String address = defaultAddress;
        int port = defaultPort;
        DictionaryOperation op = null;
        String op_word = null;
        String op_word_meaning = null;
        boolean isGUI = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-test")) {
                if (checkNextArgumentStatus(args, i)) {
                    try {
                        isTesting = true;
                        deleyMillSec = Integer.valueOf(args[i + 1]);
                    } catch (Exception e) {
                        loger.severe(e.getClass().getSimpleName() + ": " + e.getMessage());
                        loger.severe("Bad argument, Check Manual!");
                    }
                }
            }
            if (args[i].equals("-a")) {
                if (checkNextArgumentStatus(args, i)) {
                    defaultAddress = args[i + 1];
                }
            }
            if (args[i].equals("-p")) {
                if (checkNextArgumentStatus(args, i)) {
                    try {
                        defaultPort = Integer.parseInt(args[i + 1]);
                    } catch (Exception e) {
                        loger.severe(e.getClass().getSimpleName() + ": " + e.getMessage());
                        loger.severe("Bad argument, Check Manual!");
                    }
                }
            }
            if (args[i].equals("-config")) {
                if (checkNextArgumentStatus(args, i)) {
                    setClientBaseOnConfig(args[i + 1]);
                }
            }
            if (args[i].equals("-o")) {
                if (checkNextArgumentStatus(args, i)) {
                    String arg_operation = args[i + 1];
                    if (arg_operation.equals("add")) {
                        op = DictionaryOperation.ADD;
                    } else if (arg_operation.equals("delete")) {
                        op = DictionaryOperation.DELETE;
                    } else if (arg_operation.equals("lookup")) {
                        op = DictionaryOperation.LOOKUP;
                    } else if (arg_operation.equals("update")) {
                        op = DictionaryOperation.UPDATE;
                    } else {
                        loger.severe("Bad argument, Check Manual!");
                        System.exit(0);
                    }
                    if (checkNextArgumentStatus(args, i + 1)) {
                        op_word = args[i + 2];
                        if (i + 3 < args.length && args[i + 3] != null) {
                            op_word_meaning = args[i + 3];
                        }
                    }
                }
            }
            if (args[i].equals("-gui")) {
                isGUI = true;
                launch(args);
            }
        }
        if (!isGUI) {
            try {
                ServerResponse sr = clientOperationHandler(op, op_word, op_word_meaning);
                if (sr.getResponse_code() != 200) {
                    loger.warning("Status: " + sr.operation_status + " -Message: " + sr.getError_message());
                } else {
                    loger.info("Status: " + sr.operation_status + " -Operation: " + DictionaryOperation.getString(op) + " -Word: " + sr.getOp_word() + " -Meaning: " + sr.getOp_word_meaning());
                }
            } catch (Exception e) {
                loger.severe(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        System.exit(0);
    }

    /* JAVAFX */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DictionaryClient.fxml"));
        primaryStage.setTitle("DictionaryClient");
        primaryStage.setScene(new Scene(root, 600, 365));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


}
