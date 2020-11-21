import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class query {

    // Regex patterns to parse SQL statements and split them into the basic parts
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)(CREATE\\sTABLE\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+\\(?[0-9]*\\)?,?)+)\\)\\s?;)");
    private static final Pattern DROP_TABLE_PATTERN = Pattern.compile("(?i)(DROP\\sTABLE\\s(\\w+);)");
    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)(SELECT\\s([\\s\\S]+)\\sFROM\\s([\\s\\S]+)(\\sWHERE([\\s\\S]+))?;)");
    private static final Pattern INSERT_PATTERN = Pattern.compile("(?i)(INSERT\\sINTO\\s+(\\w+)\\s+\\(([\\s\\S]+)\\)\\s+VALUES\\s+\\(([\\s\\S]+)\\);)");
    private static final Pattern DELETE_PATTERN = Pattern.compile("(?i)(DELETE\\sFROM\\s+(\\w+)\\s+WHERE\\s+([\\s\\S]+);)");
    private static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)(UPDATE\\s(\\w+)\\sSET\\s([\\s\\S]+)\\sWHERE\\s([\\s\\S]+);)");

    public static void parse(String username, String password) {
        System.out.println("Please enter the SQL queries or type 'exit' to quit");
        Scanner sc = new Scanner(System.in);
        String sql;

        // User enter SQL queries, if not "exit" continue to match if it is a SQL statement
        while (sc.hasNext() && !((sql = sc.nextLine()).equalsIgnoreCase("exit"))) {

            // Use matchers to match the regex patterns we wrote
            Matcher createTableSQL = CREATE_TABLE_PATTERN.matcher(sql);
            Matcher dropTableSQL = DROP_TABLE_PATTERN.matcher(sql);
            Matcher selectSQL = SELECT_PATTERN.matcher(sql);
            Matcher insertSQL = INSERT_PATTERN.matcher(sql);
            Matcher deleteSQL = DELETE_PATTERN.matcher(sql);
            Matcher updateSQL = UPDATE_PATTERN.matcher(sql);

            if (createTableSQL.find()) {
                // To do: write event logs for the user query
                System.out.println("create a table: " + sql);
                create(createTableSQL, username, password);
            } else if (dropTableSQL.find()) {
                System.out.println("drop a table: " + sql);
                drop(dropTableSQL, username, password);
            } else if (selectSQL.find()) {
                System.out.println("select data: " + sql);
                select(selectSQL, username, password);
            } else if (insertSQL.find()) {
                System.out.println("insert data: " + sql);
                insert(insertSQL, username, password);
            } else if (deleteSQL.find()) {
                System.out.println("delete data: " + sql);
                delete(deleteSQL, username, password);
            } else if (updateSQL.find()) {
                System.out.println("update data: " + sql);
                update(updateSQL, username, password);
            } else {
                // Nothing matches with Regex patterns
                System.out.println("Please make sure the input is in standard SQL format.\n" + sql + " is not valid.");
            }
            // Link to next input
            System.out.println("Please enter the SQL queries or type 'exit' to quit.");
        }
    }

    // Parse the statements deeper, to prepare for the actual operations
    private static void create(Matcher createTable, String username, String password) {
        String tableName = createTable.group(2);
        String columns = createTable.group(3);
        // Separate columns into 2 lists: column name and datatype
        String[] columnString = columns.split("\\s*,\\s*"); // separate by comma
        List<String> columnStringList = Arrays.asList(columnString); //List contains column name and datatype, eg. id int
        ArrayList<String> columnName = new ArrayList<>();
        ArrayList<String> dataType = new ArrayList<>();
        for (String s : columnStringList) {
            String[] columnType = s.split("\\s+"); // separate by whitespace
            columnName.add(columnType[0]);
            dataType.add(columnType[1]);
        }
        System.out.println(columnName + " " + dataType);
        int created = action.create(username, tableName, columnName, dataType);
        if (created == 0) {
            System.out.println("The table: " + tableName + "cannot be created.");
        }else{
            System.out.println("The table: " + tableName + "is created by: " + username);
        }

        // Link user to actions

        //if returned 0 then user can't create otherwise print user can create a table

    }

    private static void drop(Matcher dropTable, String username, String password) {
        String tableName = dropTable.group(1);

        // Link user to actions

        //returns 1 if table was dropped
        //returns 0 if user can't drop the table
    }

    private static void select(Matcher select, String username, String password) {
        String fieldNames = select.group(1);
        String tableName = select.group(2);
        if (select.group(3) != null) {
            String conditions = select.group(3);
        }

        // Link user to actions
    }

    private static void insert(Matcher insert, String username, String password) {
        String tableName = insert.group(2);
        String keys = insert.group(3);
        String[] columnName = keys.split("\\s*,\\s*");
        List<String> columnNameList = Arrays.asList(columnName);
        // Separate keys into a arraylist
        String values = insert.group(4);
        String[] columnValue = values.split("\\s*,\\s*");
        List<String> columnValueList = Arrays.asList(columnValue);
        // Separate values into a arraylist
        System.out.println(columnNameList + " " + columnValueList);
        // Link user to actions
        //returns 1 if changes made in the text file
        //returns 0 if no changes allowed to make
        int created = action.insert(username, tableName, columnNameList, columnValueList);
        if (created == 0) {
            System.out.println("The table: " + tableName + "cannot be created.");
        }else{
            System.out.println("The table: " + tableName + "is created by: " + username);
        }

    }

    private static void delete(Matcher delete, String username, String password) {
        String tableName = delete.group(1);
        String conditions = delete.group(2);

        // Link user to actions
    }

    private static void update(Matcher update, String username, String password) {
        String tableName = update.group(1);
        String updates = update.group(2);
        String conditions = update.group(3);

        // Link user to actions
    }
}
