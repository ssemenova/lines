import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // setup - change all these values!
        List<Query> workload = generateWorkload(1000);




        int[] dbSizes = {1000, 5000, 10000};
//        int[] dbSizes = {5000, 10000, 15000};

        for (int j = 0; j <= 1; j++) {
            System.out.println(j);

            for (int i = 0; i < dbSizes.length; i++) {
                List<int[]> db = generateDB(dbSizes[i]);



//                int numNodes = nodeSizes[i];
                    int numNodes = 3;
                    System.out.print(dbSizes[i] + ", ");
                    int numFrags = 3; // only needed/used for hash fragmentation type (for value frag type, number of fragments is determined by the value ranges list)


                    int fragType = j; // 0 - hash, 1 - value
                    List<int[]> valRanges = (fragType == 1) ? generateValRanges() : null;

                    // create fragments, nodes, and an index
                    Map<Integer, List<Fragment>> index = new HashMap<>();
                    List<Fragment> fragments = (fragType == 1) ? createFragments(valRanges, db, index) : createFragments(db, numFrags);
                    List<Node> nodes = createNodes(numNodes, fragments);

                    Random rand = new Random();

                    runThroughWorkload(workload, index, nodes, fragType, fragments, numNodes);

            }
        }

    }

    /*
        Runs through the sample workload and prints out,
        for every query, for every node, the amount of networks and scans it had to do
     */
    public static void runThroughWorkload(List<Query> workload, Map<Integer, List<Fragment>> index, List<Node> nodes, int fragType, List<Fragment> frags, int numNodes) {
        Random rand = new Random();
        Node initiator = nodes.get(rand.nextInt(numNodes));
        int networks = 0; //the final amount of networks is the sum of all the networks
        List<Integer> scans; //the final amount of scans is the smallest scan performed
        List<Integer> finalScans = new LinkedList<>();
        List<Integer> finalNetworks = new LinkedList<>();
        Map<Node, int[]> nodeToResults = new HashMap<>();
        for (Query query : workload) {
            List<Fragment> relevantFrags = (fragType == 1)? query.getFragments(index) : frags; // no index for hash fragmentation
            networks = 0;
            scans = new LinkedList<>();

            for (Fragment frag : relevantFrags) {
                for (Node node : nodes) {
                    if (node.getFragments().contains(frag)) {
                        int[] results = query.matches(frag, fragType);
                        node.addScans(results[0]);
                        if (!node.equals(initiator)) {
                            node.addNetworks(results[1]);
                        }

                    }
                }
            }

            for (Node node : nodes) {
                //get networks and scans
                networks += node.getNetworks();
//                System.out.println(node.getScans());
                scans.add(node.getScans());

                //used to see the amount of scans and networks for each particular node
                int[] prevResults = nodeToResults.get(node);
                if (prevResults==null) {
                    nodeToResults.put(node, new int[]{node.getNetworks(), node.getScans(), 1});
                }
                else {
                    if (node.getScans() > prevResults[0]) {
                        nodeToResults.replace(node, new int[]{node.getScans(), node.getNetworks() + prevResults[1], 1 + prevResults[2]});
                    }
                    else {
                        nodeToResults.replace(node, new int[]{prevResults[0], node.getNetworks() + prevResults[1], 1 + prevResults[2]});
                    }
                }

                node.resetScanNetwork();
            }

            Collections.sort(scans);

            finalScans.add(scans.get(scans.size()-1));
            finalNetworks.add(networks);
        }
        System.out.print(finalScans.stream().mapToInt(val -> val).average().getAsDouble() + ", ");
        System.out.println(finalNetworks.stream().mapToInt(val -> val).average().getAsDouble());

//        for (Node node : nodeToResults.keySet()) {
//            System.out.println("--");
//            System.out.println("Scans = " + nodeToResults.get(node)[0]);
//            System.out.println("Networks = " + nodeToResults.get(node)[1]/nodeToResults.get(node)[2]);
//
//        }


    }

    /*
        Creates an "index" that maps values to the fragment they can be found in
     */
    public static void addNewIndex(Map<Integer, List<Fragment>> index, int key, Fragment frag) {
        if (index.containsKey(key)) {
            List<Fragment> oldList = index.get(key);
            oldList.add(frag);
            index.replace(key, oldList);
        } else {
            List<Fragment> newList = new LinkedList<>();
            newList.add(frag);
            index.put(key, newList);
        }
    }

    /*
        Creates nodes
     */
    public static List<Node> createNodes(int numNodes, List<Fragment> fragments) {
        List<Node> nodes = new LinkedList<>();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node());
        }

        // assigns fragments to nodes
        for (int i = 0; i < fragments.size(); i++) {
            nodes.get(i%numNodes).addFragment(fragments.get(i));
        }

        return nodes;
    }

    /*
        Creates fragments if fragmentation type is value
     */
    public static List<Fragment> createFragments(List<int[]> valRanges, List<int[]> db, Map<Integer, List<Fragment>> index) {
        List<Fragment> fragments = new LinkedList<>();

        // value fragmentation - will fragment based on a specified list of value ranges
        int rangeStart, rangeEnd;

        // for every range of values, create a new fragment object, add matching rows to it,
        // and generate the index for those rows
        for (int[] range : valRanges) {
            rangeStart = range[0];
            rangeEnd = range[1];
            Fragment tempFrag = new Fragment();
            int currAttr = db.get(0)[0];
            Boolean newAttr = true;

            for (int[] row : db) {
                if (row[0] >= rangeStart && row[0] < rangeEnd) {
                    if (newAttr) {
                        currAttr = row[0];
                        addNewIndex(index, currAttr, tempFrag);
                    }
                    tempFrag.addRow(row);
                }
                newAttr = (currAttr != row[0]);
            }
            fragments.add(tempFrag);
        }

        return fragments;
    }

    /*
        Creates fragments if fragmentation type is hash
     */
    public static List<Fragment> createFragments(List<int[]> db, int numFrags) {
        List<Fragment> fragments = new LinkedList<>();
        Fragment tempFrag;

        for (int i = 0; i < numFrags; i++) {
            fragments.add(new Fragment());
        }

        for (int[] row : db) {
            tempFrag = fragments.get((row[1]+row[0])%numFrags);
            tempFrag.addRow(row);
        }

        return fragments;
    }

    /*
        Generates a sample workload with example queries - right now, just a list of "WHERE VALUE = *"s
     */
    public static List<Query> generateWorkload(int selectivity) {
        Random rand = new Random();
        Random rg = new Random();
        List<Query> workload = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            int mid = rand.nextInt(17)+2000;
            int Y = (int) Math.floor(Math.abs(rg.nextGaussian()*selectivity));
            int start = Math.max(mid - Y, 2000);
            int end = Math.min(mid + Y, 2020);
            int extra = rand.nextInt(5)+1;
            workload.add(new DoubleValueQuery(start, end, extra));
        }

        return workload;
    }

    /*
        If value-based fragmentation, a list of value ranges to fragment on
     */
    public static List<int[]> generateValRanges() {
        List<int[]> valRanges = new LinkedList<>();

        // change this!
        // ranges for "Year" from [x, y)
        valRanges.add(new int[]{2000, 2006});
        valRanges.add(new int[]{2006, 2012});
        valRanges.add(new int[]{2012, 2020});

        return valRanges;
    }

    /*
        Generates a random database with *numEntries* rows and 2 attributes
     */
    public static List<int[]> generateDB(int numEntries) {
        List<int[]> db = new LinkedList<>(); // Year, OtherAttribute
        Random rand = new Random();

        // values for "Year" range from 2000 to 2016
        // values for "OtherAttribute" from 1 to 999
        // feel free to change this too
        for (int i = 0; i < numEntries; i++) {
            db.add(new int[]{rand.nextInt(17) + 2000, rand.nextInt(5)+1});
        }

        //sort by value "Year"
        Collections.sort(db, (o1, o2) -> {
            if (o1[0] > o2[0])
                return 1;
            else if (o1[0] < o2[0])
                return -1;
            else
                return 0;
        });

        return db;
    }
}

