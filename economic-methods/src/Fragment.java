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
    Map<Integer, Double> densityTuples; // TODO: interval tree
    int numQueries;

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
            densityTuples.put(i, 0.0);
        }

        calculateDensityFromEstimates();
    }

    /*
        Update density estimate
     */
    public void updateDensityFrag(Query q) {
        for (int i = start; i < end; i++) {
            densityTuples.replace(i,(q.price + densityTuples.get(i)) / numQueries);
        }

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
    public double getVariance() {
        double variance;
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += Math.pow((densityTuples.get(i) - (densityFrag/(end-start))), 2);
        }
        variance = sum / (end - start);
        return variance;
    }

    /*
        Update information after a split or join
     */
    public void updateAfterSplitorJoin() {
        //TODO: move density tuples over to another fragment instead of deleting and recreating
    }

    public String toString() {
        return start + "-" + end;
    }


}
