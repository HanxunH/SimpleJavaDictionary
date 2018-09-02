import java.util.*;
import java.io.*;
import java.util.logging.Logger;


public class OrcaDictionary {
    public enum dictionaryOperation {
        ADD, DELETE, UPDATE, LOOKUP, TEST;

        public static dictionaryOperation getType(String operation){
            if(operation.equals("ADD")){
                return OrcaDictionary.dictionaryOperation.ADD;
            }else if (operation.equals("DELETE")){
                return OrcaDictionary.dictionaryOperation.DELETE;
            }else if (operation.equals("LOOKUP")){
                return OrcaDictionary.dictionaryOperation.LOOKUP;
            }else if (operation.equals("UPDATE")){
                return OrcaDictionary.dictionaryOperation.UPDATE;
            }else{
                return OrcaDictionary.dictionaryOperation.TEST;
            }
        }

        public static String getString(dictionaryOperation op){
            if(op==ADD){
                return "ADD";
            }else if(op==DELETE){
                return "DELETE";
            }else if(op==UPDATE){
                return "UPDATE";
            }else if(op==LOOKUP){
                return "LOOKUP";
            }else{
                return "TEST";
            }
        }
    }
    private HashMap<String, String> dictionary = new HashMap<String, String>();
    private static OrcaLoger logHandler = new OrcaLoger();
    private static Logger loger = logHandler.getLogger();

    public OrcaDictionary(){
        try
        {
            FileInputStream fis = new FileInputStream("orca.dictionary");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dictionary = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(Exception e) {
            loger.severe(e.getMessage());
        }
    }

    public void add(String word, String meaning){
        dictionary.put(word,meaning);
    }

    public void delete(String word){
        dictionary.remove(word);
    }

    public void update(String word, String meaning){
        dictionary.put(word,meaning);
    }

    public String lookUp(String word){
        return dictionary.get(word);
    }

    public void saveToFile(){
        try
        {
            FileOutputStream fos = new FileOutputStream("orca.dictionary");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dictionary);
            oos.close();
            fos.close();
            loger.info("File Saved");
        }catch(IOException ioe)
        {
            loger.severe(ioe.getMessage());
        }
    }

}

