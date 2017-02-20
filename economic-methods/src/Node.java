import java.util.LinkedList;
import java.util.List;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Node {
    int cost; // cost per hour
    int disk; // disk space
    List<Fragment> fragments;

    public Node(int cost, int disk) {
        this.cost = cost;
        this.disk = disk;
        this.fragments = new LinkedList<>();
    }
}
