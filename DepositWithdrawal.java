/*
Graham Shanno
CSE 241
Deposit/Withdrawal Interface
*/

import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class DepositWithdrawal{
    public static void depositWithdrawalInterface(Connection connection) throws SQLException, IOException, java.lang.ClassNotFoundException{
        System.out.println();
        Scanner input = new Scanner(System.in);

        //first make sure the branch that the customer is at has a bank teller to make the transaction
        //if branch has a teller, can do withdrawal and deposit
        //if branch just has an atm, can only do withdrawal
        Statement statement = connection.createStatement();
        String query = "select * from branch";
        ResultSet result = null;
        result = statement.executeQuery(query);
        //print the available branches and locations
        try{
            System.out.println("Welcome. Here are a list of active branches we have, and their respective ID's: ");
            System.out.println("Location \t\t    Branch ID");
            System.out.println("-------- \t\t    ---------");
            while(result.next()){
                System.out.println(String.format("%-30s %-10s", result.getString("location"), result.getString("branch_ID")));
            }
        }
        catch(Exception e){
            System.out.println("Error. There is no data in the branch database.");
        }

        String branchID = "";
        String branch = "";
        boolean match = false;
        do{
            try{
                //enter branch ID
                System.out.println("Please enter the branch's ID that you are performing this transaction at: ");
                branchID = input.nextLine();
                if(branchID.equals("quit")){
                    match = true;
                    break;
                }
                result = statement.executeQuery(query);
                while(result.next()){
                    branch = result.getString("branch_ID");
                    if(branchID.equals(branch)){
                        System.out.println("\nWelcome to the " + result.getString("location") + " branch!");
                        String type = result.getString("branch_type");
                        if(type.equals("atm")){
                            boolean a = false;
                            do{
                                System.out.println("This branch only has an atm, and cannot perform a deposit transaction.\nWould you like to make a debit withdrawal? Type \'yes\' or \'no\': ");
                                String response = input.nextLine();
                                if(response.equals("yes")){
                                    a = true;
                                    withdrawal(input, connection, branch);
                                    break;
                                }
                                else if(response.equals("no")){
                                    break;
                                }
                                else{
                                    System.out.println("That was not a valid response. Try again. ");
                                }
                            }while(!a);
                            //System.out.println("atm");
                        }
                        else{
                            boolean a = false;
                            do{
                            System.out.println("This branch has both an atm and a teller. Would you like to perform a withdrawal or deposit? Enter \'withdrawal\' for withdrawal and \'deposit\' for deposit, or \'quit\' to quit: ");
                            String t = input.nextLine();

                            //because the bank has a teller, you can withdraw from the savings account now
                            if(t.equals("withdrawal")){
                                a = true;
                                boolean question = false;
                                do{
                                System.out.println("Would you like to withdraw from your savings account or checking? Type \'savings\' for savings and \'checking\' for checking: ");
                                String accountType = input.nextLine();
                                if(accountType.equals("savings")){
                                    question = true;
                                    withdrawalSavings(input, connection, branch);
                                }
                                else if(accountType.equals("checking")){
                                    question = true;
                                    withdrawal(input, connection, branch);
                                }
                                else{
                                    System.out.println("Not a valid response.");
                                }
                                }while(!question);
                            }
                            else if(t.equals("deposit")){
                                a = true;
                                deposit(input, connection, branch);
                            }
                            else if(t.equals("quit")){
                                a = true;
                            }
                            else{
                                System.out.println("That was not a valid response. Try again.");
                            }
                            //System.out.println("teller");
                            }while(!a);
                        }
                        match = true;
                        break;
                    }
                }
                if(match == false){
                    System.out.println("That is not a valid branch ID. Try again, or enter \'quit\' to quit. ");
                }
            }
            catch(Exception e){
                System.out.println("Not a valid input.");
            }
        }while(!match);
    }

    public static void withdrawal(Scanner input, Connection connection, String branchID) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean match = false;
        Statement statement = connection.createStatement();
        ResultSet result = null;
        String query = "select * from debit"; 
        result = statement.executeQuery(query);
        do{
        //print out the available card numbers
        while(result.next()){
            System.out.println(result.getString("card_number"));
        }
        System.out.println();
        System.out.println("What is your card number?: ");
        String number = input.nextLine();
        String card = "";
        try{
          result = statement.executeQuery(query);
          while(result.next()){
            card = result.getString("card_number");
            if(card.equals(number)){
              match = true;
              System.out.println("Here are your details for that debit card: ");
              System.out.println("Card number: " + result.getString("card_number"));
              System.out.println("CheckingID: " + result.getString("checking_ID"));

              String checking_ID = result.getString("checking_ID");

              //match the debit card to the corresponding checking account
              System.out.println("Here are the corresponding checking account details:");
              try{
                query = "select * from checking";
                result = statement.executeQuery(query);
                String checking = "";
                while(result.next()){
                  checking = result.getString("checking_ID");
                  if(checking.equals(checking_ID)){
                    System.out.println("CheckingID: " + result.getString("checking_ID"));
                    System.out.println("Balance: " + result.getString("balance"));
                    System.out.println("Interest rate: " + result.getString("interest_rate"));
                    System.out.println("AccountID: " + result.getString("account_ID"));
                    System.out.println();
                    String accountID = result.getString("account_ID");
                    //create the transaction
                    boolean next = false;
                    do{
                    try{
                      System.out.println("How much would you like to withdraw?: ");
                      double withdraw = input.nextDouble();
                      double data = Double.parseDouble(result.getString("balance"));
                      if(withdraw > data){
                        System.out.println("The withdraw amount is greater than your balance. Not valid.");
                      }
                      //insert into the transactions database
                      else{
                        //generate the trans_ID
                        int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                        String t_ID = Integer.toString(trans_ID);
                        //System.out.println(p_ID);
                        query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + withdraw + ", \'withdrawal\', \'" + accountID + "\', \'" + branchID + "\')";
                        query(connection, query, "Couldn't add to the transactions database");
                        System.out.println();

                        double newBalance = data - withdraw;
                        //now modify the balance of the account
                        query = "update checking set balance = " + newBalance + "where checking_ID = \'" + checking + "\'";
                        query(connection, query, "Couldn't update the checking account balance");
                        System.out.println("Withdrawal has been added to the transactions system and balances have been updated. Thank you.");
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
            }
          }
          if(match == false){
            System.out.println("We do not have that debit card on record. Try again, here are the available debit cards we have in our system. ");
            query = "select * from debit";
            result = statement.executeQuery(query);
            while(result.next()){
                System.out.println(result.getString("card_number"));
            }
          }
        }
        catch(Exception e){
            System.out.println("Invalid option.");
        }
        }while(!match);
  }

  public static void deposit(Scanner input, Connection connection, String branchID) throws SQLException, IOException, java.lang.ClassNotFoundException{
      String query = "";
      ResultSet result = null;
      Statement statement = connection.createStatement();
      boolean in = false;
      do{
          //check to see if they want to deposit into savings or checking account
          System.out.println("Do you want to deposit into your checkings account or savings account? Type \'checking\' for checking, \'savings\' for savings, or \'quit\' to quit: ");
          String response = input.nextLine();

          //checking account
          if(response.equals("checking")){
              boolean a = false;
              do{
                  try{
                    query = "select * from checking";
                    result = statement.executeQuery(query);
                    System.out.println("Here are the available checking IDs we have on record.");
                    while(result.next()){
                        System.out.println(result.getString("checking_ID"));
                    } 
                    //reset
                    result = statement.executeQuery(query);
                    System.out.println("What is your checking ID?: ");
                    String checking = input.nextLine();
                    System.out.println();
                    while(result.next()){
                        if(checking.equals(result.getString("checking_ID"))){
                            a = true;
                            System.out.println("Here is your checking account info: \n");
                            System.out.println("Checking ID: " + result.getString("checking_ID"));
                            System.out.println("Balance: "+ result.getString("balance"));
                            System.out.println("Account ID: " + result.getString("account_ID"));
                            System.out.println();
                            String accountID = result.getString("account_ID");
                            boolean b = false;
                            do{
                                try{
                                    System.out.println("How much would you like to deposit?: ");
                                    double deposit = input.nextDouble();
                                    double data = Double.parseDouble(result.getString("balance"));

                                    //generate a transactions ID
                                    int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                                    String t_ID = Integer.toString(trans_ID);

                                    //add into the database
                                    query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + deposit + ", \'deposit\', \'" + accountID + "\', \'" + branchID + "\')";
                                    query(connection, query, "Couldn't add to the transactions database");
                                    System.out.println();

                                    double newBalance = data + deposit;

                                    //update the checking account balance
                                    query = "update checking set balance = " + newBalance + "where checking_ID = \'" + checking + "\'";
                                    query(connection, query, "Couldn't update the checking account balance");

                                    System.out.println("Deposit has been added to the transactions system and balances have been updated. Thank you.");
                                    System.out.println("New account balance = " + newBalance);
                                    System.out.println("--------------------------------------------------------------------------------");
                                    b = true;
                                }
                                catch(Exception e){
                                    System.out.println("That was not a valid input.");
                                    input.next();
                                }
                            }while(!b);
                        }
                    }
                  }
                  catch(Exception e){
                      System.out.println("Error, could not connect to the savings database");
                  }
              }while(!a);
              in = true;
          }

          //savings account
          else if(response.equals("savings")){
              boolean a = false;
              do{
                  try{
                    query = "select * from savings";
                    result = statement.executeQuery(query);
                    System.out.println("Here are the available savings account IDs we have on record.");
                    while(result.next()){
                        System.out.println(result.getString("savings_ID"));
                    } 
                    //reset
                    result = statement.executeQuery(query);
                    System.out.println("What is your savings ID?: ");
                    String savings = input.nextLine();
                    System.out.println();
                    while(result.next()){
                        if(savings.equals(result.getString("savings_ID"))){
                            a = true;
                            //print out the savings account info
                            System.out.println("Here is your savings account info: \n");
                            System.out.println("SavingsID: " + result.getString("savings_ID"));
                            System.out.println("Balance: "+ result.getString("balance"));
                            System.out.println("Penalty: " + result.getString("penalty"));
                            System.out.println("Min Balance: " + result.getString("min_balance"));
                            System.out.println("Account ID: " + result.getString("account_ID"));
                            System.out.println();
                            String accountID = result.getString("account_ID");
                            boolean b = false;
                            do{
                                try{
                                    System.out.println("How much would you like to deposit?: ");
                                    double deposit = input.nextDouble();
                                    double data = Double.parseDouble(result.getString("balance"));
                                    //generate a transactions ID
                                    int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                                    String t_ID = Integer.toString(trans_ID);

                                    //add into the database
                                    query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + deposit + ", \'deposit\', \'" + accountID + "\', \'" + branchID + "\')";
                                    query(connection, query, "Couldn't add to the transactions database");
                                    System.out.println();

                                    double newBalance = data + deposit;

                                    //update the savings account balance
                                    query = "update savings set balance = " + newBalance + "where savings_ID = \'" + savings + "\'";
                                    query(connection, query, "Couldn't update the savings account balance");
                                    System.out.println("Deposit has been added to the transactions system and balances have been updated. Thank you.");
                                    System.out.println("New account balance = " + newBalance);
                                    System.out.println("--------------------------------------------------------------------------------");
                                    b = true;
                                }
                                catch(Exception e){
                                    System.out.println("That was not a valid input.");
                                    input.next();
                                }
                            }while(!b);
                        }
                    }
                  }
                  catch(Exception e){
                      System.out.println("Error, could not connect to the savings database");
                  }
              }while(!a);
              in = true;
          }

          //quit
          else if(response.equals("quit")){
              in = true;
              break;
          }
          else{
              System.out.println("That was not a valid answer.");
          }
      }while(!in);
  }

    //when branch has a teller, customer can perform a withdrawal from savings
  public static void withdrawalSavings(Scanner input, Connection connection, String branchID) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean match = false;
        Statement statement = connection.createStatement();
        ResultSet result = null;
        String query = "select * from savings";
        result = statement.executeQuery(query);
        do{
        //print out the available savingsID
        while(result.next()){
             System.out.println(result.getString("savings_ID"));
        }
        System.out.println();
        System.out.println("What is your savings ID number?: ");
        String savingsID = input.nextLine();
        String savings = "";
        try{
          result = statement.executeQuery(query);
          while(result.next()){
            savings = result.getString("savings_ID");
            if(savings.equals(savingsID)){
              match = true;
              System.out.println();
              System.out.println("Here are your details for that savings account: ");
              System.out.println("Savings ID: " + result.getString("savings_ID"));
              System.out.println("Balance: " + result.getString("balance"));
              System.out.println("Penalty: " + result.getString("penalty"));
              System.out.println("Minimum Balance: " + result.getString("min_balance"));
              System.out.println("Account ID: " + result.getString("account_ID"));
              try{
                String accountID = result.getString("account_ID");
                //create the withdrawal
                boolean next = false;
                do{
                try{
                  System.out.println("How much would you like to withdraw?: ");
                  double withdraw = input.nextDouble();
                  double data = Double.parseDouble(result.getString("balance"));
                  double min = Double.parseDouble(result.getString("min_balance"));
                  if((data - withdraw) < min){
                    System.out.println("The withdraw amount is greater than your minimum balance. You are incurring a penalty of: " + result.getString("penalty"));
                    next = true;
                  }
                //insert into the purchases database
                  else{
                //generate the purchases_ID
                    int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                    String t_ID = Integer.toString(trans_ID);
                    //System.out.println(p_ID);
                    query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + withdraw + ", \'withdrawal\', \'" + accountID + "\', \'" + branchID + "\')";
                    query(connection, query, "Couldn't add to the transactions database");
                    System.out.println();

                    double newBalance = data - withdraw;
                    //now modify the balance of the savings account
                    query = "update savings set balance = " + newBalance + "where savings_ID = \'" + savings + "\'";
                    query(connection, query, "Couldn't update the savings account balance");
                    System.out.println("Withdrawal has been added to the transactions system and balances have been updated. Thank you.");
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
            catch(Exception e){
                System.out.println("Error, could not access the savings account");
            }
            }
          }

            if(match == false){
                System.out.println("We do not have that savings ID on record. Try again, here are the available IDs we have in our system. \n");
                query = "select * from savings";
                result = statement.executeQuery(query);
                while(result.next()){
                    System.out.println(result.getString("savings_ID"));
                }
                System.out.println();
            }
        }
        catch(Exception e){
            System.out.println("Invalid option.");
        }
        }while(!match);
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