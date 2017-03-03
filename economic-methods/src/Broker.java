import com.lodborg.intervaltree.IntervalTree;

import java.util.*;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Broker {
    IntervalTree<Integer> tree = new IntervalTree<>();
    List<Fragment> fragments;
    List<Node> nodes;
    int QPW;
    int DBsize;

    public Broker(List<Fragment> fragments, List<Node> nodes, int DBsize) {
        this.fragments = fragments;
        this.QPW = 0;
        this.nodes = nodes;
        this.DBsize = DBsize;
        distributeFragments();
    }

    /*
        Process a query
     */
    public void processQuery(Query q) {
        List<Fragment> fragsNeeded = new LinkedList<>(); // TODO: never use this - why is it necessary to keep a list of needed queries?

        for (Fragment frag : fragments) {
            if ((frag.start <= q.start && frag.end > q.start) || (frag.end >= q.end && frag.start < q.end )) {
                fragsNeeded.add(frag);
            }
            frag.updateDensityFrag(q);
        }
    }

    /*
        Decide whether to split or join any fragments and whether to redistribute them
     */
    public void evaluateFragments() {
        List<Double> variances = new LinkedList<>();

        Map<Fragment, Double> fragVar = new HashMap<>();

        for (Fragment frag : fragments) {
            fragVar.put(frag, frag.getVariance());
        }


        // Split fragments
        // TODO: choose weighted random fragment to split or join
//        Fragment chosenFrag;
//        findSplitPoint(chosenFrag);

        //Join fragments
        Collections.sort(fragments, new Comparator<Fragment>(){
            public int compare(Fragment frag1, Fragment frag2) {
                if (frag1.start < frag2.start) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        List<Double> joinVarSum = new LinkedList<>();
        double var;
        for (int i = 0; i < Math.ceil(fragments.size()/3); i = i*3) {
            var = 0;
            for (int j = 0; j < 3; j++) {
                var += fragments.get(i+j).getVariance();
            }
        }



        // TODO: call findSplitPoint on that fragment


        // TODO: call updateAfterSplitorJoin on all fragments involved

    }

    /*
        Finds a split point that minimizes the variance in the fragment
     */
    private int findSplitPoint(Fragment frag) {
        // TODO: write
        return 0;
    }

    /*
        Split fragment
        To split a fragment, the first fragment object is kept the same and updated,
        while a new fragment object is created for the second
     */
    private void splitFragment(Fragment frag, int S) {
        int end = frag.end;
        frag.end = S;
        Fragment newFrag = new Fragment(S, end);
        fragments.add(newFrag);
    }

    /*
        Join fragments
        To join three fragments into two, the first fragment is start->S, the second is S->end, and the last is deleted
     */
    private void joinFragments(Fragment frag1, Fragment frag2, Fragment frag3, int S) {
        int totalEnd = frag3.end;

        frag1.end = S;
        frag2.start = S;
        frag2.end = totalEnd;
        fragments.remove(frag3);
    }

    /*
        Redistributes fragments among nodes
     */
    public void distributeFragments() {
        for (Fragment frag : fragments) {
            frag.shares = calculateShares(frag, nodes.get(0));
        }

        // TODO: distribute fragments among VMs
    }

    /*
        Calculate largest amount of shares while profit is still > 0
     */
    private int calculateShares(Fragment frag, Node m) {
        int shares = 0;
        int profit = calculateProfit(frag, m, 0);

        while (profit >= 0) {
            shares++;
            profit = calculateProfit(frag, m, 0);
        }
        return shares-1;
    }

    /*
        Helper method for calculateShares, returns profit given a fragment, node, and number of shares
     */
    private int calculateProfit(Fragment frag, Node m, int shares) {
        return QPW * (frag.densityFrag / shares) - frag.size*(m.cost / m.disk);
    }
}
