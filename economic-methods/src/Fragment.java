import java.util.HashMap;
import java.util.Map;

/**
 * Created by seaurchi on 2/17/17.
 */
public class Fragment {
    int start;
    int end;
    int size;
    int shares;
    int density;
    Map<Integer, Integer> densityEstimate;

    public Fragment(int start, int end, int size, int shares) {
        this.start = start;
        this.end = end;
        this.size = size;
        this.shares= shares;
        densityEstimate = createInitialDensityEstimate();
        this.density = calculateDensity();
    }

    /*
        Creates map from each tuple in the fragment to 0
        Since the initial density estimate is 0 for every tuple
     */
    private Map<Integer, Integer> createInitialDensityEstimate() {
        //estimates avg dollar value of a tuple over all queries
        Map<Integer, Integer> densityEstimate = new HashMap<>();

        for (int i = start; i < end; i++) {
            densityEstimate.put(i, 0);
        }

        return densityEstimate;
    }

    /*
        Update density estimate
     */
    public void updateDensityEstimate() {
        // TODO: write this
        // TODO: How often is density estimate updated?
    }

    /*
        Calculate density based on the density estimate for each tuple
     */
    private int calculateDensity() {
        int density = 0;

        for (int i = start; i < end; i++) {
            density += densityEstimate.get(i);
        }
        return density;
    }
}
