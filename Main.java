/*
Graham Shanno
CSE 241
Main Interface
*/
import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class Main{
    public static void main(String [] args) throws SQLException, IOException, java.lang.ClassNotFoundException{
      try{
        Connection connection = null;
        try{
          Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch(Exception e){
          System.out.println("error");
          e.printStackTrace();
        }
        boolean test = false;
        Scanner input = new Scanner(System.in);
        while(!test){
          System.out.println("Enter username: ");
          String username = input.nextLine();
          System.out.println("Enter password: ");
          String password = input.nextLine();
          
          test = true;
          try{
            connection = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", username, password);
          }
          catch(Exception e){
            System.out.println("Incorrect username or password. Check your connection.");
            test = false;
          }
        }
        System.out.println("\nWelcome to Nickel Savings and Loan!");
        System.out.println("___________________________________");

        input = new Scanner(System.in);
        boolean quit = false;
        String task = "";

        do{
          System.out.println("Users: \n1. Customer Card Purchases \n2. Account Deposit/Withdrawal \n3. Taking out a new loan \n4. Make a payment on a loan or creditcard\n5. Quit");
          System.out.println("Enter your user type. Example- Enter '1' to make a purchase with a card. User: ");
          task = input.nextLine();
          if(task.equals("1")){
            Purchases.purchaseInterface(connection);
          }
          else if(task.equals("2")){
            DepositWithdrawal.depositWithdrawalInterface(connection);
          }
          else if(task.equals("3")){
             Loan.loanInterface(connection);
          }
          else if(task.equals("4")){
            LoanCreditPayment.loanPaymentInterface(connection);
          }
          else if(task.equals("5")){
            System.out.println("Thank you for using Nickel Savings and Loan!");
            quit = true;
          }
          else{
            System.out.println("\nThat was not a valid iput. Try again.\n");
          }
        }while(!quit);
        
        //close the connection
        connection.close();
      }
      catch(Throwable throwable){
        System.out.println("\nNot a valid input. Try again. \n");
      }
    }
}