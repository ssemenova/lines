import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Broker {
    List<Fragment> fragments;
    int QPW;

    public Broker(int QPW, List<Fragment> fragments) {
        this.fragments = fragments;
        this.QPW = QPW; // TODO: How often is QPW updated?
    }

    /*
        Redistributes fragments among nodes
     */
    public void distributeFragments() {
        // TODO: assuming 1 VM type
        for (Fragment frag : fragments) {
            frag.shares = calculateShares(frag, m);
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
        return QPW * (frag.density / shares) - frag.size*(m.cost / m.disk);
    }
}
