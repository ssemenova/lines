/**
 * Created by seaurchi on 12/3/16.
 */
import java.util.HashSet;

public class Node {
    private HashSet<Fragment> fragments;
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

    public void doScan() {
        scan++;
    }

    public void doNetwork() {
        network++;
    }

    public String toString() {
        String printOut = "";

        for (Fragment frag : fragments) {
            printOut += frag.toString();
        }
        return printOut;
    }

}
