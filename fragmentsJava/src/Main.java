import java.util.*;

public class Main {
    public static void main(String[] args) {
        // setup - change all these values!
        int fragType = 0; // 0 - hash, 1 - value
        LinkedList<int[]> valRanges = (fragType == 1) ? generateValRanges() : null;
        LinkedList<String> workload = generateWorkload();
        int numNodes = 2;
        int numFrags = 5; // only needed/used for hash fragmentation type (for value frag type, number of fragments is determined by the value ranges list)
        LinkedList<int[]> db = generateDB();

        // create fragments, nodes, and an index
        LinkedList<Fragment> fragments = (fragType == 1) ? createFragments(valRanges, db) : createFragments(db, numFrags);
        Map<Integer, Fragment> index = createIndex(fragments);
        LinkedList<Node> nodes = createNodes(numNodes, fragments);

        runThroughWorkload(workload, index, nodes);
    }

    /*
        Runs through the sample workload and prints out,
        for every query, for every node, the amount of networks and scans it had to do
     */
    public static void runThroughWorkload(LinkedList<String> workload, Map<Integer, Fragment> index, LinkedList<Node> nodes) {
        LinkedList<Fragment> relevantFrags = new LinkedList<>();

        for (String query : workload) {
            relevantFrags = processQuery(query, index);

            for (Fragment frag : relevantFrags) {

            }

            System.out.println("Query " + query);
            for (Node node : nodes) {
                System.out.println("-----");
                System.out.println("Networks : " + node.getNetworks());
                System.out.println("Scans : " + node.getScans());
            }
        }
    }

    public static LinkedList<Fragment> processQuery(String query, Map<Integer, Fragment> index) {
        LinkedList<Fragment> relevantFrags = new LinkedList<>();
        int start = 0;
        int end = 0;

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
            relevantFrags.add(index.get(i));
        }
        return relevantFrags;
    }

    /*
        Creates an "index" that maps values to the fragment they can be found in
     */
    public static Map<Integer, Fragment> createIndex(LinkedList<Fragment> fragments) {
        Map<Integer, Fragment> index = new HashMap<>();

        for (Fragment frag : fragments) {
            for (int i2 : frag.getIndex()) {
                index.put(i2, frag);
            }
        }

        return index;
    }

    /*
        Creates nodes
     */
    public static LinkedList<Node> createNodes(int numNodes, LinkedList<Fragment> fragments) {
        LinkedList<Node> nodes = new LinkedList<>();

        for (int i = 0; i < numNodes; i++) {
            nodes.add(new Node());
        }

        // assigns fragments to nodes
        int fragmentsPerNode = fragments.size() / numNodes;
        int i = 0;

        while (!fragments.isEmpty()) {
            nodes.get(i%numNodes).addFragment(fragments.pop());
            i++;
        }

        return nodes;
    }

    /*
        Creates fragments if fragmentation type is value
     */
    public static LinkedList<Fragment> createFragments(LinkedList<int[]> valRanges, LinkedList<int[]> db) {
        LinkedList<Fragment> fragments = new LinkedList<>();

        // value fragmentation - will fragment based on a specified list of value ranges
        int rangeStart, rangeEnd;

        // for every range of values, create a new fragment object, add matching rows to it,
        // and generate the index for those rows
        for (int[] range : valRanges) {
            rangeStart = range[0];
            rangeEnd = range[1];
            Fragment tempFrag = new Fragment();
            int currAttr = db.getFirst()[0];
            Boolean newAttr = true;

            for (int[] row : db) {
                if (row[0] >= rangeStart && row[0] < rangeEnd) {
                    if (newAttr) {
                        currAttr = row[0];
                        tempFrag.addIndex(currAttr);
                    }
                    tempFrag.addRow(row);
                }
                newAttr = currAttr != row[0];
            }

            fragments.add(tempFrag);
        }

        return fragments;
    }

    /*
        Creates fragments if fragmentation type is hash
     */
    public static LinkedList<Fragment> createFragments(LinkedList<int[]> db, int numFrags) {
        LinkedList<Fragment> fragments = new LinkedList<>();

        for (int i = 0; i < numFrags; i++) {
            fragments.add(new Fragment());
        }

        int attr;
        Fragment tempFrag;
        for (int[] row : db) {
            attr = row[0];
            tempFrag = fragments.get(attr%numFrags);
            tempFrag.addRow(row);

            //if the fragment does not yet contains a row with a value equal to the current value
            if (!tempFrag.getIndex().contains(attr)) {
                tempFrag.addIndex(row[0]);
            }
        }

        System.out.println(fragments);
        for (Fragment frag : fragments) {
            System.out.println(frag);
        }

        return fragments;
    }

    /*
        Generates a sample workload with example queries - right now, just a list of "WHERE VALUE = *"s
     */
    public static LinkedList<String> generateWorkload() {
        LinkedList<String> workload = new LinkedList();

        // change this!
        String wl = "2012\n" +
                "2012-2013\n" +
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

        String[] wla = wl.split("\n");
        for (String query : wla)
            workload.add(query);

        return workload;
    }

    /*
        If value-based fragmentation, a list of value ranges to fragment on
     */
    public static LinkedList<int[]> generateValRanges() {
        LinkedList<int[]> valRanges = new LinkedList();

        // change this!
        // ranges for "Year" from [x, y)
        valRanges.add(new int[]{2000, 2002});
        valRanges.add(new int[]{2002, 2004});
        valRanges.add(new int[]{2004, 2010});
        valRanges.add(new int[]{2010, 2016});

        return valRanges;
    }

    /*
        Generates a random database with *numEntries* rows and 2 attributes
     */
    public static LinkedList<int[]> generateDB() {
        int numEntries = 200;
        LinkedList<int[]> db = new LinkedList<int[]>(); // Year, OtherAttribute
        Random rand = new Random();

        // values for "Year" range from 2000 to 2016
        // values for "OtherAttribute" from 0 to 999
        // feel free to change this too
        for (int i = 0; i < numEntries; i++) {
            db.add(new int[]{rand.nextInt(16) + 2000, rand.nextInt(1000)});
        }

        //sort by value "Year"
        Collections.sort(db, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2){
                if (o1[0] > o2[0])
                    return 1;
                else if (o1[0] < o2[0])
                    return -1;
                else
                    return 0;
            }
        });

        return db;
    }
}

