import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // setup - change all these values!
        int fragType = 1; // 0 - hash, 1 - value
        List<int[]> valRanges = (fragType == 1) ? generateValRanges() : null;
        List<Query> workload = generateWorkload();
        int numNodes = 1;
        int numFrags = 5; // only needed/used for hash fragmentation type (for value frag type, number of fragments is determined by the value ranges list)
        List<int[]> db = generateDB();

        // create fragments, nodes, and an index
        Map<Integer, List<Fragment>> index = new HashMap<>();
        List<Fragment> fragments = (fragType == 1) ? createFragments(valRanges, db, index) : createFragments(db, numFrags);
        List<Node> nodes = createNodes(numNodes, fragments);

        runThroughWorkload(workload, index, nodes, fragType, fragments);
    }

    /*
        Runs through the sample workload and prints out,
        for every query, for every node, the amount of networks and scans it had to do
     */
    public static void runThroughWorkload(List<Query> workload, Map<Integer, List<Fragment>> index, List<Node> nodes, int fragType, List<Fragment> frags) {
        for (Query query : workload) {
            List<Fragment> relevantFrags = (fragType == 1)? query.getFragments(index) : frags;

            for (Fragment frag : relevantFrags) {
                for (Node node : nodes) {
                    if (node.getFragments().contains(frag)) {
                        int[] results = query.matches(frag);
                        node.addScans(results[0]);
                        node.addNetworks(results[1]);
                    }
                }
            }

//            System.out.println("Query " + query);
            for (Node node : nodes) {
                System.out.println(node);
                node.resetScanNetwork();
            }
//            System.out.println("========");
        }
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

        for (int i = 0; i < numFrags; i++) {
            fragments.add(new Fragment());
        }

        int attr;
        Fragment tempFrag;
        for (int[] row : db) {
            attr = row[0];
            tempFrag = fragments.get((attr+row[1])%numFrags);
            tempFrag.addRow(row);
        }

        return fragments;
    }

    /*
        Generates a sample workload with example queries - right now, just a list of "WHERE VALUE = *"s
     */
    public static List<Query> generateWorkload() {
        Random rand = new Random();
        List<Query> workload = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            int start = rand.nextInt(17) + 2000;
            int end = rand.ints(start, (2016 + 1)).findFirst().getAsInt();
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
        valRanges.add(new int[]{2000, 2002});
        valRanges.add(new int[]{2002, 2004});
        valRanges.add(new int[]{2004, 2010});
        valRanges.add(new int[]{2010, 2017});

        return valRanges;
    }

    /*
        Generates a random database with *numEntries* rows and 2 attributes
     */
    public static List<int[]> generateDB() {
        int numEntries = 10000;
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

