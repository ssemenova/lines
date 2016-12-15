import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by seaurchi on 12/7/16.
 */
public class DoubleValueQuery extends Query {
    private int start;
    private int end;
    private int extra;

    public DoubleValueQuery(int start, int end, int extra) {
        this.start = start;
        this.end = end;
        this.extra = extra;
    }


    public List<Fragment> getFragments(Map<Integer, List<Fragment>> index) {
        List<Fragment> relevantFrags = new LinkedList<>();

        for (int i = start; i <= end; i++) {
            if (index.keySet().contains(i))
                for (Fragment frag : index.get(i))
                    if (!relevantFrags.contains(frag))
                        relevantFrags.add(frag);
        }

        return relevantFrags;
    }

    public Boolean rowMatch(int[] row) {
        return (row[1] == extra);
    }

    public int[] matches(Fragment frag, int fragType) {
        //for the given fragment, how many rows need to be scanned?
        int[] results = {0,0}; //scans, networks

        List<int[]> rows = frag.getRows();
        int currAttr = rows.get(0)[0];

        //move up in db so value range matches
        int i = 0;
        while (currAttr < start && i < rows.size() - 1) {
            i++;
            currAttr = rows.get(i)[0];
        }

        int[] currRow = rows.get(i);
        while (currAttr <= end && i < rows.size()) {
            if (rowMatch(currRow)) {
                results[1]++;
            }
            currRow = rows.get(i);
            i++;
        }

        results[0] = rows.size();

        return results;
    }

    public String toString() {
        String result = "start = " + start + ", end = " + end + ", extra = " + extra;
        return result;
    }

}
