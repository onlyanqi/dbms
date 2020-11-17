import java.io.*;

public class signup {

    public static int signup(String username, String password) throws IOException {

        try {
            File file = new File("login.txt");
            FileWriter fw = new FileWriter(file, true);   //true means while is appended
            fw.append(username);
            fw.append("\n");
            fw.append(password);
            fw.append("\n");
            fw.close();

        }catch (IOException e)
        {
            e.printStackTrace();
        }


        return 1;
    }
}
