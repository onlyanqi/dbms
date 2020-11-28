import javax.crypto.Cipher;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class action {


    //Create Table
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
                fw_ddc.append("  ");
                fw_ddc.append(values.get(i));    //then the datatypes
                fw_ddc.append("\n");
            }
            fw_ddc.append("\n");    //empty line to denote end of record
            fw_ddc.close();
            br_ddc.close();

            File file = new File(tablename + ".txt");   //a seperate file to store the data of the table
            file.createNewFile();   //creates that file

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    //to insert into table
    public static int insert(String username, String tablename, List<String> column, List<String> values)
    {
        try{
            File ddc=new File("Data_Dictionary.txt");
            FileReader fr_ddc = new FileReader(ddc);
            BufferedReader br_ddc=new BufferedReader(fr_ddc);
            if (ddc.createNewFile()) //if no file exists for data dictionary we create one
            {
                System.out.println("Data Dictionary Created");
            }

            //Verify if the user can make the changes to the mentioned table
            String line=br_ddc.readLine();
            int flag=0;
            while(line!=null)
            {
                if(line.equalsIgnoreCase(username))
                {
                    line=br_ddc.readLine();
                    if(line.equalsIgnoreCase(tablename))
                    {
                        flag=1;
                    }
                }

                line=br_ddc.readLine();
            }
            fr_ddc.close();
            br_ddc.close();





            if(flag==1)   //user can make changes in the table
            {
                File myfile = new File(tablename + ".txt");
                FileWriter fw = new FileWriter(myfile, true);
                File fd = new File("Data_Dictionary.txt");
                FileReader fdr = new FileReader(fd);
                BufferedReader bdr = new BufferedReader(fdr);
                String input = bdr.readLine();   //from data dictionary
                List<String> temp=new ArrayList<>();
                int count=0;
                int check=0;

                while (input != null)   //adding values only which are inserted by user. Rest keeping empty
                {
                    if (input.equalsIgnoreCase(username))
                    {    //check if username matches in ddc
                        input = bdr.readLine();
                        if (input.equalsIgnoreCase(tablename)) //next line would be the tablename
                        {
                            input = bdr.readLine();   //now moving onto columns
                            while(!input.isBlank())                   //now running till we find next record
                            {
                                String t[]=input.split(" ");    //split to get the column name
                                temp.add(t[0]);  //Store the column names and order of column names
                                input=bdr.readLine();   //runs till we encounter the end of block
                            }

                            //now temp has the order of column names as stored in data dictionary
                            for(int i=0;i<temp.size();i++)
                            {
                                for(int j=0;j<column.size();j++)
                                {
                                    if(temp.get(i).equalsIgnoreCase(column.get(j)))
                                    {
                                        count=j;
                                        check=1;   //lets temp know when to keep the column empty
                                    }
                                }


                                fw.append(temp.get(i));
                                fw.write(" ");       //a space
                                if(check==1)
                                {
                                    fw.append(values.get(count));
                                }
                                else
                                {
                                    fw.write("");        //empty if no value given
                                }
                                fw.write("\n");    //change of line
                                count=0;
                                check=0;
                            } //runs loop till entire table column is filled
                            fw.write("\n");  //empty line once all columns are filled
                        }
                        continue;//if tablename not matched with the user continue as we already have the next line
                    }//if username not matched
                    input = bdr.readLine();
                }//end of while traversing the data dictionary to fill columns
                bdr.close();
                fw.close();
            }//end of if

            else //if no user linked with tablename
            {
                return 0;
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

















    //to update the table
    public static  int update(String username, String tablename,List<String> column, List<String> values, String conditionName, String conditionVal) {

        try {

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
            while (line != null)
            {
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
                String txtline=br_myfile.readLine();
                int count=0;  //count for no of blocks of record
                int block=0;
                //first get the record number block where we have to make the change

                while(txtline!=null)
                {
                    if(!txtline.isBlank())
                    {
                        String s[] = txtline.split(" ");
                        if (s[0].equalsIgnoreCase(conditionName)) {

                            if (s[1].equalsIgnoreCase(conditionVal)) {
                                count++;
                                block = count;
                                break;
                            }
                        }

                    }
                    else {
                        count++;  //line is blank so count one block
                    }
                    txtline = br_myfile.readLine();  //next line
                }//now we know which block number we have to change to
                if(block==0){
                    return 0;
                }
                br_myfile.close();
                myfilereader.close();


                FileReader fr=new FileReader(myfile);
                BufferedReader br=new BufferedReader(fr);  //the actual data text file
                File temp = new File("temp.txt");  //the file where we will copy
                if (temp.createNewFile()) //if no file exists for data dictionary we create one
                {
                    //new temporary file created
                }
                FileWriter fw_temp = new FileWriter(temp, true);
                String tempinput=br.readLine();  //from the actual data text file
                int checkno=1;//checks the block number
                List<String> order=new ArrayList<>();
                int check=0;
                int flag1 = 0;

                while(tempinput!=null)   //run through entire data text file and copy to new one
                {
                    if(checkno==block)   //if block number is same
                    {
                        flag1=0;
                        if(!tempinput.isBlank())
                        {
                            String s[] = tempinput.split(" ");  //get a column name i.e column then value, so s[0]=column

                            for (int i = 0; i < column.size(); i++)  //match current column to see if it needs an updated value
                            {
                                if (s[0].equalsIgnoreCase(column.get(i)))   //see if that column is updated
                                {
                                    check = i;
                                    flag1 = 1;
                                    break;
                                }
                            }
                            if (flag1==0)  //no column found in the block that is to be updated compared to current line
                            {
                                System.out.println(tempinput);
                                fw_temp.write(tempinput);   //no update so write line as it was
                                fw_temp.write("\n");
                            }
                            else //if update found
                            {
                                fw_temp.write(s[0]);   //column name
                                fw_temp.write(" ");
                                fw_temp.write(values.get(check));  //enter the new value
                                fw_temp.write("\n");
                            }
                        }else{
                            //if line was blank
                            fw_temp.write(tempinput);
                            checkno++;
                        }
                        tempinput=br.readLine();
                    }else {
                        //if a different block then just copy as it is
                        if (tempinput.isBlank()) {
                            checkno++;    //change the block number when new line encountered
                        }

                        fw_temp.write(tempinput);
                        fw_temp.write("\n");
                        tempinput = br.readLine();
                    }

                }//end of while that runs through the entire datatext file

                br.close();
                fr.close();

                fw_temp.close();
                myfile.delete();
                temp.renameTo(new File(tablename + ".txt"));
            } //end of if that tells if user is allowed to make changes
            else
            {
                return 0;
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }


        return 1;

    }











    //to select/print the value
    public static int select(String username, String tablename, List<String> column,  String conditionName, String conditionVal)
    {

        try {

            File myfile = new File(tablename + ".txt");  //process the table name file
            if(!myfile.exists())
            {
                System.out.println("Table doesn't exist!");
                return 0;
            }
            FileReader myfilereader = new FileReader(myfile);
            BufferedReader br_myfile = new BufferedReader(myfilereader);
            String txtline=br_myfile.readLine();
            int count=0;  //count for no of blocks of record
            int block=0;
            int flag=0; //0 when no columns
            int flag1=0; //0 when no conditions

            //select columns from tablename where=
            if(!conditionName.equalsIgnoreCase("") && !conditionVal.equalsIgnoreCase(""))   //Checking if both conditions are passed
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
            if(column.get(0).equalsIgnoreCase("*"))
            {
                flag=0;    //print everything where conditions matches
            }
            else
            {
                flag=1;  //specific columns

            }

            br_myfile.close();
            myfilereader.close();
            //but if conditions are empty
            //select* from tablename;  //empty conditions

            if(flag1==-1)  //wrong conditions passed
            {
                System.out.println("No valid condition passed!");
                return 0;   //no conditon or column matching
            }




            FileReader fr=new FileReader(myfile);
            BufferedReader br=new BufferedReader(fr);  //the actual data text file
            String tempinput=br.readLine();  //from the actual data text file
            int checkno=1;//checks the block number


            if(flag==0 && flag1==0) //select* from tablename;  no conditions, print all
            {
                while(tempinput!=null)
                {
                    System.out.println(tempinput);
                    tempinput=br.readLine();
                }
                br.close();
                return 1;
            }





            int block_check=1; //check the block number
            if(flag==0 && flag1==1) //select* from tablename where... ; print but the specified block of record/row
            {
                while (tempinput!=null)
                {
                    if(block==block_check)
                    {
                        System.out.println(tempinput);
                        tempinput=br.readLine();
                        if(tempinput.isEmpty())
                        {
                            block_check++;
                            tempinput=br.readLine();
                        }
                    }
                    else{
                        tempinput=br.readLine();
                        if(tempinput.isEmpty())
                        {
                            block_check++;
                            tempinput=br.readLine();  //we don't print the empty line
                        }
                    }
                }

                br.close();
                return 1;
            }




            if(flag==1 && flag1==1)  // select columns from tablename where... ;  both the conditions and specific columns
            {

                while (tempinput != null)
                {
                    if (block == block_check)   //same block
                    {
                        String s[] = tempinput.split(" ");   //get the column name and its value
                        for (int i = 0; i < column.size(); i++)
                        {
                            if (column.get(i).equalsIgnoreCase(s[0]))     //match the column name
                            {
                                System.out.println(s[1]);  //print the value
                            }
                        }
                        tempinput = br.readLine();
                        if (tempinput.isEmpty())
                        {
                            tempinput = br.readLine();
                            block_check++;
                            return 1;
                        }// end of same block if

                    } else //if different block
                    {
                        tempinput = br.readLine();
                        if (tempinput.isEmpty())
                        {
                            tempinput = br.readLine();
                            block_check++;
                        }
                    }
                }
            }

            if(flag==0 && flag1==1) //select columns from tablename; Here columns are there but no tablename
            {
                while(tempinput!=null)
                {
                    String s[]=tempinput.split(" ");
                    for(int i=0;i< column.size();i++)
                    {
                        if(column.get(i).equalsIgnoreCase(s[0]))
                        {
                            System.out.println(s[1]);  //print the value whenever column matches
                        }
                    }
                    tempinput=br.readLine();
                    if(tempinput.isEmpty())
                    {
                        tempinput=br.readLine(); //no need to process empty line
                    }
                }
                br.close();
                return 1;
            }





        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        return 1;
    }












    //Add the primary key
    public static int alter_primary(String username, String tablename, String column)
    {
        try {
            File Dictionary=new File("Data_Dictionary.txt");
            FileReader Dicitonary_fr=new FileReader(Dictionary);
            BufferedReader Dictionary_br=new BufferedReader(Dicitonary_fr);
            String line=Dictionary_br.readLine();
            int flag=0;
            int flag1=0;
            int columncheck=0;
            while (line!=null)
            {
                if(line.equalsIgnoreCase(username))  //if same username
                {
                    line=Dictionary_br.readLine();
                    if(line.equalsIgnoreCase(tablename))  //if the same tablename too, the user can make changes
                    {
                        flag=1;
                        line=Dictionary_br.readLine();
                        while(!line.isBlank())  //now loop over the lines of columns
                        {
                            String s[] = line.split(" ");    //check if primary key is the column we're checking
                            if(column.equalsIgnoreCase(s[0]))   //checks if the column is there or not in the table
                            {
                                columncheck=1;   //column exists in table
                                if (!s[3].isBlank())    //if matched, we now look if pk is set
                                {
                                    if (s[3].equalsIgnoreCase("pk"))
                                    {
                                        System.out.println("Key already Set");
                                        return 0;
                                    } else
                                    {
                                        line = Dictionary_br.readLine();  //if no primary key in this set of column move ahead
                                        flag1=1;
                                    }
                                }

                            }
                        }
                    }
                }

                //if username didn't match continue processing loop
                line= Dictionary_br.readLine();
            }//end of while for Data Dictionary

            Dictionary_br.close();
            Dicitonary_fr.close();

            if(flag==0)
            {
                System.out.println("Lack of permission!");
                return 0;

            }

            if(columncheck==0)
            {
                System.out.println("Column not present in the table!");
                return 0;
            }


            if(flag==1 && flag1==1 && columncheck==1) //no primary key set and user can make change and also column exists
            {
                File table = new File(tablename + ".txt");
                FileReader table_fr = new FileReader(table);
                BufferedReader table_br=new BufferedReader(table_fr);
                String input= table_br.readLine();
                List<String> values1=new ArrayList<>();  //stores all values in column to match for duplicate
                List<String> values2=new ArrayList<>();
                while(input!=null)
                {
                    if(!input.isEmpty())
                    {
                        String s[]=input.split(" ");
                        if(column.equalsIgnoreCase(s[0]))  //column matched
                        {
                            values1.add(s[1]);
                            values2.add(s[1]);
                        }
                    }
                    input=table_br.readLine();
                }

                //Now check if any duplicate value in column or not
                for(String temp:values1)
                {
                    if(values2.contains(temp))
                    {
                        System.out.println("Duplicate value exists in column. Cannot be a primary key!");
                        return 0;
                    }
                }

                //No duplicate value exists. Add this column as primary key to Data Dictionary
                FileReader fr = new FileReader(Dictionary);
                BufferedReader br = new BufferedReader(fr);
                File temp=new File("temp.txt");
                if(temp.createNewFile())
                {
                    //creates a new file
                }
                FileWriter fw=new FileWriter(temp,true);
                String tempinput = br.readLine();
                while (tempinput != null)
                {
                    if (tempinput.equalsIgnoreCase(username))
                    {
                        fw.write(tempinput);
                        fw.write("\n");
                        tempinput = br.readLine();
                        if (tempinput.equalsIgnoreCase(tablename))  //user can delete the table now
                        {
                            fw.write(tempinput);
                            fw.write("\n");
                            tempinput=br.readLine();
                            while(!tempinput.isBlank())
                            {
                                String s[]=tempinput.split(" ");
                                if(s[0].equalsIgnoreCase(column))
                                {
                                    fw.write(s[0]+" "+s[1]+" "+"PK");  //add the Primary Key marker
                                    fw.write("\n");
                                }
                                else {
                                    fw.write(tempinput);
                                    fw.write("\n");
                                }
                                tempinput=br.readLine();
                            }
                        } //if tablename didn't match
                        fw.write(tempinput);
                    }
                    else   //if the username didn't match
                    {
                        fw.write(tempinput);   //copy the none matched line
                        fw.write("\n");
                    }
                    tempinput= br.readLine();
                }

                br.close();
                fr.close();
                fw.close();
                Dictionary.delete();  //delete old data dictionary
                temp.renameTo(new File("Data_Dictionary.txt"));   //rename temp file to Data Dictionary

            }

        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }








    //to delete a record in the table
    public static int delete(String username, String tablename, String conditionName, String conditionVal) {
        try {

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
            while (line != null)
            {
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
                String txtline=br_myfile.readLine();
                int count=0;  //count for no of blocks of record
                int block=0;
                //first get the record number block where we have to make the change

                while(txtline!=null)
                {
                    if(!txtline.isBlank())
                    {
                        String s[] = txtline.split(" ");
                        if (s[0].equalsIgnoreCase(conditionName)) {

                            if (s[1].equalsIgnoreCase(conditionVal)) {
                                count++;
                                block = count;
                                break;
                            }
                        }

                    }
                    else {
                        count++;  //line is blank so count one block
                    }
                    txtline = br_myfile.readLine();  //next line
                }//now we know which block number we have to change to


                if(block==0){
                    return 0;    //if no block matched
                }
                br_myfile.close();
                myfilereader.close();


                FileReader fr=new FileReader(myfile);
                BufferedReader br=new BufferedReader(fr);  //the actual data text file
                File temp = new File("temp.txt");  //the file where we will copy
                if (temp.createNewFile()) //if no file exists for data dictionary we create one
                {
                    //new temporary file created
                }
                FileWriter fw_temp = new FileWriter(temp, true);
                String tempinput=br.readLine();  //from the actual data text file
                int checkno=1;//checks the block number
                List<String> order=new ArrayList<>();


                while(tempinput!=null)   //run through entire data text file and copy to new one
                {
                    if(checkno==block)   //if block number is same
                    {

                    }//End of if, if it was the same block
                    else
                    {
                        fw_temp.write(tempinput);
                        fw_temp.write("\n");
                    }

                    if(tempinput.isBlank())   //new block
                    {
                        checkno++;

                    }
                    tempinput=br.readLine();

                }//end of while that runs through the entire datatext file

                fw_temp.close();
                br.close();
                fr.close();

                fw_temp.close();
                myfile.delete();
                temp.renameTo(new File(tablename + ".txt"));
            } //end of if that tells if user is allowed to make changes
            else
            {
                return 0;
            }
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }


        return 1;


    }




    //to drop the table
    public static int drop(String username, String tablename) {
        int flag=0;
        try {

            File file = new File("Data_Dictionary.txt");  //assuming data dictionary is already there
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            File temp=new File("temp.txt");
            if(temp.createNewFile())
            {
                //creates a new file
            }
            FileWriter fw=new FileWriter(temp,true);
            String line = br.readLine();
            while (line != null)
            {
                if (line.equalsIgnoreCase(username))
                {
                    line = br.readLine();
                    if (line.equalsIgnoreCase(tablename))  //user can delete the table now
                    {
                        flag=1;
                        while(line.isBlank())
                        {
                            line=br.readLine();
                        }//end of record
                    }

                    fw.write(username);  //if not the same tablename but same username,first write the username
                    fw.write("\n");
                    fw.write(line);   //pointer at tablename already. A different tablename
                    fw.write("\n");
                }
                else   //if the username didn't match
                {
                    fw.write(line);   //copy the none matched line
                    fw.write("\n");
                }
                line = br.readLine();
            }

            File fin=new File(tablename+".txt");
            fin.delete();   //delete the table text file that contains the data
            file.delete();  //delete old data dictionary
            temp.renameTo(new File("Data_Dictionary.txt"));   //rename temp file to Data Dictionary
            fw.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

}
