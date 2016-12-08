import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by seaurchi on 12/7/16.
 */
public abstract class Query {
    public Query() {}

    public abstract List<Fragment> getFragments(Map<Integer, List<Fragment>> index);

    public abstract Boolean rowMatch(int[] row);

    public abstract int[] matches(Fragment frag);
}
