import java.io.*;

public class login {

    public static int login(String username, String password) {
        try {

            File file = new File("login.txt");  //default login text file
            if (file.createNewFile())   //if no file exists, we create one
            {
                System.out.println("New login file created!");
            }

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();   //read the text file line by line
            int flag = 0;  //if 0 after the loop, no user found
            while (line != null) {
                if (username.contentEquals(line))
                {
                    line = br.readLine();
                    if (password.contentEquals(line))
                    {
                        System.out.println("Successful login");
                        flag = 1;
                    }
                    else
                        {
                        System.out.println("Wrong Password!Try again!");
                        }
                }

                line = br.readLine();
            }


            if (flag == 0)
            {
                System.out.println("No user exists!");
                return 0;
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }


}



