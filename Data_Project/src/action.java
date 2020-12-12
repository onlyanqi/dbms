import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class action {


    //to create a table
    public static int create(String username, String tablename, ArrayList<String> columns, ArrayList<String> values) {
        try {
            File ddc = new File("Data_Dictionary.txt");
            FileWriter fw_ddc = new FileWriter(ddc, true);    //appending data dictionary allowed
            if (ddc.createNewFile()) //if no file exists for data dictionary we create one
            {
                System.out.println("Data Dictionary Created");
            }

            FileReader fr_ddc = new FileReader(ddc);   //Now that we have a dictionary,  we traverse it to check if no same user has the same table
            BufferedReader br_ddc = new BufferedReader(fr_ddc);
            String ddc_line = br_ddc.readLine();
            while (ddc_line != null) {
                if (ddc_line.equalsIgnoreCase(username)) {
                    ddc_line = br_ddc.readLine();  //checking the table name associated with that user

                    if (ddc_line.equalsIgnoreCase(tablename)) {
                        System.out.println("User already associated with table!");
                        return 0;
                    }
                    continue;     //no need to change line again
                } //end of if checking that whether the username already exists
                //user record should be added if we're here


                ddc_line = br_ddc.readLine();  //change line
            }

            //user record passes Data Dictionary check and should be added with new table
            fw_ddc.append(username);    //adding the username first
            fw_ddc.append("\n");
            fw_ddc.append(tablename);   //then the table name
            fw_ddc.append("\n");

            for (int i = 0; i < columns.size(); i++) {
                fw_ddc.append(columns.get(i));   //first the column name
                fw_ddc.append(" ");
                fw_ddc.append(values.get(i));    //then the datatypes
                fw_ddc.append("\n");
            }
            fw_ddc.append("\n");    //empty line to denote end of record
            fw_ddc.close();
            br_ddc.close();

            File file = new File(tablename + ".txt");   //a seperate file to store the data of the table
            file.createNewFile();   //creates that file

            File flock = new File("Table_Locks.txt");
            FileWriter fw_lock = new FileWriter(flock, true);    //appending table and lock
            if (flock.createNewFile()) //if no file exists for data dictionary we create one
            {
                System.out.println("Table Lock File Created");
            }
            fw_lock.append(username).append(" ").append(tablename).append(" ").append("false").append("\n\n");    //adding user name and table name and islocked: false
            fw_lock.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }


    //to insert into table
    public static int insert(String username, String tablename, List<String> column, List<String> values) {
        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File ddc = new File("Data_Dictionary.txt");
                FileReader fr_ddc = new FileReader(ddc);
                BufferedReader br_ddc = new BufferedReader(fr_ddc);
                if (ddc.createNewFile()) //if no file exists for data dictionary we create one
                {
                    System.out.println("Data Dictionary Created");
                }
                //Verify if the user can make the changes to the mentioned table
                String line = br_ddc.readLine();
                int flag = 0;
                while (line != null) {
                    if (line.equalsIgnoreCase(username)) {
                        line = br_ddc.readLine();
                        if (line.equalsIgnoreCase(tablename)) {
                            flag = 1;
                        }
                    }

                    line = br_ddc.readLine();
                }
                fr_ddc.close();
                br_ddc.close();

                if (flag == 1)   //user can make changes in the table
                {
                    File myfile = new File(tablename + ".txt");  //add to the table
                    FileWriter fw = new FileWriter(myfile, true);
                    File fd = new File("Data_Dictionary.txt");
                    FileReader fdr = new FileReader(fd);
                    BufferedReader bdr = new BufferedReader(fdr);
                    String input = bdr.readLine();   //Read from data dictionary to maintain order of insertion in column names
                    List<String> temp = new ArrayList<>();
                    int count = 0;
                    int check = 0;
                    int primarycheck = 0;

                    while (input != null)   //adding values only which are inserted by user. Rest keeping empty
                    {
                        if (input.equalsIgnoreCase(username)) {    //check if username matches in ddc
                            input = bdr.readLine();
                            if (input.equalsIgnoreCase(tablename)) //next line would be the tablename
                            {
                                input = bdr.readLine();   //now moving onto columns
                                while (!input.isBlank())                   //now running till we find next record
                                {
                                    String t[] = input.split(" ");    //split to get the column names

                                    if (t.length > 2)   //could be a primary key
                                    {
                                        if (t[2].equalsIgnoreCase("pk")) //it is a primary key column. Needs special processing
                                        { //if yes then we have to make sure no duplicate values are entered

                                            File table = new File(tablename + ".txt");
                                            FileReader tablefr = new FileReader(table);
                                            BufferedReader tablebr = new BufferedReader(tablefr);
                                            List<String> col1 = new ArrayList<>();
                                            String run = tablebr.readLine();  //run through the data stored in that column in the entire table
                                            while (run != null)  //check if that column has any duplicate values
                                            {
                                                if (!run.isBlank()) {
                                                    String r[] = run.split(" ");
                                                    String tablecolumn = r[0];
                                                    if (tablecolumn.equalsIgnoreCase(t[0]))  //check if it's the same column as the one with primary key
                                                    {
                                                        col1.add(r[1]); //store the values already present in the table for the primary key column
                                                    }
                                                }
                                                run = tablebr.readLine();
                                            }


                                            if (column.contains(t[0])) //checks if the pk column has any value being inserted into it
                                            {
                                                int index = column.indexOf(t[0]); //get the index of column if it is being inserted into with a new value

                                                for (String s : col1)  //check for duplicates in that column. Both that already exist and the new values being added
                                                {
                                                    if (values.get(index).equalsIgnoreCase(s)) {
                                                        System.out.println("Duplicate value exists while entering in primary key column!");
                                                        unlock(username, tablename);
                                                        return 0;
                                                    }
                                                }
                                            }

                                            tablebr.close();
                                            tablefr.close();
                                        }//not a primary key then reach directly here
                                    }//length less than 2 so no primary key.
                                    temp.add(t[0]);  //Store the column names and order of column names
                                    input = bdr.readLine();   //runs till we encounter the end of block
                                }

                                //now temp has the order of column names as stored in data dictionary
                                for (int i = 0; i < temp.size(); i++) {
                                    for (int j = 0; j < column.size(); j++) {
                                        if (temp.get(i).equalsIgnoreCase(column.get(j))) {
                                            count = j;
                                            check = 1;   //lets temp know when to keep the column empty.If no value is given
                                        }
                                    }
                                    fw.append(temp.get(i));  //write to table data. Writes the column name
                                    fw.write(" ");       //a space
                                    if (check == 1) {
                                        fw.append(values.get(count));  //write into table data
                                    } else {
                                        fw.write("");        //empty if no value given
                                    }
                                    fw.write("\n");    //change of line
                                    count = 0;
                                    check = 0;
                                } //runs loop till entire table column is filled

                                fw.write("\n");  //empty line once all columns are filled
                            }
                            //if tablename not matched with the user continue as we already have the next line
                        }//if username not matched
                        input = bdr.readLine();
                    }//end of while traversing the data dictionary to fill columns

                    bdr.close();
                    fw.close();
                }//end of if
                else //if no user linked with tablename
                {
                    System.out.println("No permissions found!");
                    unlock(username, tablename);
                    return 0;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlock(username, tablename);
        return 1;
    }


    //to update the table
    public static int update(String username, String tablename, List<String> column, List<String> values, String conditionName, String conditionVal) {

        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                TimeUnit.SECONDS.sleep(20); // wait for 20 second to start doing actual operation
                File ddc = new File("Data_Dictionary.txt");
                FileReader fr_ddc = new FileReader(ddc);
                BufferedReader br_ddc = new BufferedReader(fr_ddc);
                if (ddc.createNewFile()) //if no file exists for data dictionary we create one
                {
                    System.out.println("Data Dictionary Created");
                    unlock(username, tablename);
                    return 0;
                }

                //Verify if the user can make the changes to the mentioned table
                String line = br_ddc.readLine();  //read from data dictionary
                ArrayList<String> primarykey = new ArrayList<>();  //store the column name with the primary key tag. To use later
                int flag = 0;  //1 if user can make a change

                while (line != null) {
                    if (line.equalsIgnoreCase(username)) {
                        line = br_ddc.readLine();
                        if (line.equalsIgnoreCase(tablename)) {
                            flag = 1;
                            line = br_ddc.readLine();
                            while (line != null) {
                                String s[] = line.split(" ");
                                if (s.length > 2) {
                                    if (s[2].equalsIgnoreCase("pk")) {
                                        primarykey.add(s[2]);//got the primary key column
                                        break;
                                    }
                                }
                                line = br_ddc.readLine();
                            }
                        }//if tablename isn't the same
                    }
                    line = br_ddc.readLine();
                }
                fr_ddc.close();
                br_ddc.close();


                if (flag == 1)   //user can make changes in the table
                {

                    for (String s : primarykey) {
                        List<String> col1 = new ArrayList<>();
                        //first check for primary column values
                        File table = new File(tablename + ".txt");
                        FileReader tablefr = new FileReader(table);
                        BufferedReader tablebr = new BufferedReader(tablefr);
                        String run = tablebr.readLine();  //run through the data stored in that column in the entire table
                        while (run != null)  //check if that column has any duplicate values
                        {
                            if (!run.isBlank()) {
                                String temp[] = run.split(" ");
                                if (s.equalsIgnoreCase(temp[0]))  //get content of one primary key
                                {
                                    col1.add(temp[1]);
                                }
                            }
                            run = tablebr.readLine();
                        }

                        for (String t : col1) {
                            int index = column.indexOf(s);  //get index of column user is updating to fetch the corresponding value
                            if (t.equalsIgnoreCase(values.get(index))) {
                                System.out.println("Duplicate values not allowed in primary key column");
                                unlock(username, tablename);
                                return 0;
                            }
                        }
                        tablebr.close();
                        tablefr.close();
                    }


                    //if we reached here then primary column condition is fine
                    File myfile = new File(tablename + ".txt");
                    FileReader myfilereader = new FileReader(myfile);
                    BufferedReader br_myfile = new BufferedReader(myfilereader);
                    String txtline = br_myfile.readLine();
                    int count = 0;  //count for no of blocks of record
                    int block = 0;
                    //first get the record number block where we have to make the change

                    while (txtline != null) {
                        if (!txtline.isBlank()) {
                            String s[] = txtline.split(" ");
                            if (s[0].equalsIgnoreCase(conditionName)) {

                                if (s[1].equalsIgnoreCase(conditionVal)) {
                                    count++;
                                    block = count;
                                    break;
                                }
                            }

                        } else {
                            count++;  //line is blank so count one block
                        }
                        txtline = br_myfile.readLine();  //next line
                    }//now we know which block number we have to change to

                    if (block == 0) {
                        System.out.println("Condition check failed!");
                        unlock(username, tablename);
                        return 0;
                    }

                    br_myfile.close();
                    myfilereader.close();


                    FileReader fr = new FileReader(myfile);
                    BufferedReader br = new BufferedReader(fr);  //the actual data text file
                    File temp = new File("temp.txt");  //the file where we will copy
                    if (temp.createNewFile()) //if no file exists for new table data we create one
                    {
                        //new temporary file created
                    }
                    FileWriter fw_temp = new FileWriter(temp, true);
                    String tempinput = br.readLine();  //from the actual data text file
                    int checkno = 1;//checks the block number. start with 1
                    List<String> order = new ArrayList<>();
                    int check = 0;
                    int flag1 = 0;

                    while (tempinput != null)   //run through entire data text file and copy to new one
                    {
                        if (checkno == block)   //if block number is same
                        {
                            flag1 = 0;
                            if (!tempinput.isBlank())  //if the current line is not blank. Blank would mean a change of block
                            {
                                String s[] = tempinput.split(" ");  //get current column name i.e column then value, so s[0]=column

                                for (int i = 0; i < column.size(); i++)  //match current column to see if it needs an updated value
                                {
                                    if (s[0].equalsIgnoreCase(column.get(i)))   //see if that column is updated
                                    {
                                        check = i;  //gives the index of column
                                        flag1 = 1;  //tells us there is an update here
                                        break;
                                    }
                                }
                                if (flag1 == 0)  //no column found in the block that is to be updated compared to current line
                                {
                                    fw_temp.write(tempinput);   //no update so write line as it was
                                    fw_temp.write("\n");
                                } else //if update found, flag1==1
                                {
                                    fw_temp.write(s[0]);   //column name
                                    fw_temp.write(" ");
                                    fw_temp.write(values.get(check));  //enter the new value
                                    fw_temp.write("\n");
                                }
                            } else {
                                //if line within the same block was blank
                                fw_temp.write(tempinput);
                                fw_temp.write("\n");
                                checkno++;
                            }
                            tempinput = br.readLine(); //change to new line
                        } else {
                            fw_temp.write(tempinput);
                            fw_temp.write("\n");
                            //if a different block then just copy as it is
                            if (tempinput.isBlank()) {
                                checkno++;    //change the block number when new line encountered
                                fw_temp.write("\n");
                            }
                            tempinput = br.readLine();
                        }

                    }//end of while that runs through the entire datatext file
                    br.close();
                    fr.close();
                    fw_temp.close();
                    myfile.delete();
                    temp.renameTo(new File(tablename + ".txt"));
                } //end of if that tells if user is allowed to make changes
                else {
                    unlock(username, tablename);
                    return 0;
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlock(username, tablename);
        return 1;

    }


    //to select/print the value
    public static int select(String username, String tablename, List<String> column, String conditionName, String conditionVal) {

        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File myfile = new File(tablename + ".txt");  //process the table name file
                if (!myfile.exists()) {
                    System.out.println("Table doesn't exist!");
                    unlock(username, tablename);
                    return 0;
                }
                FileReader myfilereader = new FileReader(myfile);
                BufferedReader br_myfile = new BufferedReader(myfilereader);
                String txtline = br_myfile.readLine();
                int count = 0;  //count for no of blocks of record
                int block = 0;
                int flag = 0; //0 when no columns
                int flag1 = 0; //0 when no conditions

                //select columns from tablename where=
                if (!conditionName.equalsIgnoreCase("") && !conditionVal.equalsIgnoreCase(""))   //Checking if both conditions are passed
                {
                    //if both the values are passed we check for block number
                    while (txtline != null)    //we look for the matching block
                    {
                        if (!txtline.isBlank())    //if line isn't blank we match for conditions
                        {
                            String s[] = txtline.split(" ");
                            if (s[0].equalsIgnoreCase(conditionName))    //column and then the value
                            {

                                if (s[1].equalsIgnoreCase(conditionVal)) {
                                    count++;
                                    block = count;
                                    flag1 = 1;   //we got the correct conditions
                                    break;  //as soon as we get the block number
                                }
                            }

                        } else {
                            count++;  //line is blank so count one block
                        }
                        txtline = br_myfile.readLine();  //next line to keep the loop running
                        flag1 = -1;   //wrong conditions. Would only continue if it didn't break
                    }//now we know which block number we have to read
                }//end of if that checks when conditions aren't empty


                //checks for columns
                if (column.get(0).equalsIgnoreCase("*")) {
                    flag = 0;    //print everything where conditions matches
                } else {
                    flag = 1;  //specific columns

                }

                br_myfile.close();
                myfilereader.close();
                //but if conditions are empty
                //select* from tablename;  //empty conditions

                if (flag1 == -1)  //wrong conditions passed
                {
                    System.out.println("No valid condition passed!");
                    unlock(username, tablename);
                    return 0;   //no conditon or column matching
                }


                FileReader fr = new FileReader(myfile);
                BufferedReader br = new BufferedReader(fr);  //the actual data text file
                String tempinput = br.readLine();  //from the actual data text file
                int checkno = 1;//checks the block number


                if (flag == 0 && flag1 == 0) //select* from tablename;  no conditions, print all
                {
                    while (tempinput != null) {
                        System.out.println(tempinput);
                        tempinput = br.readLine();
                    }
                    br.close();
                    unlock(username, tablename);
                    return 1;
                }


                int block_check = 1; //check the block number
                if (flag == 0 && flag1 == 1) //select* from tablename where... ; print but the specified block of record/row
                {
                    while (tempinput != null) {
                        if (block == block_check) {
                            System.out.println(tempinput);
                            tempinput = br.readLine();
                            if (tempinput.isEmpty()) {
                                block_check++;
                                tempinput = br.readLine();
                            }
                        } else {
                            tempinput = br.readLine();
                            if (tempinput == null) {
                                unlock(username, tablename);
                                return 1;
                            }
                            if (tempinput.isEmpty()) {
                                block_check++;
                                tempinput = br.readLine();  //we don't print the empty line
                            }
                        }
                    }

                    br.close();
                    unlock(username, tablename);
                    return 1;
                }


                if (flag == 1 && flag1 == 1)  // select columns from tablename where... ;  both the conditions and specific columns
                {

                    while (tempinput != null) {
                        if (block == block_check)   //same block
                        {
                            String s[] = tempinput.split(" ");   //get the column name and its value
                            for (int i = 0; i < column.size(); i++) {
                                if (column.get(i).equalsIgnoreCase(s[0]))     //match the column name
                                {
                                    System.out.println(s[1]);  //print the value
                                }
                            }
                            tempinput = br.readLine();
                            if (tempinput == null) {
                                unlock(username, tablename);
                                return 1;
                            }
                            if (tempinput.isEmpty()) {
                                tempinput = br.readLine();
                                block_check++;
                                unlock(username, tablename);
                                return 1;
                            }// end of same block if

                        } else //if different block
                        {
                            tempinput = br.readLine();
                            if (tempinput.isEmpty()) {
                                tempinput = br.readLine();
                                block_check++;
                            }
                        }
                    }
                }

                if (flag == 0 && flag1 == 1) //select columns from tablename; Here columns are there but no tablename
                {
                    while (tempinput != null) {
                        String s[] = tempinput.split(" ");
                        for (int i = 0; i < column.size(); i++) {
                            if (column.get(i).equalsIgnoreCase(s[0])) {
                                System.out.println(s[1]);  //print the value whenever column matches
                            }
                        }
                        tempinput = br.readLine();
                        if (tempinput.isEmpty()) {
                            tempinput = br.readLine(); //no need to process empty line
                        }
                    }
                    br.close();
                    unlock(username, tablename);
                    return 1;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


    //Add the primary key
    public static int alter_primary(String username, String tablename, String column) {
        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File Dictionary = new File("Data_Dictionary.txt");
                FileReader Dicitonary_fr = new FileReader(Dictionary);
                BufferedReader Dictionary_br = new BufferedReader(Dicitonary_fr);
                String line = Dictionary_br.readLine();
                int flag = 0;  //1 if the tablename matches. Tells us user can make changes

                while (line != null) {
                    if (line.equalsIgnoreCase(username))  //if same username
                    {
                        line = Dictionary_br.readLine();
                        if (line.equalsIgnoreCase(tablename))  //if the same tablename too, the user can make changes
                        {
                            line = Dictionary_br.readLine();     //line now goes through columns

                            while (!line.isBlank())  //now loop over the lines of columns until we enter a new block. Blank line means a change of block
                            {
                                String s[] = line.split(" ");    //check if primary key is the column we're checking
                                if (column.equalsIgnoreCase(s[0]))   //checks if the column is there or not in the table
                                {
                                    if (s.length > 2)  //there has to be another value in array
                                    {
                                        if (s[2].equalsIgnoreCase("pk")) {
                                            System.out.println("Key already Set");
                                            unlock(username, tablename);
                                            return 0;
                                        }
                                    } else   //we can set a primary key
                                    {
                                        flag = 1;
                                        break;   //because the column name matched and had no primary key
                                    }
                                } else   //if the column name didn't match. This is within the if after matching the tablename
                                {
                                    line = Dictionary_br.readLine();  //if no primary key in this set of column move ahead
                                    if (line.isBlank()) {
                                        //end of the block. If no column even though the username and table matched
                                        System.out.println("Wrong column entered");
                                        break;  //as there can only be one user associated with the table
                                    }
                                }
                            }//end of while that runs matching columns after username and tablename was matched

                        }//tablename didn't match
                    }
                    if (flag == 1) {
                        break;
                    }

                    //if username didn't match continue processing loop
                    line = Dictionary_br.readLine();
                }//end of while for Data Dictionary

                Dictionary_br.close();
                Dicitonary_fr.close();


                if (flag == 1) //no primary key set and user can make change and also column exists
                {
                    //first check if the column has unique values
                    File table = new File(tablename + ".txt");
                    FileReader table_fr = new FileReader(table);
                    BufferedReader table_br = new BufferedReader(table_fr);
                    String input = table_br.readLine();
                    List<String> values1 = new ArrayList<>();  //stores all values in column to match for duplicate
                    List<String> values2 = new ArrayList<>();  //Other duplicate array to loop through

                    //We check for duplicate values, by copying of the mentioned column to the arraylist
                    while (input != null) {
                        if (!input.isBlank()) {
                            String s[] = input.split(" ");
                            if (column.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                            {
                                values1.add(s[1]);
                            }
                        }
                        input = table_br.readLine();
                    }

                    table_br.close();
                    table_fr.close();


                    //Now check if any duplicate value in column or not
                    Set<String> valueSet = new HashSet<>();
                    for (String temp : values1) {
                        if (!valueSet.add(temp)) {
                            System.out.println("Duplicates found in column");
                            unlock(username, tablename);
                            return 0;
                        }
                    }


                    //No duplicate value exists. Add this column as primary key to Data Dictionary
                    FileReader fr = new FileReader(Dictionary);
                    BufferedReader br = new BufferedReader(fr);
                    File temp = new File("temp.txt");   //temporary file to copy into new dictionary
                    if (temp.createNewFile()) {
                        //creates a new file if not there
                    }
                    FileWriter fw = new FileWriter(temp, true);  //write into the temporary file
                    String tempinput = br.readLine();  //read from actual dictionary file
                    while (tempinput != null) {
                        if (tempinput.equalsIgnoreCase(username)) //first match the username
                        {
                            fw.write(tempinput); //write it to new file
                            fw.write("\n");
                            tempinput = br.readLine(); //change the line
                            if (tempinput.equalsIgnoreCase(tablename))  //now match the tablename
                            {
                                fw.write(tempinput);
                                fw.write("\n");
                                tempinput = br.readLine();  //now match the columns after both username and column name was verified
                                while (!tempinput.isBlank()) {
                                    String s[] = tempinput.split(" ");  //get the column names,datatype etc
                                    if (s[0].equalsIgnoreCase(column)) {
                                        fw.write(tempinput);
                                        fw.write(" PK");//write the column name
                                        fw.write("\n");
                                    } else {
                                        //if the column name didn't match then copy as it is
                                        fw.write(tempinput);
                                        fw.write("\n");
                                    }
                                    tempinput = br.readLine();  //process remaining columns till we hit a new block as shown by the blank condition
                                }
                            } //if tablename didn't match
                            fw.write(tempinput);
                            fw.write("\n");
                        } else   //if the username didn't match
                        {
                            fw.write(tempinput);   //copy the none matched line
                            fw.write("\n");
                        }
                        tempinput = br.readLine();
                    }

                    br.close();
                    fr.close();
                    fw.close();
                    Dictionary.delete();  //delete old data dictionary
                    temp.renameTo(new File("Data_Dictionary.txt"));   //rename temp file to Data Dictionary

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlock(username, tablename);
        return 1;
    }


    //Add the foreign key
    public static int alter_foreign(String username, String tableName1, String key1, String tableName2, String key2) {
        try {
            if (lock_check(username, tableName1)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File Dictionary = new File("Data_Dictionary.txt");
                FileReader dataFR = new FileReader(Dictionary);
                BufferedReader dataBR = new BufferedReader(dataFR);
                String line = dataBR.readLine();
                int flag1 = 0; // 1 if the tableName1 matches.
                int flag2 = 0; // 1 if the tableName2 matches.
                int flag3 = 0; // 1 if values of key2 in table 2 contains all the values of key1 in table 1.
                int flag4 = 0; // 1 if values of key1 in table 1 have more than 1 values

                while (line != null) {
                    if (line.equalsIgnoreCase(username))  //if same username
                    {
                        line = dataBR.readLine();
                        if (line.equalsIgnoreCase(tableName1))  //if the same tableName, see if the column name exists
                        {
                            line = dataBR.readLine();     //line now goes through columns

                            while (!line.isBlank())  //now loop over the lines of columns until we enter a new block. Blank line means a change of block
                            {
                                String s[] = line.split(" ");    //check if primary key is the column we're checking
                                if (key1.equalsIgnoreCase(s[0]))   //checks if the column is there or not in the table
                                {
                                    if (s.length > 2)  //there has to be another value in array
                                    {
                                        if (s[2].equalsIgnoreCase("pk")) {
                                            System.out.println(key1 + " is already a Primary Key in " + tableName1);
                                            unlock(username, tableName1);
                                            return 0;
                                        } else if (s[2].equalsIgnoreCase("fk")) {
                                            System.out.println(key1 + " is already a Foreign Key in " + tableName1);
                                            unlock(username, tableName1);
                                            return 0;
                                        }
                                    } else {
                                        flag1 = 1;
                                        break;   //because the column name matched and had no primary key and foreign key
                                    }
                                } else   //if the column name didn't match. This is within matching the table name1
                                {
                                    line = dataBR.readLine();  //if no primary key in this set of column move ahead
                                    if (line.isBlank()) {
                                        //end of the block. If no column matches even though the username and table matched
                                        System.out.println("The column entered is not exist in table 1");
                                        unlock(username, tableName1);
                                        return 0;  //as there can only be one user associated with the table
                                    }
                                }
                            }//end of while that runs matching columns after username and tablename was matched
                        }//table name1 didn't match
                        if (line.equalsIgnoreCase(tableName2)) {
                            line = dataBR.readLine();
                            if (!line.isBlank()) {
                                String s[] = line.split(" ");
                                if (key2.equalsIgnoreCase(s[0]))  //checks if the column is there or not in the table
                                {
                                    if (s.length > 2)  //there has to be another value in array
                                    {
                                        if (s[2].equalsIgnoreCase("pk")) // key2 is PK in table2
                                        {
                                            flag2 = 1;
                                        }
                                    } else {
                                        System.out.println(key2 + " is not set as a Primary Key in " + tableName2);
                                        unlock(username, tableName1);
                                        return 0;
                                    }
                                } else //if the column name didn't match. This is within matching the table name2
                                {
                                    line = dataBR.readLine();  //if no primary key in this set of column move ahead
                                    if (line.isBlank()) {
                                        //end of the block. If no column matches even though the username and table matched
                                        System.out.println("The column entered is not exist in table 2");
                                        unlock(username, tableName1);
                                        return 0;  //as there can only be one user associated with the table
                                    }
                                }
                            }
                        }
                    } //if username didn't match continue processing loop
                    line = dataBR.readLine();
                }//end of while for Data Dictionary

                dataBR.close();
                dataFR.close();

                if (flag1 == 1 && flag2 == 1) // key1 exists in table1, not already a PK or FK, and key2 is PK in table2
                // now we need to check if all data in this column of child table is shown in parent table
                {
                    File table1 = new File(tableName1 + ".txt");
                    FileReader table1_fr = new FileReader(table1);
                    BufferedReader table1_br = new BufferedReader(table1_fr);
                    String line1 = table1_br.readLine();

                    File table2 = new File(tableName2 + ".txt");
                    FileReader table2_fr = new FileReader(table2);
                    BufferedReader table2_br = new BufferedReader(table2_fr);
                    String line2 = table2_br.readLine();

                    List<String> values1 = new ArrayList<>();  //stores all values in column of table1
                    List<String> values2 = new ArrayList<>();  //stores all values in column of table2

                    while (line1 != null) {
                        if (!line1.isBlank()) {
                            String s[] = line1.split(" ");
                            if (key1.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                            {
                                values1.add(s[1]);
                            }
                        }
                        line1 = table1_br.readLine();
                    }

                    while (line2 != null) {
                        if (!line2.isBlank()) {
                            String s[] = line2.split(" ");
                            if (key2.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                            {
                                values2.add(s[1]);
                            }
                        }
                        line2 = table2_br.readLine();
                    }

                    if (values1.size() > 1) {
                        flag4 = 1;
                    }

                    if (values2.containsAll(values1)) {
                        flag3 = 1;
                    }

                    table1_br.close();
                    table1_fr.close();
                    table2_br.close();
                    table2_fr.close();
                }

                if (flag3 == 0) {
                    System.out.println("Values in child table cannot be more than those in parent table");
                    unlock(username, tableName1);
                    return 0;
                }

                if (flag3 == 1) {
                    FileReader fr = new FileReader(Dictionary);
                    BufferedReader br = new BufferedReader(fr);
                    File temp = new File("temp.txt");   //temporary file to copy into new dictionary
                    if (temp.createNewFile()) {
                        //creates a new file if not there
                    }
                    FileWriter fw = new FileWriter(temp, true);  //write into the temporary file
                    String tempinput = br.readLine();  //read from actual dictionary file
                    while (tempinput != null) {
                        if (tempinput.equalsIgnoreCase(username)) //first match the username
                        {
                            fw.write(tempinput); //write it to new file
                            fw.write("\n");
                            tempinput = br.readLine(); //change the line
                            if (tempinput.equalsIgnoreCase(tableName1))  //now match the table name 1
                            {
                                fw.write(tempinput);
                                fw.write("\n");
                                tempinput = br.readLine();  //now match the columns after both username and column name was verified
                                while (!tempinput.isBlank()) {
                                    String s[] = tempinput.split(" ");  //get the column names,datatype etc
                                    if (s[0].equalsIgnoreCase(key1)) {
                                        fw.write(s[0]);  //write the column name
                                        fw.write(" ");
                                        fw.write(s[1]);  //the datatype
                                        fw.write(" FK");//add the Foreign Key marker at the end
                                        fw.write("\n"); //new line
                                    } else {
                                        //if the column name didn't match then copy as it is
                                        fw.write(tempinput);
                                        fw.write("\n");
                                    }
                                    tempinput = br.readLine();  //process remaining columns till we hit a new block as shown by the blank condition
                                }
                            } //if tablename didn't match
                            fw.write(tempinput);
                            fw.write("\n");
                        } else   //if the username didn't match
                        {
                            fw.write(tempinput);   //copy the none matched line
                            fw.write("\n");
                        }
                        tempinput = br.readLine();
                    }

                    br.close();
                    fr.close();
                    fw.close();
                    Dictionary.delete();  //delete old data dictionary
                    temp.renameTo(new File("Data_Dictionary.txt"));   //rename temp file to Data Dictionary


                    // write a relationship file (ERD)
                    File relationFile = new File(username + "_Relationship.txt");
                    if (relationFile.createNewFile())   //if no file exists, we create one
                    {
                        System.out.println("New Relationship file created!");
                    }
                    FileWriter relationFW = new FileWriter(relationFile, true);
                    if (flag4 == 1) {
                        relationFW.append(tableName1).append(" ").append(key1).append(" ").append("FK").append(" many").append("\n");
                    } else {
                        relationFW.append(tableName1).append(" ").append(key1).append(" ").append("FK").append(" 1").append("\n");
                    }
                    relationFW.append(tableName2).append(" ").append(key2).append(" ").append("PK").append(" 1").append("\n").append("\n");
                    relationFW.close();
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlock(username, tableName1);
        return 1;
    }


    //to delete a record in the table
    public static int delete(String username, String tablename, String conditionName, String conditionVal) {
        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File ddc = new File("Data_Dictionary.txt");
                FileReader fr_ddc = new FileReader(ddc);
                BufferedReader br_ddc = new BufferedReader(fr_ddc);
                if (ddc.createNewFile()) //if no file exists for data dictionary we create one
                {
                    System.out.println("Data Dictionary Created");
                }

                //Verify if the user can make the changes to the mentioned table
                String line = br_ddc.readLine();
                int flag = 0;
                while (line != null) {
                    if (line.equalsIgnoreCase(username)) {
                        line = br_ddc.readLine();
                        if (line.equalsIgnoreCase(tablename)) {
                            flag = 1;
                        }
                        continue;
                    }

                    line = br_ddc.readLine();
                }
                fr_ddc.close();
                br_ddc.close();

                if (flag == 1)   //user can make changes in the table
                {

                    File myfile = new File(tablename + ".txt");
                    FileReader myfilereader = new FileReader(myfile);
                    BufferedReader br_myfile = new BufferedReader(myfilereader);
                    String txtline = br_myfile.readLine();
                    int count = 0;  //count for no of blocks of record
                    int block = 0;
                    //first get the record number block where we have to make the change

                    while (txtline != null) {
                        if (!txtline.isBlank()) {
                            String s[] = txtline.split(" ");
                            if (s[0].equalsIgnoreCase(conditionName)) {

                                if (s[1].equalsIgnoreCase(conditionVal)) {
                                    count++;
                                    block = count;
                                    break;
                                }
                            }

                        } else {
                            count++;  //line is blank so count one block
                        }
                        txtline = br_myfile.readLine();  //next line
                    }//now we know which block number we have to change to


                    if (block == 0) {
                        unlock(username, tablename);
                        return 0;    //if no block matched
                    }
                    br_myfile.close();
                    myfilereader.close();


                    FileReader fr = new FileReader(myfile);
                    BufferedReader br = new BufferedReader(fr);  //the actual data text file
                    File temp = new File("temp.txt");  //the file where we will copy
                    if (temp.createNewFile()) //if no file exists for data dictionary we create one
                    {
                        //new temporary file created
                    }
                    FileWriter fw_temp = new FileWriter(temp, true);
                    String tempinput = br.readLine();  //from the actual data text file
                    int checkno = 1;//checks the block number
                    List<String> order = new ArrayList<>();


                    while (tempinput != null)   //run through entire data text file and copy to new one
                    {
                        if (checkno == block)   //if block number is same
                        {

                        }//End of if, if it was the same block
                        else {
                            fw_temp.write(tempinput);
                            fw_temp.write("\n");
                        }

                        if (tempinput.isBlank())   //new block
                        {
                            checkno++;

                        }
                        tempinput = br.readLine();

                    }//end of while that runs through the entire datatext file

                    fw_temp.close();
                    br.close();
                    fr.close();

                    fw_temp.close();
                    myfile.delete();
                    temp.renameTo(new File(tablename + ".txt"));
                } //end of if that tells if user is allowed to make changes
                else {
                    unlock(username, tablename);
                    return 0;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        unlock(username, tablename);
        return 1;


    }


    //to drop the table
    public static int drop(String username, String tablename) {
        int flag = 0;
        try {
            if (lock_check(username, tablename)) {
                System.out.println("Table is locked, please try again later");
                return 0;
            } else {
                File file = new File("Data_Dictionary.txt");  //assuming data dictionary is already there
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                File temp = new File("temp.txt");
                if (temp.createNewFile()) {
                    //creates a new file
                }
                FileWriter fw = new FileWriter(temp, true);
                String line = br.readLine();
                while (line != null) {
                    if (line.equalsIgnoreCase(username)) {
                        line = br.readLine();
                        if (line.equalsIgnoreCase(tablename))  //user can delete the table now
                        {
                            flag = 1;
                            line = br.readLine();
                            while (!line.isBlank()) {
                                line = br.readLine();
                            }//end of record
                        } else {
                            fw.write(username);  //if not the same tablename but same username,first write the username
                            fw.write("\n");
                            fw.write(line);   //pointer at tablename already. A different tablename
                            fw.write("\n");
                        }
                    } else   //if the username didn't match
                    {
                        fw.write(line);   //copy the none matched line
                        fw.write("\n");
                    }
                    line = br.readLine();
                }

                fw.close();
                br.close();
                fr.close();
                File fin = new File(tablename + ".txt");
                fin.delete();   //delete the table text file that contains the data
                file.delete();  //delete old data dictionary
                temp.renameTo(new File("Data_Dictionary.txt"));   //rename temp file to Data Dictionary
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unlock(username, tablename);
        return flag;
    }


    // to create dump file for the user
    public static int dump(String username) {
        int flag = 0;
        try {

            File ddc = new File("Data_Dictionary.txt");
            FileReader fr_ddc = new FileReader(ddc);
            BufferedReader br_ddc = new BufferedReader(fr_ddc);
            if (ddc.createNewFile()) //if no file exists for data dictionary we create one
            {
                System.out.println("Data Dictionary Created");
            }

            File myfile = new File(username + "_dump.txt");
            FileWriter fw = new FileWriter(myfile, true);
            int count = 0;  //count for no of blocks of record
            //first get the record number block where we have to make the change
            String txtline = br_ddc.readLine();

            while (txtline != null) {
                if (!txtline.isBlank()) //if the current line isn't blank we process it
                {
                    if (txtline.equalsIgnoreCase(username)) {
                        txtline = br_ddc.readLine();
                        while (!txtline.isBlank()) {
                            fw.write(txtline);
                            fw.write("\n");
                            txtline = br_ddc.readLine();
                            flag = 1;
                        }
                        fw.write("\n");
                    }
                }
                txtline = br_ddc.readLine();
            }

            br_ddc.close();
            fw.close();
            fr_ddc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    private static boolean lock_check(String username, String tablename) {
        boolean is_locked = false;
        try {
            File lockFile = new File("Table_Locks.txt");
            FileReader lockFR = new FileReader(lockFile);
            BufferedReader lockBR = new BufferedReader(lockFR);
            String line = lockBR.readLine();
            int flag = 0;
            while (line != null) {
                if (!line.isBlank()) {
                    String s[] = line.split(" ");
                    if (username.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                    {
                        if (tablename.equalsIgnoreCase(s[1])) {
                            if (s[2].equalsIgnoreCase("true")) {
                                is_locked = true; // the table is already locked
                            } else if (s[2].equalsIgnoreCase("false")) {
                                // since the table is not locked, and the user want to do operations
                                // we need to lock the table now
                                flag = 1;
                            }
                        }
                    }
                }
                line = lockBR.readLine();
            }
            lockFR.close();
            lockBR.close();

            if (flag == 1) {
                FileReader fr = new FileReader(lockFile);
                BufferedReader br = new BufferedReader(fr);
                File temp = new File("temp.txt");  //the file where we will copy
                temp.createNewFile(); //if no file exists for new table data we create one
                FileWriter fw_temp = new FileWriter(temp, true);
                String tempinput = br.readLine();  //from the actual data text file
                while (tempinput != null)   //run through entire data text file and copy to new one
                {
                    if (!tempinput.isBlank()) {
                        String s[] = tempinput.split(" ");
                        if (username.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                        {
                            if (tablename.equalsIgnoreCase(s[1])) {
                                if (s[2].equalsIgnoreCase("true")) {
                                    is_locked = true; // the table is already locked
                                } else if (s[2].equalsIgnoreCase("false")) {
                                    // since the table is not locked, and the user want to do operations
                                    // we need to lock the table now
                                    fw_temp.append(s[0]).append(" ").append(s[1]).append(" true");
                                    fw_temp.write("\n");
                                }
                            } else {
                                fw_temp.write(tempinput);
                                fw_temp.write("\n");
                            }
                        } else {
                            fw_temp.write(tempinput);
                            fw_temp.write("\n");
                        }
                    }
                    tempinput = br.readLine();

                }//end of while that runs through the entire datatext file
                br.close();
                fr.close();
                fw_temp.close();
                lockFile.delete();
                temp.renameTo(new File("Table_Locks.txt"));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is_locked;
    }

    private static void unlock(String username, String tablename) {
        try {
            File lockFile = new File("Table_Locks.txt");
            FileReader fr = new FileReader(lockFile);
            BufferedReader br = new BufferedReader(fr);
            File temp = new File("temp.txt");  //the file where we will cop y
            temp.createNewFile(); //if no file exists for new table data we create one
            FileWriter fw_temp = new FileWriter(temp, true);
            String tempinput = br.readLine();  //from the actual data text file
            while (tempinput != null)   //run through entire data text file and copy to new one
            {
                if (!tempinput.isBlank()) {
                    String s[] = tempinput.split(" ");
                    if (username.equalsIgnoreCase(s[0]))  //column matched, fetch corresponding value
                    {
                        if (tablename.equalsIgnoreCase(s[1])) {
                            fw_temp.append(s[0]).append(" ").append(s[1]).append(" false");
                            fw_temp.write("\n");
                        } else {
                            fw_temp.write(tempinput);
                            fw_temp.write("\n");
                        }
                    } else {
                        fw_temp.write(tempinput);
                        fw_temp.write("\n");
                    }
                }
                tempinput = br.readLine();

            }//end of while that runs through the entire datatext file
            br.close();
            fr.close();
            fw_temp.close();
            lockFile.delete();
            temp.renameTo(new File("Table_Locks.txt"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
