import java.util.List;

/**
 * Created by seaurchi on 2/20/17.
 */
public class Query {
    // price for query
    // fragments required for query
    // reads (# of tuples) required for query
    double price;
    List<Fragment> frags;
    List<Row> rows;
    int startValue;
    int endValue;

    public Query(double price) {
        this.price = price;
    }
}
