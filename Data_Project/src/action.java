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
            int flag = 1;  //checks if username record should be added to ddc(1) or not(0)
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

                                fw.write(temp.get(i));   //store the column name
                                fw.write(" ");       //a space
                                if(check==1)
                                {
                                    fw.write(values.get(count));    //the value of the column
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
                int check=-1;

                while(tempinput!=null)   //run through entire data text file and copy to new one
                {
                    if(checkno==block)   //if block number is same
                    {
                        check=-1;
                        if(!tempinput.isBlank())
                        {
                            String s[] = tempinput.split(" ");  //get a column name i.e column then value, so s[0]=column

                            for (int i = 0; i < column.size(); i++)  //match current column to see if it needs an updated value
                            {
                                if (s[0].equalsIgnoreCase(column.get(i)))   //see if that column is updated
                                {
                                    check = i;
                                    break;
                                }
                            }
                            if (check == -1)  //no column found in the block that is to be updated compared to current line
                            {
                                fw_temp.write(tempinput);   //no update so write line as it was
                                fw_temp.write("\n");
                            }
                            else //if update found
                            {
                                fw_temp.write(column.get(check));   //column name
                                fw_temp.write(" ");
                                fw_temp.write(values.get(check));  //enter the new value
                                fw_temp.write("\n");
                            }
                        }             //if line was blank

                        fw_temp.write(tempinput);  //copy the empty line

                    } //if a different block then just copy as it is

                    fw_temp.write(tempinput);
                    tempinput = br.readLine();
                    if (tempinput.isBlank())
                    {
                        checkno++;    //change the block number when new line encountered
                    }

                }//end of while that runs through the entire datatext file

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
    public static int select(String username,String tablename,List<String> column, List<String> values, String conditionName, String conditionVal)
    {

        try {

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
                }//now we know which block number we have to read
                br_myfile.close();
                myfilereader.close();


                FileReader fr=new FileReader(myfile);
                BufferedReader br=new BufferedReader(fr);  //the actual data text file
                String tempinput=br.readLine();  //from the actual data text file
                int checkno=1;//checks the block number
                List<String> order=new ArrayList<>();

                while(tempinput!=null)   //run through entire text file
                {
                    if(checkno==block)   //if block number is same
                    {
                        if(!tempinput.isBlank())
                        {
                            String s[] = tempinput.split(" ");  //get a column name i.e column then value, so s[0]=column

                            for (int i = 0; i < column.size(); i++)  //match current column to see if it needs an updated value
                            {
                                if (s[0].equalsIgnoreCase(column.get(i)))   //see if that column is updated
                                {
                                    System.out.print(tempinput);    //print the matched line
                                }
                            }
                        }  //if line was blank

                    } //if a different block then just copy as it is

                    tempinput = br.readLine();
                    if (tempinput.isBlank())
                    {
                        checkno++;    //change of block if new line encountered
                        tempinput=br.readLine();
                    }
                }//end of while that runs through the entire datatext file


        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        return 1;
    }








    //to delete a record in the table
    public static  int delete(String username, String tablename,List<String> column, List<String> values, String conditionName, String conditionVal) {

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
                int check=-1;

                while(tempinput!=null)   //run through entire data text file and copy to new one
                {
                    if(checkno==block)   //if block number is same
                    {

                        tempinput=br.readLine();
                        if(tempinput.isBlank())
                        {
                            checkno++;   //change of block number
                        }
                    } //if a different block then just copy as it is
                    else {
                        fw_temp.write(tempinput);
                        tempinput = br.readLine();
                        if (tempinput.isBlank()) {
                            checkno++;    //change the block number when new line encountered
                        }
                    }
                }//end of while that runs through the entire datatext file

                myfile.delete();   //delete the original text file
                temp.renameTo(new File(tablename + ".txt"));    //rename the temp file to the tablename
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
        int flag = 0;   //check if user can delete or not
        try {

            File file = new File("Data_Dictionary.txt");  //assuming data dictionary is already there
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            PrintWriter pw = new PrintWriter(file);   //it will create a new file/overwrite file of the same name

            String line = br.readLine();
            while (line != null) {
                if (line.equalsIgnoreCase(username)) {
                    line = br.readLine();
                    if (line.equalsIgnoreCase(tablename))  //user can delete the table now
                    {
                        while (line.isBlank())    //blank line means end of one record
                        {
                            line = br.readLine();   //process till we get to new record
                        }
                        flag = 1;  //confirms table was deleted
                        File table = new File(tablename + ".txt");
                        table.delete();    //deletes the file associated with table data

                    }

                    pw.write(username);  //if not the same tablename but same username,first write the username
                    pw.write("\n");
                    pw.write(line);   //pointer at tablename already
                    pw.write("\n");
                    continue;    //no need to process next one
                }

                pw.write(line);   //copy the none matched line
                pw.write("\n");
                line = br.readLine();
            }

            File fin=new File(tablename+".txt");
            fin.delete();   //delete the table text file too

            pw.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }
}
