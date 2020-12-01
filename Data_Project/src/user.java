import java.io.IOException;
import java.util.Scanner;

public class user {
    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter username: ");
        String username=sc.next();
        System.out.println("Enter password: ");
        String password=sc.next();
        int n=login.login(username,password);
        if(n==0)
        {
            System.out.println("Do you want to signup?(y/n)");
            String ch=sc.next();
            if(ch.contentEquals("y")||ch.contentEquals("Y"))
            {
                int s=signup.signup(username,password);
                if(s==0)
                {
                    System.out.println("Terminated");    //username exists
                }
                else
                {
                    System.out.println("New User Created");
                    query.parse(username);
                }
            }
            else
            {
                System.out.println("Terminated!");
            }
        }
        else  //user successfully logged in
        {
            query.parse(username);
            //Link user to sql processing in a new class
        }
    }
}
