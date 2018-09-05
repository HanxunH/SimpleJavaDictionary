/***
 * @project project1
 * @author HanxunHuang ON 9/4/18
 * COMP90015 Distributed Systems
 * Hanxun Huang
 * hanxunh@student.unimelb.edu.au
 * Student ID: 975781
 ***/

import java.util.logging.Level;
import java.util.logging.Logger;


public class OrcaLoger {
    private static Logger logger = Logger.getLogger("OrcaLoger");

    public OrcaLoger() {
        /* Set Logger*/
        logger.setLevel(Level.ALL);
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tY-%1$tm-%1$td-%1$tH:%1$tM:%1$tS]-[%4$s][%2$s]: %5$s%6$s%n");
    }

    public static Logger getLogger() {
        return logger;
    }

}
