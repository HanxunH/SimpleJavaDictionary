/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

public interface OrcaDictionary {
    public void add(String word, String meaning);

    public void update(String word, String meaning);

    public boolean delete(String word);

    public String lookUp(String word);

    public void save();
}
