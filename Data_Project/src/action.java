import java.io.*;
import java.util.ArrayList;

public class action {


    //Create Table
    public static int create(String username, String tablename, ArrayList<String> columns, ArrayList<String> values)
    {
        try{
            File ddc=new File("Data_Dictionary.txt");
            FileWriter fw_ddc=new FileWriter(ddc,true);    //appending data dictionary allowed
            if(ddc.createNewFile()) //if no file exists for data dictionary we create one
            {
                System.out.println("Data Dictionary Created");
            }

            FileReader fr_ddc=new FileReader(ddc);   //Now that we have a dictionary,  we traverse it to check if no same user has the same table
            BufferedReader br_ddc=new BufferedReader(fr_ddc);
            String ddc_line=br_ddc.readLine();
            int flag=1;  //checks if username record should be added to ddc(1) or not(0)
            while (ddc_line!=null)
            {
                if(ddc_line.equalsIgnoreCase(username))
                {
                    ddc_line=br_ddc.readLine();  //checking the table name associated with that user

                    if(ddc_line.equalsIgnoreCase(tablename))
                    {
                        System.out.println("User already associated with table!");
                        return 0;
                    }
                   continue;     //no need to change line again
                } //end of if checking that whether the username already exists
                //user record should be added if we're here



               ddc_line=br_ddc.readLine();  //change line
            }

            //user record passes Data Dictionary check and should be added with new table
            fw_ddc.append(username);    //adding the username first
            fw_ddc.append("\n");
            fw_ddc.append(tablename);   //then the table name
            fw_ddc.append("\n");
            for (int i = 0; i < columns.size(); i++)
            {
                fw_ddc.append(columns.get(i));   //first the column name
                fw_ddc.append("\n");
                fw_ddc.append(values.get(i));    //then the values
                fw_ddc.append("\n");
            }
            fw_ddc.append("\n");    //empty line to denote end of record
            fw_ddc.close();
            br_ddc.close();

            File file=new File(tablename+".txt");   //a seperate file to store the data of the table
            file.createNewFile();   //creates that file

        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return 1;
    }









    //to drop the table
    public static int drop(String username,String tablename)
    {
        int flag=0;   //check if user can delete or not
        try {

            File file = new File("Data_Dictionary.txt");  //assuming data dictionary is already there
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            PrintWriter pw=new PrintWriter(file);   //it will create a new file/overwrite file of the same name

            String line= br.readLine();
            while(line!=null)
            {
                if(line.equalsIgnoreCase(username))
                {
                    line=br.readLine();
                    if(line.equalsIgnoreCase(tablename))  //user can delete the table now
                    {
                        while (line.isBlank())    //blank line means end of one record
                        {
                            line= br.readLine();   //process till we get to new record
                        }
                        flag=1;  //confirms table was deleted
                    }

                    pw.write(username);  //if not the same tablename but same username,first write the username
                    pw.write("\n");
                    pw.write(line);   //pointer at tablename already
                    pw.write("\n");
                    continue;    //no need to process next one
                }

                pw.write(line);   //copy the none matched line
                pw.write("\n");
                line=br.readLine();
            }

            pw.close();
            br.close();
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return flag;
    }





}
