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
    public void addScans(int scans) { this.scan += scans; }

    public int getNetworks() {
        return network;
    }
    public void addNetworks(int network) { this.network += network; }

    public void resetScanNetwork() { scan = 0; network = 0; }

    public Set<Fragment> getFragments() {
        return fragments;
    }

    public String toString() {
//        return "NODE= networks: " + network + ", scans: " + scan;
        return " " + fragments;
    }
}
