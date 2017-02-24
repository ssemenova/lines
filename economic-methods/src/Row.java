import java.util.List;

/**
 * Created by seaurchi on 2/19/17.
 */
public class Row {
    List<String> attrs;
    int ID;

    public Row(List<String> attrs) {
        this.attrs = attrs;
    }

    /* Add ID # to Row */
    public void addID(int ID) {
        this.ID = ID;
    }

    public String toString() {
        String returnString = ID + " =";
        for (String attr : attrs) {
            returnString += " " + attr;
        }

        return returnString;
    }
}
