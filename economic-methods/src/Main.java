import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        /* Changeable initial variables */
        // NODE
        int nodeCost;
        int nodeDisk;
        // CLIENT
        int QPW = 10;
        int QPWerror;
        // DB
        int DBrows = 100;
        List<String> attrs = new LinkedList<>(Arrays.asList("ID, Name, Address, Phone Number"));

        /* Initialize everything */
        Database DB = new Database(attrs, DBrows);
        List<Fragment> frags = createFragments(DB);
        Broker broker = new Broker(QPW, frags);


        // price for query
        // fragments required for query
        // reads (# of tuples) required for query
    }

    public static LinkedList<Fragment> createFragments(Database DB) {

        return null;
    }

    public static void sendQuery(int QPW, int QPWerror) {

    }
}
