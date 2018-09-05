/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class DictionaryServerGUIController {
    @FXML
    private TextField port_number_TextField;
    @FXML
    private TextArea server_console_TextArea;
    @FXML
    private Label user_Counter_Label;
    @FXML
    private Button start_Button;
    @FXML
    private Button stop_Button;
    @FXML
    private MenuItem menuItem_exit;
    @FXML
    private MenuItem menuItem_version;

    private DictionaryServer server = new DictionaryServer();
    private int default_port = 6666;
    private OrcaServerThread serverThread = new OrcaServerThread();
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();
    private static boolean isStarted = false;
    private String logFileString = "server.log";
    private static boolean isTailLogStarted = false;
    private static OrcaServerLogReaderThread orcaServerLogReaderThread = null;

    @FXML
    public void menuItem_exit_Action(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void menuItem_version_Action(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "OrcaDictionaryServer", server.version_String, server.author_String);
    }

    @FXML
    public void start_Button_Action(ActionEvent event) {
        if (isStarted) {
            showAlert(AlertType.ERROR, "OrcaDictionaryServer", "Server Already Running", "Stop Server first to start new one.");
            return;
        }
        if (port_number_TextField.getText().equals("")) {
            showAlert(AlertType.INFORMATION, "OrcaDictionaryServer", "Port Number Not Set", "Running on default port: 6666");
        } else {
            setPortNumber();
        }
        serverThread = new OrcaServerThread();
        serverThread.start();
        isStarted = true;
        if (isTailLogStarted == false) {
            orcaServerLogReaderThread = new OrcaServerLogReaderThread();
            orcaServerLogReaderThread.start();
            Task task = new Task<Void>() {
                @Override
                public Void call() throws Exception {
                    while (true) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                setUserCounter();
                            }
                        });
                        Thread.sleep(1000);
                    }
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
            isTailLogStarted = true;
        }
    }

    @FXML
    public void stop_Button_Action(ActionEvent event) {
        if (!isStarted) {
            showAlert(AlertType.ERROR, "OrcaDictionaryServer", "Server Already Stoped", "");
            return;
        }
        serverThread.shutdown();
        isStarted = false;
    }


    private class OrcaServerLogReaderThread extends Thread {
        private final AtomicBoolean running = new AtomicBoolean(true);

        public void shutdown() {
            Thread.currentThread().interrupt();
            running.set(false);
        }

        public void run() {
            running.set(true);
            try {
                String line;
                File file = new File(logFileString);
                while (running.get() && file.exists() == false) {
                    Thread.currentThread().sleep(100);
                }
                FileReader fr = new FileReader(logFileString);
                BufferedReader br = new BufferedReader(fr);
                while (running.get()) {
                    line = br.readLine();
                    if (line == null) {
                        Thread.currentThread().sleep(300);
                    } else {
                        line = line + "\n";
                        server_console_TextArea.appendText(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class OrcaServerThread extends Thread {

        private ServerSocket serverSocket = null;
        private final AtomicBoolean running = new AtomicBoolean(true);

        public OrcaServerThread() {
            serverSocket = server.getServerSocket();
        }

        public void shutdown() {
            Thread.currentThread().interrupt();
            running.set(false);
            try {
                if (server != null) {
                    serverSocket.close();
                }
            } catch (IOException ignored) {

            }
        }

        class MyFormatter extends Formatter {

            /* (non-Javadoc)
             * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
             */
            @Override
            public String format(LogRecord record) {
                DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(record.getMillis());
                StringBuilder sb = new StringBuilder();
                sb.append("[" + formatter.format(calendar.getTime()) + "]");
                sb.append("[" + record.getLevel() + "]").append(" :");
                sb.append(record.getMessage()).append('\n');
                return sb.toString();
            }
        }

        public void run() {
            try {
                File file = new File(logFileString);
                file.deleteOnExit();
                FileHandler fh = new FileHandler(logFileString, true);
                fh.setFormatter(new MyFormatter());
                loger.addHandler(fh);
                loger.setUseParentHandlers(true);
            } catch (Exception e) {

            }
            try {
                serverSocket = new ServerSocket(default_port);
                loger.info("Server started on port: " + String.valueOf(default_port));
                running.set(true);
                while (running.get()) {
                    Socket socket = serverSocket.accept();
                    loger.info("Client ID: " + String.valueOf(server.getUserCounter()) + " accepted");
                    server.new OrcaServerRequestHandlerThread(socket, server.getUserCounter());
                    server.updateUserCounter(server.getUserCounter() + 1);
                }
            } catch (Exception e) {
                loger.severe(e.getMessage());
                loger.info("Server Shut Down...");
                return;
            }
            loger.info("Server Shut Down...");
        }

    }

    private void showAlert(AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setPortNumber() {
        this.default_port = Integer.valueOf(port_number_TextField.getText());
    }

    private void setUserCounter() {
        this.user_Counter_Label.setText(String.valueOf(server.getUserCounter()));
    }
}
