import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class query {
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)(CREATE\\sTABLE\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+\\(?[0-9]*\\)?,?)+)\\)\\s?;)");
    private static final Pattern DROP_TABLE_PATTERN = Pattern.compile("(?i)(DROP\\sTABLE\\s(\\w+);)");
    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)(SELECT\\s([\\s\\S]+)\\sFROM\\s([\\s\\S]+)(\\sWHERE([\\s\\S]+))?;)");
    private static final Pattern INSERT_PATTERN = Pattern.compile("(?i)(INSERT\\sINTO\\s+(\\w+)\\s+\\(([\\s\\S]+)\\)\\s+VALUES\\s+\\(([\\s\\S]+)\\);)");
    private static final Pattern DELETE_PATTERN = Pattern.compile("(?i)(DELETE\\sFROM\\s+(\\w+)\\s+WHERE\\s+([\\s\\S]+);)");
    private static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)(UPDATE\\s(\\w+)\\sSET\\s([\\s\\S]+)\\sWHERE\\s([\\s\\S]+);)");

    public static void parse() {
        System.out.println("Please enter the SQL queries or type 'exit' to quit");
        Scanner sc = new Scanner(System.in);
        String sql;
        while (sc.hasNext() && !((sql = sc.nextLine()).equalsIgnoreCase("exit"))) {
            Matcher createTableSQL = CREATE_TABLE_PATTERN.matcher(sql);
            Matcher dropTableSQL = DROP_TABLE_PATTERN.matcher(sql);
            Matcher selectSQL = SELECT_PATTERN.matcher(sql);
            Matcher insertSQL = INSERT_PATTERN.matcher(sql);
            Matcher deleteSQL = DELETE_PATTERN.matcher(sql);
            Matcher updateSQL = UPDATE_PATTERN.matcher(sql);

            if(createTableSQL.find()){
                System.out.println("create a table: "+ sql);
                create(createTableSQL);
            }else if(dropTableSQL.find()){
                System.out.println("drop a table: " + sql);
                drop(dropTableSQL);
            }
            else if (selectSQL.find()) {
                System.out.println("select data: " + sql);
                select(selectSQL);
            }
            else if(insertSQL.find()) {
                System.out.println("insert data: " + sql);
                insert(insertSQL);
            }
            else if (deleteSQL.find()) {
                System.out.println("delete data: " + sql);
                delete(deleteSQL);
            }
            else if (updateSQL.find()) {
                System.out.println("update data: " + sql);
                update(updateSQL);
            }
            else{
                System.out.println("Please make sure the input is in standard SQL format.\n" + sql);
            }
            System.out.println("Please enter the SQL queries or type 'exit' to quit.");
        }
    }

    private static void create(Matcher createTable) {
        String tableName = createTable.group(1);
        String columns = createTable.group(2);
    }

    private static void drop(Matcher dropTable) {
        String tableName = dropTable.group(1);
    }

    private static void select(Matcher select) {
        String fieldNames = select.group(1);
        String tableName = select.group(2);
        if (select.group(3)!=null){
            String conditions = select.group(3);
        }
    }

    private static void insert(Matcher insert) {
        String tableName = insert.group(1);
        String keys = insert.group(2);
        String values = insert.group(3);

    }

    private static void delete(Matcher delete) {
        String tableName = delete.group(1);
        String conditions = delete.group(2);
    }

    private static void update(Matcher update) {
        String tableName = update.group(1);
        String updates = update.group(2);
        String conditions = update.group(3);
    }
}
