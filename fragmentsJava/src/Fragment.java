/**
 * Created by seaurchi on 12/3/16.
 */
import java.util.LinkedList;

public class Fragment {
    private LinkedList<Integer> index;
    private LinkedList<int[]> rows;

    public Fragment() {
        index = new LinkedList();
        rows = new LinkedList();
    }

    public void addRow(int[] row) {
        rows.add(row);
    }

    public void addIndex(int index) {
        this.index.add(index);
    }

    public LinkedList<Integer> getIndex() {
        return index;
    }
}

