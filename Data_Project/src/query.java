import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class query {

    // Regex patterns to parse SQL statements and split them into the basic parts
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)(CREATE\\sTABLE\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+\\(?[0-9]*\\)?,?)+)\\)\\s?;)");
    private static final Pattern DROP_TABLE_PATTERN = Pattern.compile("(?i)(DROP\\sTABLE\\s(\\w+);)");
    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)(SELECT\\s([\\s\\S]+)\\sFROM\\s(\\w+)+\\s?(WHERE\\s([\\s\\S]+))?;)");
    private static final Pattern INSERT_PATTERN = Pattern.compile("(?i)(INSERT\\sINTO\\s+(\\w+)\\s+\\(([\\s\\S]+)\\)\\s+VALUES\\s+\\(([\\s\\S]+)\\);)");
    private static final Pattern DELETE_PATTERN = Pattern.compile("(?i)(DELETE\\sFROM\\s+(\\w+)\\s+WHERE\\s+([\\s\\S]+);)");
    private static final Pattern UPDATE_PATTERN = Pattern.compile("(?i)(UPDATE\\s(\\w+)\\sSET\\s([\\s\\S]+)\\sWHERE\\s([\\s\\S]+);)");
    private static final Pattern ALTER_PK_PATTERN = Pattern.compile("(?i)(ALTER\\sTABLE\\s(\\w+)\\sADD\\sPRIMARY\\sKEY\\s\\(([\\s\\S]+)\\);)");
    private static final Pattern ALTER_FK_PATTERN = Pattern.compile("(?i)(ALTER\\sTABLE\\s(\\w+)\\sADD\\sFOREIGN\\sKEY\\s\\(([\\s\\S]+)\\)\\sREFERENCES\\s(\\w+)\\(([\\s\\S]+)\\);)");

    public static void parse(String username) {
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
            Matcher alterPKSQL = ALTER_PK_PATTERN.matcher(sql);
            Matcher alterFKSQL = ALTER_FK_PATTERN.matcher(sql);

            if (createTableSQL.find()) {
                // To do: write event logs for the user query
                System.out.println("create a table: " + sql);
                create(createTableSQL, username);
            } else if (dropTableSQL.find()) {
                System.out.println("drop a table: " + sql);
                drop(dropTableSQL, username);
            } else if (selectSQL.find()) {
                System.out.println("select data: " + sql);
                select(selectSQL, username);
            } else if (insertSQL.find()) {
                System.out.println("insert data: " + sql);
                insert(insertSQL, username);
            } else if (deleteSQL.find()) {
                System.out.println("delete data: " + sql);
                delete(deleteSQL, username);
            } else if (updateSQL.find()) {
                System.out.println("update data: " + sql);
                update(updateSQL, username);
            } else if (alterPKSQL.find()) {
                alterPK(alterPKSQL, username);
            } else if (alterFKSQL.find()) {
                alterFK(alterFKSQL, username);
            } else {
                // Nothing matches with Regex patterns
                System.out.println("Please make sure the input is in standard SQL format.\n" + sql + " is not valid.");
            }
            // Link to next input
            System.out.println("Please enter the SQL queries or type 'exit' to quit.");
        }
    }

    // Parse the statements deeper, to prepare for the actual operations

    private static void create(Matcher createTable, String username) {
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

        // Link user to actions
        int created = action.create(username, tableName, columnName, dataType);
        //if returned 0 then user can't create otherwise print user can create a table
        if (created == 0) {
            System.out.println("The table: " + tableName + " cannot be created.");
        } else {
            System.out.println("The table: " + tableName + " is created by: " + username);
        }
    }

    private static void drop(Matcher dropTable, String username) {
        String tableName = dropTable.group(2);
        System.out.println(tableName);
        // Link user to actions
        int dropped = action.drop(username, tableName);
        //returns 1 if table was dropped
        //returns 0 if user can't drop the table
        if (dropped == 0) {
            System.out.println("The table: " + tableName + " cannot be dropped.");
        } else {
            System.out.println("Dropped table: " + tableName + " by: " + username);
        }
    }

    private static void select(Matcher select, String username) {
        String fieldNames = select.group(2);
        String[] fieldNamesString = fieldNames.split("\\s*,\\s*");
        List<String> fieldNamesStringList = Arrays.asList(fieldNamesString);
        System.out.println(fieldNamesStringList);
        String tableName = select.group(3);
        System.out.println(tableName);
        String conditionName = "";
        String conditionValue = "";
        if (select.group(4) != null) {
            String condition = select.group(5);
            String[] conditionString = condition.split("\\s*=\\s*");
            conditionName = conditionString[0];
            conditionValue = conditionString[1];
            System.out.println(conditionName + "\n" + conditionValue);
        }
        // Link user to actions
        int selected = action.select(username, tableName, fieldNamesStringList, conditionName, conditionValue);
        if (selected == 0) {
            System.out.println("The table: " + tableName + " cannot be selected.");
        } else {
            System.out.println("Values are selected from table: " + tableName + " by: " + username);
        }

    }

    private static void insert(Matcher insert, String username) {

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
        int inserted = action.insert(username, tableName, columnNameList, columnValueList);
        if (inserted == 0) {
            System.out.println("The table: " + tableName + " cannot be inserted.");
        } else {
            System.out.println("Values are inserted into table: " + tableName + " by: " + username);
        }

    }

    private static void delete(Matcher delete, String username) {
        String tableName = delete.group(2);
        String condition = delete.group(3);
        String[] conditionString = condition.split("\\s*=\\s*");
        String conditionName = conditionString[0];
        String conditionValue = conditionString[1];
        System.out.println(tableName + "\n" + conditionName + "\n" + conditionValue);

        // Link user to actions
        int deleted = action.delete(username, tableName, conditionName, conditionValue);
        if (deleted == 0) {
            System.out.println("The table: " + tableName + " cannot be deleted.");
        } else {
            System.out.println("Deleted values in table: " + tableName + " by: " + username);
        }
    }

    private static void update(Matcher update, String username) {
        String tableName = update.group(2);
        String set = update.group(3);
        String[] setString = set.split("\\s*,\\s*");
        ArrayList<String> column = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();
        for (String s : setString) {
            String[] columnValue = s.split("\\s*=\\s*"); // separate by whitespace
            column.add(columnValue[0]);
            value.add(columnValue[1]);
        }
        String condition = update.group(4);
        String[] conditionString = condition.split("\\s*=\\s*");
        String conditionName = conditionString[0];
        String conditionValue = conditionString[1];
        System.out.println(tableName + "\n" + column + "\n" + value + "\n" + conditionName + "\n" + conditionValue);
        // Link user to actions
        int updated = action.update(username, tableName, column, value, conditionName, conditionValue);
        if (updated == 0) {
            System.out.println("The table: " + tableName + " cannot be updated.");
        } else {
            System.out.println("The table: " + tableName + " is updated by: " + username);
        }
    }

    private static void alterPK(Matcher alterPKSQL, String username) {
        String tableName = alterPKSQL.group(2);
        String primaryKey = alterPKSQL.group(3);

        int alterPK = 0;
//                action.alterPK(username, tableName, primaryKey);
        if (alterPK == 0) {
            System.out.println("Primary key: " + primaryKey + " cannot be added to table: " + tableName);
        } else {
            System.out.println("Primary key: " + primaryKey + " is added to table: " + tableName + " by: " + username);
        }
    }

    private static void alterFK(Matcher alterFKSQL, String username) {
        String tableName1 = alterFKSQL.group(2);
        String foreignKey = alterFKSQL.group(3);
        String tableName2 = alterFKSQL.group(4);
        String primaryKey = alterFKSQL.group(5);

        int alterFK = 0;
//                action.alterFK(username, tableName1, foreignKey, tableName2, primaryKey);
        if (alterFK == 0) {
            System.out.println("Foreign key: " + foreignKey + " cannot be added between table: " + tableName1 + " and table: " + tableName2);
        } else {
            System.out.println("Foreign key: " + foreignKey + " is added between table: " + tableName1 + "and table: " + tableName2 + " by: " + username);
        }

    }
}
