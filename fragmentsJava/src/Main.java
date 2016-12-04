import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // setup - change all these values!
        int fragType = 1; // 0 - hash, 1 - value
        List<int[]> valRanges = (fragType == 1) ? generateValRanges() : null;
        List<String> workload = generateWorkload();
        int numNodes = 2;
        int numFrags = 5; // only needed/used for hash fragmentation type (for value frag type, number of fragments is determined by the value ranges list)
        List<int[]> db = generateDB();

        // create fragments, nodes, and an index
        List<Fragment> fragments = (fragType == 1) ? createFragments(valRanges, db) : createFragments(db, numFrags);
        Map<Integer, List<Fragment>> index = createIndex(fragments);
        List<Node> nodes = createNodes(numNodes, fragments);

        runThroughWorkload(workload, index, nodes);
    }

    /*
        Runs through the sample workload and prints out,
        for every query, for every node, the amount of networks and scans it had to do
     */
    public static void runThroughWorkload(List<String> workload, Map<Integer, List<Fragment>> index, List<Node> nodes) {
        for (String query : workload) {
            Map<Integer, List<Fragment>> relevantFrags = processQuery(query, index);

            for (Integer key : relevantFrags.keySet()) {
                for (Fragment frag : relevantFrags.get(key)) {
                    for (Node node : nodes) {
                        if (node.getFragments().contains(frag)) {
                            node.doScanNetwork(frag, key);
                        }
                    }
                }
            }

            System.out.println("Query " + query);
            for (Node node : nodes) {
                System.out.println("-----");
                System.out.println(node);
                node.resetScanNetwork();
            }
            System.out.println("========");
        }
    }

    /*
        Parse query and return a map of values to relevant fragments
     */
    public static Map<Integer, List<Fragment>> processQuery(String query, Map<Integer, List<Fragment>> index) {
        Map<Integer, List<Fragment>> relevantFrags = new HashMap<>();
        int start, end;

        if (query.contains("-")) {
            String[] split = query.split("-");
            start = Integer.parseInt(split[0]);
            end = Integer.parseInt(split[1]);
        } else if (query.contains(">")) {
            start = Integer.parseInt(query.replace(">", ""));
            end = 2016;
        } else if (query.contains("<")) {
            start = 2000;
            end = Integer.parseInt(query.replace("<", ""));
        } else {
            start = Integer.parseInt(query);
            end = start;
        }

        for (int i = start; i <= end; i++) {
            relevantFrags.put(i, index.get(i));
        }

        return relevantFrags;
    }

    /*
        Creates an "index" that maps values to the fragment they can be found in
     */
    public static Map<Integer, List<Fragment>> createIndex(List<Fragment> fragments) {
        Map<Integer, List<Fragment>> index = new HashMap<>();

        for (Fragment frag : fragments) {
            for (int key : frag.getIndex().keySet()) {
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
        }

        return index;
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
    public static List<Fragment> createFragments(List<int[]> valRanges, List<int[]> db) {
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
            List<Integer> indexes = new LinkedList<>();

            for (int[] row : db) {
                if (row[0] >= rangeStart && row[0] < rangeEnd) {
                    if (newAttr) {
                        currAttr = row[0];
                        indexes.add(currAttr);
                    }
                    tempFrag.addRow(row);
                }
                newAttr = currAttr != row[0];
            }
            indexes.forEach(tempFrag::addIndex);
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

        for (Fragment frag : fragments) {
            for (int i = 2000; i < 2017; i++) {
                frag.addIndex(i);
            }
        }

        return fragments;
    }

    /*
        Generates a sample workload with example queries - right now, just a list of "WHERE VALUE = *"s
     */
    public static List<String> generateWorkload() {
        List<String> workload = new LinkedList<>();

        // change this!
        String wl = "2010\n" +
                "2012-2013\n" +
                "2004-2010\n" +
                "2013\n" +
                ">2012\n" +
                "2012\n" +
                "2012-2013\n" +
                ">2010\n" +
                "2011-2012\n" +
                "2012\n" +
                "<2013\n" +
                "2012-2013\n" +
                "<2013\n" +
                "2011-2013\n" +
                "2012\n" +
                "2013\n";

        Collections.addAll(workload, wl.split("\n"));

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
        int numEntries = 200;
        List<int[]> db = new LinkedList<>(); // Year, OtherAttribute
        Random rand = new Random();

        // values for "Year" range from 2000 to 2016
        // values for "OtherAttribute" from 1 to 999
        // feel free to change this too
        for (int i = 0; i < numEntries; i++) {
            db.add(new int[]{rand.nextInt(18) + 1999, rand.nextInt(1000)+1});
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

