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

        // FRAGMENTS
        int fragAmount = 5; // amount of unique fragments to create

        // QUERIES
        int price = 10; // TODO: not an int?

        // RUNNING
        int time = 5000;
        int selectivity = 10; // query selectivity
        int splitJoinFreq = 5; // how many time units to wait until splitting and joining


        /* INITIALIZE EVERYTHING */
        Broker broker = new Broker(createFragments(fragAmount), createNodes(numNodes, nodeCost, nodeDisk), DBrows);


        /* START SENDING QUERIES */
        Random rand = new Random();
        Random rg = new Random();
        int currentTime = 0;

        while (currentTime < time) {
            int mid = rand.nextInt(DBrows);
            int Y = (int) Math.floor(Math.abs(rg.nextGaussian()*selectivity));
            int start = Math.max(mid - Y, 0);
            int end = Math.min(mid + Y, DBrows);

            Query q = new Query(start, end, price);
            broker.processQuery(q);

            if (currentTime % splitJoinFreq == 0) {
                broker.evaluateFragments();
            }
            currentTime++;
        }
    }

    // TODO: doesn't work
    public static List<Fragment> createFragments(int fragAmount) {
        List<Fragment> frags = new LinkedList<>();

        return frags;
    }

    public static List<Node> createNodes(int numNodes, int nodeCost, int nodeDisk) {
        List<Node> nodes = new LinkedList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node(nodeCost, nodeDisk));
        }
        return nodes;
    }

    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }

}
