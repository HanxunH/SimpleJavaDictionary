import java.util.*;
import java.io.*;
import java.util.logging.Logger;

public interface OrcaDictionary {
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

    public void add(String word, String meaning);
    public void update(String word, String meaning);
    public boolean delete(String word);
    public String lookUp(String word);
    public void save();
}


