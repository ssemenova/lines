//import java.util.*;
//
///**
// * Created by seaurchi on 2/19/17.
// */
//public class Database {
//    List<Row> DB;
//    Row attrs;
//    int size;
//
//    public Database(List<String> attrs, int DBrows) {
//        this.attrs = new Row(attrs);
//        this.size = DBrows;
//        DB = generateDB(DBrows, attrs.size());
//    }
//
//    /*
//        Generates a test database
//        -TESTED
//     */
//    private List<Row> generateDB(int DBrows, int attrsSize) {
//        Random rand = new Random();
//        List<Row> generatedDB = new LinkedList<>();
//
//        for (int i = 0; i < DBrows; i++) {
//            List<String> row = new LinkedList<>();
//            for (int j = 0; j < attrsSize; j++) {
//                row.add(Integer.toString(rand.nextInt(5)));
//            }
//            addRow(generatedDB, row);
//        }
//
//        return generatedDB;
//    }
//
//    /*
//        Add row to database, used to create DB
//        -TESTED
//     */
//    private void addRow(List<Row> DBtoChange, List<String> attrs) {
//        int rowNum = DBtoChange.size();
//        Row row = new Row(attrs);
//        DBtoChange.add(row);
//    }
//
//    /*
//        Sort the database by a key
//        -TESTED
//     */
//    public void sort(int index) {
//        Collections.sort(DB, (o1, o2) -> {
//            int aValue = Integer.parseInt(o1.attrs.get(index));
//            int bValue = Integer.parseInt(o2.attrs.get(index));
//
//            if (aValue < bValue)
//                return -1;
//            else if (aValue > bValue)
//                return 1;
//            else
//                return 0;
//        });
//
//        for (int i = 0; i < DB.size(); i++) {
//            DB.get(i).addID(i);
//        }
//    }
//}
