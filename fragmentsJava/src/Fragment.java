/**
 * Created by seaurchi on 12/3/16.
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Fragment {
    private Map<Integer, Integer> index;
    private List<int[]> rows;

    public Fragment() {
        index = new HashMap();
        rows = new LinkedList();
    }

    public void addRow(int[] row) {
        rows.add(row);
    }

    public void addIndex(int index) {
        //counts up the number of rows that match the index
        //and places that in the hashmap
        int currAttr = rows.get(0)[0];
        int matchingRows = 0;

        int i = 0;
        while (currAttr <= index && i < this.rows.size()) {
            if (currAttr == index) {
                matchingRows++;
            }
            currAttr = rows.get(i)[0];
            i++;
        }
        this.index.put(index, matchingRows);
    }

    public Map<Integer, Integer> getIndex() {
        return index;
    }

    public String toString() {
        String stringstring = "";

        for (Integer key : index.keySet()) {
            stringstring += " - i" + key + " r" + index.get(key);
        }

        return stringstring;
    }
}

