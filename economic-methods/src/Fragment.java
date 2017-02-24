import java.util.HashMap;
import java.util.Map;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Fragment {
    int start; // inclusive
    int end; // exclusive
    int size;
    int shares;
    int densityFrag;
    Map<Integer, Integer> densityTuples;

    public Fragment(int start, int end) {
        this.start = start;
        this.end = end;
        this.size = end - start;
        this.shares= 1;
        createInitialDensityEstimates();
    }

    /*
        Creates map from each tuple in the fragment to 0
        Since the initial density estimate is 0 for every tuple
     */
    private void createInitialDensityEstimates() {
        //estimates avg dollar value of a tuple over all queries
        densityTuples = new HashMap<>();

        for (int i = start; i < end; i++) {
            densityTuples.put(i, 0);
        }

        calculateDensityFromEstimates();
    }

    /*
        Update density estimate
     */
    public void updateDensityEstimate() {
        // TODO: write this
        // TODO: How often is density estimate updated?

        calculateDensityFromEstimates();
    }

    /*
        Calculate density based on the density estimate for each tuple
     */
    private void calculateDensityFromEstimates() {
        densityFrag = 0;

        for (int i = start; i < end; i++) {
            densityFrag += densityTuples.get(i);
        }
    }

    /*
        Get the variance for a fragment
     */
    public double getVariance(Database DB) {
        double variance;

        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += (densityTuples.get(i) - (densityFrag/(end-start)))^2;
        }

        variance = sum / (end - start);

        return 0.0;
    }

    /*
        Update information after a split
        To split a fragment, the first fragment object is kept the same and updated,
        while a new fragment object is created for the second

        Update information after a join
        To join three fragments into two, the first two fragment objects are updated,
        while the third fragment object is deleted

        Updating a fragment includes updating its start and end rows, and recalculating the density for each of the rows
     */
    public void updateAfterSplitorJoin(int newStart, int newEnd) {
        this.start = newStart;
        this.end = newEnd;
        updateDensityEstimate();
    }

    public String toString() {
        return start + "-" + end;
    }
}
