import java.sql.Time;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        /* INITIAL VARIABLES TO CHANGE */

        // NODE
        int nodeCost = 5; // cost per hour
        int nodeDisk = 5; // disk space (in mb)
        int numNodes = 5; // number of nodes

        // DB
        int DBrows = 10; // rows in database
        int key = 0; // key to use for value fragmentation
        List<String> attrs = Arrays.asList("ID", "Name", "Address", "Phone Number"); // attributes

        // FRAGMENTS
        int fragAmount = 5; // amount of unique fragments to create


        /* INITIALIZE EVERYTHING */

        Database DB = new Database(attrs, DBrows);
        List<Fragment> frags = createFragments(DB, fragAmount, key);
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(nodeCost, nodeDisk));
        }
        Broker broker = new Broker(frags, nodes);


        /* START SENDING QUERIES */
        Random rand = new Random();

        // TODO: send queries randomly
        sendQuery(broker);

        // price for query
        // fragments required for query
        // reads (# of tuples) required for query
    }

    // TODO: doesn't work
    public static LinkedList<Fragment> createFragments(Database DB, int fragAmount, int key) {
        DB.sort(key);


        List<Fragment> frags = new LinkedList<>();




        return null;
    }

    public static void sendQuery(Broker broker) {
        Query newQuery = new Query();
    }
}
