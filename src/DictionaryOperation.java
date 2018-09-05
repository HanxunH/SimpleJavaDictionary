/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

public enum DictionaryOperation {
    ADD, DELETE, UPDATE, LOOKUP, TEST;

    public static DictionaryOperation getType(String operation) {
        if (operation.equals("ADD")) {
            return ADD;
        } else if (operation.equals("DELETE")) {
            return DELETE;
        } else if (operation.equals("LOOKUP")) {
            return LOOKUP;
        } else if (operation.equals("UPDATE")) {
            return UPDATE;
        } else {
            return TEST;
        }
    }

    public static String getString(DictionaryOperation op) {
        if (op == ADD) {
            return "ADD";
        } else if (op == DELETE) {
            return "DELETE";
        } else if (op == UPDATE) {
            return "UPDATE";
        } else if (op == LOOKUP) {
            return "LOOKUP";
        } else {
            return "TEST";
        }
    }
}