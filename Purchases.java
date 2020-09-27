/*
Graham Shanno
CSE 241
Purchasing Interface
*/

import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class Purchases{
    public static void purchaseInterface(Connection connection) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean quit = false;
        System.out.println();
        Scanner input = new Scanner(System.in);
    
        //find if the card number exists
        String number = cardNumber(input, connection);
        boolean purchase;
        //if the card number exists, make the purchase
        if(number.length() > 0){
          purchase = purchase(input, connection, number);
        }
    }
  public static String cardNumber(Scanner input, Connection connection) throws SQLException, IOException, java.lang.ClassNotFoundException
  {
    String number = "";
    try
    {
      Statement statement = connection.createStatement();
      String query = "select * from card";
      ResultSet result = null;
      boolean correct = false;
      boolean results = false;
      String card = "";
      boolean match = false;
      result = statement.executeQuery(query);
      while(result.next()){
          System.out.println(result.getString("card_number"));
      }
      System.out.println();

      //user input for card number
      System.out.println("Welcome: Please enter you card number that you plan on making the purchase with: ");
      number = input.nextLine();
      try
      {
        do
        {
          try
          {
            result = statement.executeQuery(query);
            if(!result.next())
            {
              System.out.println("There are no card numbers that we have on record.");
            }
            else
            {
              while(result.next()){
                //try to match the card numbers
                card = result.getString("card_number");
                if(number.equals(card)){
                  match = true;
                  correct = true;
                }
              }
            }
            //card number found, break the loop/method
            if(match == true){
              System.out.println("We found your card on the file system.");
              return number;
            }
            else{
              result = statement.executeQuery(query);
              while(result.next()){
                System.out.println(result.getString("card_number"));
              }
              System.out.println();
              System.out.println("We did not find your card in our system. Displayed above are the records we have on file. Try again, or enter \"quit\" to quit: ");
              number = input.nextLine();
              if(number.equals("quit"))
              {
                System.out.println();
                return "";
              }
            }
          }
        catch(Exception e)
        {
          System.out.println("Unable to print the card_number table");
        } 
      } while(!correct);
      }
      catch(Throwable throwable)
      {
        System.out.println("Incorrect Input");
      } 
    }
    catch(Throwable throwable)
    {
      System.out.println("Incorrect input");
    }
    //return an empty string of length 0 if no number is found
    return "";
  }

  public static boolean purchase(Scanner input, Connection connection, String number) throws SQLException, IOException, java.lang.ClassNotFoundException{
      System.out.println();
      try{
        // System.out.println("Enter the purchase amount: ");
        // double purchase = input.nextDouble();
        Statement statement = connection.createStatement();
        ResultSet result = null;
        boolean debit = false;
        //boolean credit = false;


        //determine if the card is a debit or a credit card
        String query = "select * from card natural join debit";
        String debitQuery = "";
        String creditQuery = "";
        String card = "";

        //for debit cards
        try{
          result = statement.executeQuery(query);
          while(result.next()){
            card = result.getString("card_number");
            if(card.equals(number)){
              debit = true;
              System.out.println("Here are your details for that debit card: ");
              System.out.println("Card number: " + result.getString("card_number"));
              System.out.println("CheckingID: " + result.getString("checking_ID"));

              String checking_ID = result.getString("checking_ID");

              //match the debit card to the corresponding checking account
              System.out.println("Here are the corresponding checking account details:");
              try{
                debitQuery = "select * from checking";
                result = statement.executeQuery(debitQuery);
                String checking = "";
                while(result.next()){
                  checking = result.getString("checking_ID");
                  if(checking.equals(checking_ID)){
                    System.out.println("CheckingID: " + result.getString("checking_ID"));
                    System.out.println("Balance: " + result.getString("balance"));
                    System.out.println("Interest rate: " + result.getString("interest_rate"));
                    System.out.println("AccountID: " + result.getString("account_ID"));
                    System.out.println();

                    //create the purchase
                    boolean next = false;
                    do{
                    try{
                      System.out.println("What is the purchase price?: ");
                      double purchase = input.nextDouble();
                      double data = Double.parseDouble(result.getString("balance"));
                      if(purchase > data){
                        System.out.println("The purchase amount is greater than your balance. Not valid.");
                      }
                      //insert into the purchases database
                      else{
                        //generate the purchases_ID
                        int purchase_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                        String p_ID = Integer.toString(purchase_ID);

                        query = "insert into purchases (purchases_ID, amount, card_number) values (\'" + p_ID + "\', " + purchase + ", \'" + number + "\')";
                        query(connection, query, "Couldn't add to the purchases table");

                        System.out.println();
                        double newBalance = data - purchase;

                        //now modify the balance of the account
                        query = "update checking set balance = " + newBalance + "where checking_ID = \'" + checking + "\'";
                        query(connection, query, "Couldn't update the checking account balance");

                        System.out.println("Purchase has been added to the system and balances have been updated. Thank you.");
                        System.out.println("New account balance = " + newBalance);
                        System.out.println("--------------------------------------------------------------------------------");
                        next = true;
                      }
                    }
                    catch(Exception e){
                      System.out.println("That was not a valid input. ");
                      input.next();
                    }
                    }while(!next);
                  }
                }
              }
              catch(Exception e){
                System.out.println("Error, could not access the checking account");
              }
              break;
            }
          }
        }
        catch(Exception e){
          System.out.println("Incorrect input.");
        }



        //for credit cards
        if(debit == false){
          query = "select * from card natural join credit";
          result = statement.executeQuery(query);
          try{
            String data = "";
            while(result.next()){
              data = result.getString("card_number");
              if(data.equals(number)){
                System.out.println("Here are your details for that credit card: ");
                System.out.println("Card number: " + result.getString("card_number"));
                System.out.println("Running balance: " + result.getString("run_balance"));
                System.out.println("Balance due: " + result.getString("balance_due"));
                System.out.println("Credit limit: " + result.getString("credit_limit"));
                System.out.println("Interest rate: " + result.getString("interest_rate"));
                System.out.println("AccountID: " + result.getString("account_ID"));
                System.out.println();

                //create the purchase
                boolean next = false;
                  do{
                  try{
                    System.out.println("What is the purchase price?: ");
                    double purchase = input.nextDouble();
                    double run_balance = Double.parseDouble(result.getString("run_balance"));
                    double balance_due = Double.parseDouble(result.getString("balance_due"));
                    double creditLimit = Double.parseDouble(result.getString("credit_limit"));
                    double updated = balance_due + purchase;

                    //checks to make sure running balance won't be below the credit limit
                    if(purchase > run_balance || creditLimit < updated){
                      System.out.println("The purchase amount is greater than your running balance, \nor the balance_due plus the purchase is greater than the credit limit. Not valid.");
                    }
                    //insert into the purchases database
                    else{
                      //generate the purchases_ID
                      int purchase_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                      String p_ID = Integer.toString(purchase_ID);
                      //System.out.println(p_ID);
                      query = "insert into purchases (purchases_ID, amount, card_number) values (\'" + p_ID + "\', " + purchase + ", \'" + number + "\')";
                      query(connection, query, "Couldn't add to the purchases table");
                      System.out.println();
                      double newBalance = run_balance - purchase;
                      balance_due = balance_due + purchase;
                      
                      //now modify the balance of the account
                      query = "update credit set run_balance = " + newBalance + "where card_number = \'" + number + "\'";
                      query(connection, query, "Couldn't update the running balance");
                      query = "update credit set balance_due = " + balance_due + "where card_number = \'" + number + "\'";
                      query(connection, query, "Couldn't update the balance due");

                      System.out.println("Purchase has been added to the system and balances have been updated. Thank you.");
                      System.out.println("New account balance = " + newBalance);
                      System.out.println("--------------------------------------------------------------------------------");
                      next = true;
                    }
                  }
                  catch(Exception e){
                    System.out.println("That was not a valid input. ");
                    input.next();
                  }
                  }while(!next);
              }
            }
          }
          catch(Exception e){
            System.out.println("Error accessing the credit card database.");
          }
        }
      }
      catch(Exception e){
        System.out.println("That was not a valid input.");
      }
      return true;
  }

  public static ResultSet query(Connection connection, String query, String error){
      try{
          Statement statement = connection.createStatement();
          return statement.executeQuery(query);
      }
      catch(Exception e){
          System.out.println(error);
      }
      return null;
  }
}