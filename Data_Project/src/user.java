import java.io.IOException;
import java.util.Scanner;

public class user {
    public static void main(String args[]) throws IOException {
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
                signup.signup(username,password);
            }
            else
            {
                System.out.println("Terminated!");
            }
        }
        else  //user successfully logged in
        {
            //Link user to sql processing in a new class
        }
    }
}
