import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by seaurchi on 12/7/16.
 */
public class SimpleValueQuery extends Query {
    private int start;
    private int end;

    public SimpleValueQuery(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /*
        Given an index of fragments, returns a list of fragments that satisfy the query
     */
    public List<Fragment> getFragments(Map<Integer, List<Fragment>> index) {
        List<Fragment> relevantFrags = new LinkedList<>();

        for (int i = start; i <= end; i++) {
            for (Fragment frag : index.get(i))
                if (!relevantFrags.contains(frag))
                    relevantFrags.add(frag);
        }

        return relevantFrags;
    }

    public Boolean rowMatch(int[] row) {
        return true;
    }

    public int[] matches(Fragment frag, int fragType) {
        //for the given fragment, how many rows need to be scanned?
        int[] results = {0,0}; //scans, networks

        List<int[]> rows = frag.getRows();
        int currAttr = rows.get(0)[0];

        //move up in db so value range matches
        int i = 0;
        while (currAttr < start) {
            i++;
            currAttr = rows.get(i)[0];
        }

        int[] currRow = rows.get(i);
        while (currAttr <= end && i < rows.size()) {
            results[0]++;
            if (rowMatch(currRow)) {
                results[1]++;
            }
            currRow = rows.get(i);
            i++;
        }

        return results;
    }

    public String toString() {
        String result = "start = " + start + ", end = " + end;
        return result;
    }
}
