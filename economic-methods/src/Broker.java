import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Broker {
    List<Fragment> fragments;
    List<Node> nodes;
    int QPW;
    Time updateInterval;
    Time startTime;

    public Broker(List<Fragment> fragments, List<Node> nodes) {
        this.fragments = fragments;
        this.QPW = 0;
        this.nodes = nodes;
    }

    /*
        Process a query
     */
    public void processQuery(Query q) {
        for (Fragment frag : fragments) {
            frag.updateDensityEstimate();
        }
    }

    /*
        Split fragment
     */
    private void splitFragment(Fragment frag, int S) {
        // TODO: write
    }

    /*
        Join fragments
     */
    private void joinFragments(Fragment frag1, Fragment frag2, Fragment frag3) {
        // TODO: write
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
        return QPW * (frag.density / shares) - frag.size*(m.cost / m.disk);
    }
}
