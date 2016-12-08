/**
 * Created by seaurchi on 12/3/16.
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Fragment {
    private List<int[]> rows;

    public Fragment() {
        rows = new LinkedList();
    }

    public void addRow(int[] row) {
        rows.add(row);
    }

    public List<int[]> getRows() {
        return rows;
    }

    public String toString() {
        String stringstring = "";

        for (int[] row : rows) {
            stringstring += " " + row[0];
        }

        return stringstring;
    }
}

