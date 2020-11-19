import java.io.*;
import java.util.Scanner;

public class signup {

    public static int signup(String username, String password) throws IOException {

        try {
            File file = new File("login.txt");
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            FileWriter fw = new FileWriter(file, true);   //true means while is appended
            Scanner sc=new Scanner(System.in);
            String line=br.readLine();
            while(line!=null)
            {
                if(username.equalsIgnoreCase(line))
                {
                    System.out.println("Username exists!");
                    return 0;
                }
                line= br.readLine();
            }

            fw.append(username);
            fw.append("\n");
            fw.append(password);
            fw.append("\n");
            fw.append("\n");
            fw.close();
            br.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }


        return 1;
    }
}
