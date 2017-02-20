import java.util.List;

/**
 * Created by seaurchi on 2/19/17.
 */
public class Row {
    List<String> attrs;
    int ID;

    public Row(int ID, List<String> attrs) {
        this.attrs = attrs;
        this.ID = ID;
    }
}
