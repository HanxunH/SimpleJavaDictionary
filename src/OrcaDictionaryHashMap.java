/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class OrcaDictionaryHashMap implements OrcaDictionary {

    private HashMap<String, String> dictionary = new HashMap<String, String>();
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();

    public OrcaDictionaryHashMap() {
        try {
            File file = new File("orca.dictionary");
            if (file.exists()) {
                FileInputStream fis = new FileInputStream("orca.dictionary");
                ObjectInputStream ois = new ObjectInputStream(fis);
                dictionary = (HashMap) ois.readObject();
                ois.close();
                fis.close();
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            loger.severe(e.getMessage());
        }
    }

    public void add(String word, String meaning) {
        dictionary.put(word, meaning);
    }

    public boolean delete(String word) {
        if (dictionary.containsKey(word)) {
            dictionary.remove(word);
            return true;
        }
        return false;
    }

    public void update(String word, String meaning) {
        dictionary.put(word, meaning);
    }

    public String lookUp(String word) {
        if (dictionary.containsKey(word)) {
            return dictionary.get(word);
        }
        return null;
    }

    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream("orca.dictionary");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dictionary);
            oos.close();
            fos.close();
            loger.info("File Saved");
        } catch (IOException ioe) {
            loger.severe(ioe.getMessage());
        }
    }

}
