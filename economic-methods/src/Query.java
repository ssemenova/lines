import java.util.List;

/**
 * Created by seaurchi on 2/20/17.
 */
public class Query {
    // price for query
    // fragments required for query
    // reads (# of tuples) required for query
    double price;
    int start;
    int end;

    public Query(double price, int start, int end) {
        this.price = price;
        this.start = start;
        this.end = end;
    }
}
