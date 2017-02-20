import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by seaurchi on 2/19/17.
 */
public class Database {
    Map<Integer, Row> DB;
    Row attrs;
    int size;

    public Database(List<String> attrs, int DBrows) {
        this.attrs = new Row(1, attrs);

        Random rand = new Random();

        for (int i = 0; i < DBrows; i++) {
            List<String> row = new LinkedList<>();
            for (int j = 0; j < attrs.size(); j++) {
                row.add(Integer.toString(rand.nextInt(5)));
            }
            this.addRow(row);
        }
    }

    /*
        Add row to database, used to create DB
     */
    private void addRow(List<String> attrs) {
        int rowNum = DB.size()-1;
        Row row = new Row(rowNum, attrs);
        DB.put(rowNum, row);
    }

}
