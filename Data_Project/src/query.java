import java.io.*;
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
    private static final Pattern ALTER_FK_PATTERN = Pattern.compile("(?i)(ALTER\\sTABLE\\s(\\w+)\\sADD\\sFOREIGN\\sKEY\\s\\(([\\s\\S]+)\\)\\sREFERENCES\\s(\\w+)\\s\\(([\\s\\S]+)\\);)");

    public static void parse(String username) throws IOException {
        System.out.println("Please enter the SQL queries, type 'dump' to get your dump file, or type 'exit' to quit");
        Scanner sc = new Scanner(System.in);
        String input;
        File file = new File("Event_Logs.txt"); //default event log text file
        File file1 = new File("General_Logs.txt");  //default general log text file
        if (file.createNewFile())   //if no file exists, we create one
        {
            System.out.println("New Event Logs created!");
        }
        if (file1.createNewFile())   //if no file exists, we create one
        {
            System.out.println("New General Logs created!");
        }
        FileWriter fw = new FileWriter(file, true);   //true means while is appended
        FileWriter fw1 = new FileWriter(file1, true);

        // User enter SQL queries, if not "exit" continue to match if it is a SQL statement
        while (sc.hasNext() && !((input = sc.nextLine()).equalsIgnoreCase("exit"))) {
            // Use matchers to match the regex patterns we wrote
            Matcher createTableSQL = CREATE_TABLE_PATTERN.matcher(input);
            Matcher dropTableSQL = DROP_TABLE_PATTERN.matcher(input);
            Matcher selectSQL = SELECT_PATTERN.matcher(input);
            Matcher insertSQL = INSERT_PATTERN.matcher(input);
            Matcher deleteSQL = DELETE_PATTERN.matcher(input);
            Matcher updateSQL = UPDATE_PATTERN.matcher(input);
            Matcher alterPKSQL = ALTER_PK_PATTERN.matcher(input);
            Matcher alterFKSQL = ALTER_FK_PATTERN.matcher(input);

            if (createTableSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                create(createTableSQL, username, fw, fw1);
            } else if (dropTableSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                drop(dropTableSQL, username, fw, fw1);
            } else if (selectSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                select(selectSQL, username, fw, fw1);
            } else if (insertSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                insert(insertSQL, username, fw, fw1);
            } else if (deleteSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                delete(deleteSQL, username, fw, fw1);
            } else if (updateSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                update(updateSQL, username, fw, fw1);
            } else if (alterPKSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                alterPK(alterPKSQL, username, fw, fw1);
            } else if (alterFKSQL.find()) {
                fw.append("[User query][").append(username).append("] ").append(input).append("\n");
                alterFK(alterFKSQL, username, fw, fw1);
            } else if (input.equalsIgnoreCase("dump")){
                fw.append("[Create Dump][").append(username).append("] ").append(input).append("\n");
                long start = System.nanoTime();  // Get the start Time
                // Link user to actions
                int dumped = action.dump(username);
                long end = System.nanoTime();  // Get the end Time
                long executionTime = end - start;  // Calculate the execution Time
                fw1.append("[Execution Time][").append(username).append("] Time used for CREATE DUMP FILE is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
                if (dumped == 0) {
                    fw.append("[Error][").append(username).append("] Unable to create dump file").append("\n");
                    System.out.println("The dump file cannot be created.");
                } else {
                    fw.append("[Change][").append(username).append("] Dump file created by ").append(username).append("\n");
                    System.out.println("Dump file created by " + username);
                }
            } else {
                // Nothing matches with Regex patterns
                fw.append("[User query error][").append(username).append("] ").append(input).append(" is not in standard SQL format").append("\n");
                System.out.println("Please make sure the input is in standard SQL format.\n" + input + " is not valid.");
            }
            // Link to next input
            System.out.println("Please enter the SQL queries or type 'exit' to quit.");
        }
        fw.close();
        fw1.close();
    }

    // Parse the statements deeper, to prepare for the actual operations

    private static void create(Matcher createTable, String username, FileWriter fw, FileWriter fw1) throws IOException {
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
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int created = action.create(username, tableName, columnName, dataType);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for CREATE is ").append(String.valueOf(executionTime)).append(" nanoseconds \n");
        //if returned 0 then user can't create otherwise print user can create a table
        if (created == 0) {
            fw.append("[Error][").append(username).append("] Unable to create the table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be created.");
        } else {
            fw.append("[Change][").append(username).append("] A new table: ").append(tableName).append(" is created").append("\n");
            System.out.println("The table: " + tableName + " is created by: " + username);
        }
    }

    private static void drop(Matcher dropTable, String username, FileWriter fw, FileWriter fw1) throws IOException {
        String tableName = dropTable.group(2);
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int dropped = action.drop(username, tableName);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for DROP is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        //returns 1 if table was dropped
        //returns 0 if user can't drop the table
        if (dropped == 0) {
            fw.append("[Error][").append(username).append("] Unable to dropped the table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be dropped.");
        } else {
            fw.append("[Change][").append(username).append("] Dropped a table: ").append(tableName).append("\n");
            System.out.println("Dropped a table: " + tableName + " by: " + username);
        }
    }

    private static void select(Matcher select, String username, FileWriter fw, FileWriter fw1) throws IOException {
        String fieldNames = select.group(2);
        String[] fieldNamesString = fieldNames.split("\\s*,\\s*");
        List<String> fieldNamesStringList = Arrays.asList(fieldNamesString);
        String tableName = select.group(3);
        String conditionName = "";
        String conditionValue = "";
        if (select.group(4) != null) {
            String condition = select.group(5);
            String[] conditionString = condition.split("\\s*=\\s*");
            conditionName = conditionString[0];
            conditionValue = conditionString[1];
        }
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int selected = action.select(username, tableName, fieldNamesStringList, conditionName, conditionValue);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for SELECT is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        if (selected == 0) {
            fw.append("[Error][").append(username).append("] Unable to select values from table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be selected.");
        } else {
            fw.append("[Change][").append(username).append("] Values are selected from table: ").append(tableName).append("\n");
            System.out.println("Values are selected from table: " + tableName + " by: " + username);
        }

    }

    private static void insert(Matcher insert, String username, FileWriter fw, FileWriter fw1) throws IOException {

        String tableName = insert.group(2);
        String keys = insert.group(3);
        String[] columnName = keys.split("\\s*,\\s*");
        List<String> columnNameList = Arrays.asList(columnName);
        // Separate keys into a arraylist
        String values = insert.group(4);
        String[] columnValue = values.split("\\s*,\\s*");
        List<String> columnValueList = Arrays.asList(columnValue);
        // Separate values into a arraylist
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int inserted = action.insert(username, tableName, columnNameList, columnValueList);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for INSERT is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        //returns 0 if no changes allowed to make
        if (inserted == 0) {
            fw.append("[Error][").append(username).append("] Values cannot be inserted into table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be inserted.");
        } else {
            fw.append("[Change][").append(username).append("] Values are inserted into table: ").append(tableName).append("\n");
            System.out.println("Values are inserted into table: " + tableName + " by: " + username);
        }

    }

    private static void delete(Matcher delete, String username, FileWriter fw, FileWriter fw1) throws IOException {
        String tableName = delete.group(2);
        String condition = delete.group(3);
        String[] conditionString = condition.split("\\s*=\\s*");
        String conditionName = conditionString[0];
        String conditionValue = conditionString[1];
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int deleted = action.delete(username, tableName, conditionName, conditionValue);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for DELETE is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        if (deleted == 0) {
            fw.append("[Error][").append(username).append("] Unable to deleted values in table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be deleted.");
        } else {
            fw.append("[Change][").append(username).append("] Deleted values in table: ").append(tableName).append("\n");
            System.out.println("Deleted values in table: " + tableName + " by: " + username);
        }
    }

    private static void update(Matcher update, String username, FileWriter fw, FileWriter fw1) throws IOException {
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
        long start = System.nanoTime();  // Get the start Time
        // Link user to actions
        int updated = action.update(username, tableName, column, value, conditionName, conditionValue);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for UPDATE is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        if (updated == 0) {
            fw.append("[Error][").append(username).append("] Unable to update values in table: ").append(tableName).append("\n");
            System.out.println("The table: " + tableName + " cannot be updated.");
        } else {
            fw.append("[Change][").append(username).append("] The table: ").append(tableName).append(" is updated").append("\n");
            System.out.println("The table: " + tableName + " is updated by: " + username);
        }
    }

    private static void alterPK(Matcher alterPKSQL, String username, FileWriter fw, FileWriter fw1) throws IOException {
        String tableName = alterPKSQL.group(2);
        String primaryKey = alterPKSQL.group(3);
        long start = System.nanoTime();  // Get the start Time
        int alterPK = action.alter_primary(username, tableName, primaryKey);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for ALTER PK is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        if (alterPK == 0) {
            fw.append("[Error][").append(username).append("] Primary key: ").append(primaryKey).append(" cannot be added to table: ").append(tableName).append("\n");
            System.out.println("Primary key: " + primaryKey + " cannot be added to table: " + tableName);
        } else {
            fw.append("[Change][").append(username).append("] Primary key: ").append(primaryKey).append(" is added to table: ").append(tableName).append("\n");
            System.out.println("Primary key: " + primaryKey + " is added to table: " + tableName + " by: " + username);
        }
    }

    private static void alterFK(Matcher alterFKSQL, String username, FileWriter fw, FileWriter fw1) throws IOException {
        String tableName1 = alterFKSQL.group(2);
        String foreignKey = alterFKSQL.group(3);
        String tableName2 = alterFKSQL.group(4);
        String primaryKey = alterFKSQL.group(5);
        long start = System.nanoTime();  // Get the start Time
        int alterFK = action.alter_foreign(username, tableName1, foreignKey, tableName2, primaryKey);
        long end = System.nanoTime();  // Get the end Time
        long executionTime = end - start;  // Calculate the execution Time
        fw1.append("[Execution Time][").append(username).append("] Time used for ALTER FK is ").append(String.valueOf(executionTime)).append(" nanoseconds\n");
        if (alterFK == 0) {
            fw.append("[Error][").append(username).append("] Foreign key: ").append(foreignKey).append(" cannot be added to table: ").append(tableName1).append("\n");
            System.out.println("Foreign key: " + foreignKey + " cannot be added between table: " + tableName1 + " and table: " + tableName2);
        } else {
            fw.append("[Change][").append(username).append("] Primary key: ").append(foreignKey).append(" is added to table: ").append(tableName1).append("\n");
            System.out.println("Foreign key: " + foreignKey + " is added between table: " + tableName1 + "and table: " + tableName2 + " by: " + username);
        }

    }
}
