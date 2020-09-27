/*
Graham Shanno
CSE 241
LoanCreditPayment Interface
*/

import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class LoanCreditPayment{
    public static void loanPaymentInterface(Connection connection) throws SQLException, IOException, java.lang.ClassNotFoundException{
        System.out.println();
        Scanner input = new Scanner(System.in);

        //first make sure the branch that the customer is at has a bank teller to make the loan/credit payment
        //if branch does not have a teller, cannot make the payment
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
                System.out.println("Please enter the branch's ID that you are performing this credit/loan payment at: ");
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
                        //does the branch have an atm?
                        String type = result.getString("branch_type");
                        if(type.equals("atm")){
                            System.out.println("This branch only has an atm, and cannot perform a loan or credit transaction. Sorry.");
                            match = true;
                            break;
                        }
                        else{
                            boolean a = false;
                            do{
                                //determine credit or loan payment
                                System.out.println("This branch has both an atm and a teller. Would you like to make a loan or credit payment? Enter \'loan\' for loan, \'credit\' for credit, or \'quit\' to quit: ");
                                String t = input.nextLine();
                                if(t.equals("loan")){
                                    a = true;
                                    //invoke loan payment method
                                    loan(input, connection, branch);
                                }
                                else if(t.equals("credit")){
                                    a = true;
                                    //invoke credit payment method
                                    credit(input, connection, branch);
                                }
                                else if(t.equals("quit")){
                                    a = true;
                                }
                                else{
                                    System.out.println("That was not a valid response. Try again.");
                                }
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

    //loan payment
    public static void loan(Scanner input, Connection connection, String branch) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean match = false;
        ResultSet result = null;
        Statement statement = connection.createStatement();
        String query = "select * from loan";
        System.out.println();
        try{
            //print out the available social security numbers
            result = statement.executeQuery(query);
            while(result.next()){
                System.out.println(result.getString("social_security"));
            }
            System.out.println("\nHere are the available social security numbers we have in our database. ");
        }
        catch(Exception e){
            System.out.println("Could not access the loan database.");
        }

        do{
            System.out.println("What is your social security number?: ");
            String ssn = input.nextLine();
            String social = "";
            try{
                //match the social security numbers and print out the loan information
                result = statement.executeQuery(query);
                while(result.next()){
                    social = result.getString("social_security");
                    if(social.equals(ssn)){
                        match = true;
                        System.out.println();
                        System.out.println("Here is the loan information: ");
                        System.out.println("Loan ID: " + result.getString("loan_ID"));
                        System.out.println("Loan type: " + result.getString("loan_type"));
                        System.out.println("Balance: " + result.getString("loan_balance"));
                        System.out.println("Interest rate: " + result.getString("interest_rate"));
                        System.out.println();
                        boolean a = false;
                        do{
                            try{
                                System.out.println("What is the payment that you are making?: ");
                                double payment = input.nextDouble();
                                double balance = Double.parseDouble(result.getString("loan_balance"));
                                double newBalance = balance - payment;
                                //test if new balance is equal to or less than 0
                                if(balance == 0){
                                    System.out.println("Your loan has already been paid off in full!\n");
                                    break;
                                }
                                if(newBalance < 0){
                                    System.out.println("That was not a valid payment. Payment was larger than the balance.");
                                }
                                else{
                                    //update the loan balance and add the transaction
                                    query = "update loan set loan_balance = " + newBalance + " where social_security = \'" + ssn + "\'";
                                    query(connection, query, "Could not update the loan balance");

                                    //generate the trans_ID
                                    int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                                    String t_ID = Integer.toString(trans_ID);

                                    //add a new transaction
                                    query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + payment + ", \'loan payment\', \'null\', \'" + branch + "\')";
                                    query(connection, query, "Couldn't add to the transactions database");

                                    a = true;
                                    System.out.println("\nLoan balance has been updated and transaction has been accounted for. Thank you.");
                                    System.out.println("New loan balance due: " + newBalance);
                                    System.out.println("-----------------------------------------------------------------------------------\n");
                                }
                            }
                            catch(Exception e){
                                System.out.println("That was not a valid payment. Try again.");
                                input.next();
                            }
                        }while(!a);
                        break;
                    }
                }
            }
            catch(Exception e){
                System.out.println("Could not access the loan database.");
            }
            if(match == false){
                System.out.println("We did not find that social security number in our database. Try again. ");
            }
        }while(!match);
    }

    //credit payment
    public static void credit(Scanner input, Connection connection, String branch) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean match = false;
        ResultSet result = null;
        Statement statement = connection.createStatement();
        String query = "select * from credit";
        System.out.println();
        try{
            result = statement.executeQuery(query);

            //print out the available credit card nunmbers
            while(result.next()){
                System.out.println(result.getString("card_number"));
            }
            System.out.println("\nHere are the available card numbers we have in our database. ");
        }
        catch(Exception e){
            System.out.println("Could not access the credit database.");
        }

        do{
            System.out.println("What is your card number?: ");
            String card = input.nextLine();
            String number = "";
            try{
                //reset the resultset
                result = statement.executeQuery(query);
                while(result.next()){
                    //print out the card information
                    number = result.getString("card_number");
                    if(number.equals(card)){
                        match = true;
                        System.out.println();
                        System.out.println("Here is your credit information: ");
                        System.out.println("Card number: " + result.getString("card_number"));
                        System.out.println("Interest rate: " + result.getString("interest_rate"));
                        System.out.println("Credit Limit: " + result.getString("credit_limit"));
                        System.out.println("Running balance: " + result.getString("run_balance"));
                        System.out.println("Balance due: " + result.getString("balance_due"));
                        System.out.println("Account_ID: " + result.getString("account_ID")); 
                        System.out.println();
                        boolean a = false;
                        do{
                            try{
                                System.out.println("What is the credit payment that you are making?: ");
                                double payment = input.nextDouble();
                                double balance = Double.parseDouble(result.getString("balance_due"));
                                double newBalance = balance - payment;
                                if(newBalance < 0){
                                    System.out.println("That was not a valid payment. Payment was larger than the balance_due.");
                                }
                                else{
                                    //update the credit balance and add the transaction
                                    query = "update credit set balance_due = " + newBalance + " where card_number = \'" + number + "\'";
                                    query(connection, query, "Could not update the credit balance due");

                                    //generate the trans_ID
                                    int trans_ID = (int)(Math.random() * (10000 - 1000) + 1000);
                                    String t_ID = Integer.toString(trans_ID);

                                    //add a new credit payment transaction
                                    query = "insert into transactions (transactions_ID, amount, transaction_type, account_ID, branch_ID) values (\'" + t_ID + "\', " + payment + ", \'credit payment\', \'null\', \'" + branch + "\')";
                                    query(connection, query, "Couldn't add to the transactions database");
                                    a = true;

                                    System.out.println("\nCredit balance due has been updated and transaction has been accounted for. Thank you.");
                                    //System.out.println("New credit balance due: " + result.getString("balance_due") + "\n");
                                    System.out.println("New credit balance due: " + newBalance);
                                    System.out.println("-----------------------------------------------------------------------------------------\n");
                                }
                            }
                            catch(Exception e){
                                System.out.println("That was not a valid payment. Try again.");
                                input.next();
                            }
                        }while(!a);
                        break;
                    }
                }
            }
            catch(Exception e){
                System.out.println("Could not access the credit database.");
            }
            if(match == false){
                System.out.println("We did not find that card number in our database. Try again. ");
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