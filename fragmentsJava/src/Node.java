/**
 * Created by seaurchi on 12/3/16.
 */
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Node {
    private Set<Fragment> fragments;
    private int network;
    private int scan;

    public Node() {
        network = 0;
        scan = 0;
        fragments = new HashSet<>();
    }

    public void addFragment(Fragment frag) {
        fragments.add(frag);
    }

    public int getScans() {
        return scan;
    }

    public int getNetworks() {
        return network;
    }

    public void doScanNetwork(Fragment frag, int target) {
        //for the given fragment, how many rows need to be scanned?
        scan += frag.getIndex().get(target);
        network += frag.getIndex().get(target);
    }

    public void resetScanNetwork() {
        scan = 0; network = 0;
    }

    public Set<Fragment> getFragments() {
        return fragments;
    }

    public String toString() {
        return "NODE= networks: " + network + ", scans: " + scan;
    }

}
