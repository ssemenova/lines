import com.lodborg.intervaltree.IntervalTree;

import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    }

    /*
        Process a query
     */
    public void processQuery(Query q) {
        List<Fragment> fragsNeeded = new LinkedList<>(); // never use this - why is it necessary?
        //find fragments needed
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

        // TODO: weighted random, not every fragment
        for (Fragment frag : fragments) {
            variances.add(frag.getVariance());
        }

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

        // TODO: figure it out

        // frag.updateAfterSplitorJoin();
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
