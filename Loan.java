/*
Graham Shanno
CSE 241
Loan Interface
*/

import java.util.Scanner;
import java.sql.*;
import java.io.*;

public class Loan{
    public static void loanInterface(Connection connection) throws SQLException, IOException, java.lang.ClassNotFoundException{
        System.out.println();
        Scanner input = new Scanner(System.in);

        //first make sure the branch that the customer is at has a bank teller to make the loan
        //if branch does not have a teller, cannot make the loan
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
                            System.out.println("This branch only has an atm, and cannot perform a loan transaction. Sorry.\n");
                            match = true;
                            break;
                        }
                        else{
                            boolean a = false;
                            do{
                                System.out.println("This branch has both an atm and a teller. Would you like to take out a loan? Enter \'yes\' for yes, or \'quit\' to quit: ");
                                String t = input.nextLine();
                                if(t.equals("yes")){
                                    a = true;
                                    makeLoan(input, connection, branch);
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

    //create the loan
    public static void makeLoan(Scanner input, Connection connection, String branch) throws SQLException, IOException, java.lang.ClassNotFoundException{
        boolean match = false;
        ResultSet result = null;
        Statement statement = connection.createStatement();
        String query = "";
        do{
            System.out.println();
            //print out the available SSN
            query = "select * from customer";
            result = statement.executeQuery(query);
            while(result.next()){
                System.out.println(result.getString("social_security"));
            }
            System.out.println("\nWhat is your social security number?: ");
            String social = input.nextLine();
            try{
                result = statement.executeQuery(query);
                String ssn = "";

                while(result.next()){
                    ssn = result.getString("social_security");
                    if(ssn.equals(social)){
                        match = true;
                        boolean a = false;
                        do{
                            System.out.println("\nWe found your social security number on file. \nWhat kind of loan would you like to take out? Enter \'mortgage\' for mortgage and \'unsecured\' for unsecured: ");
                            String loanType = input.nextLine();
                            //create a new loan
                            if(loanType.equals("mortgage") || loanType.equals("unsecured")){
                                a = true;
                                boolean b = false;
                                do{
                                    try{
                                        //get the loan statistics
                                        System.out.println("How much would you like to borrow?: ");
                                        double loanBalance = input.nextDouble();
                                        System.out.println("How many months will you be paying off this loan?: ");
                                        double months = input.nextDouble();
                                        //set the monthly payment
                                        double monthlyPayment = loanBalance / months;
                                        //generate a loan ID
                                        //generate an interest rate
                                        int interest = (int)(Math.random() * (5 - 0) + 0);
                                        int loanID = (int)(Math.random() * (10000 - 1000) + 1000);

                                        query = "insert into loan (loan_ID, interest_rate, loan_type, monthly_payment, loan_balance, branch_ID, social_security) values (" + loanID + ", " + interest + ", \'" + loanType + "\', " + monthlyPayment + ", " + loanBalance + ", \'" + branch + "\', \'" + ssn + "\')";
                                        query(connection, query, "Could not insert into the loan database");
                                        b = true;

                                        //output the values of the new loan
                                        System.out.println("\nNew loan has been entered.");
                                        System.out.println("Interest rate: " + interest);
                                        System.out.println("Monthly payment: " + monthlyPayment);
                                        System.out.println("Balance: " + loanBalance);
                                        System.out.println("\nThank you.\n");
                                    }
                                    catch(Exception e){
                                        System.out.println("That was not a valid input.");
                                        input.next();
                                    }
                                }while(!b);
                            }
                            // else if(loanType.equals("unsecured")){
                            //     a = true;
                            // }
                            else{
                                System.out.println("That was not a valid loan type. Try again");
                            }
                        }while(!a);
                        break;
                    }
                }

                if(match == false){
                    result = statement.executeQuery(query);
                    System.out.println();
                    // while(result.next()){
                    //     System.out.println(result.getString("social_security"));
                    // }
                    System.out.println("\nWe did not find your social security number on file. Would you like to create a new customer account? Enter \'yes\' for yes, \'no\' for no, or \'t\' to reenter your number: ");
                    String response = input.nextLine();
                    if(response.equals("yes")){
                        System.out.println();
                        boolean newAccount = false;
                        do{
                            //trying to create a new customer account
                            System.out.println("What is your first and last name?: ");
                            String name = input.nextLine();
                            System.out.println("What is your address?: ");
                            String address = input.nextLine();
                            System.out.println("What is your social security number?. Enter in the format XXX-XX-XXXX: ");
                            //check to make sure the SSN is a valid input
                            String newSocial = input.nextLine();
                            char aa = newSocial.charAt(3);
                            char bb = newSocial.charAt(6);
                            char cc = '-';
                            if(newSocial.length() != 11 || aa != cc || bb != cc){
                                System.out.println("That was not a valid SSN. Reenter your account information. ");
                            }
                            else{
                                try{
                                    //create a new customer tuple
                                    query = "insert into customer (social_security, name, address) values (\'" + newSocial + "\', \'" + name + "\', \'" + address + "\')";
                                    query(connection, query, "Error entering the new customer account");

                                    System.out.println();
                                    newAccount = true;
                                }
                                catch(Exception e){
                                    System.out.println("Could not enter the account information");
                                }
                                System.out.println("Welcome to Nickel Savings and Loan, we added your customer profile.\n");
                            }
                        }while(!newAccount);
                    }
                    else if(response.equals("no")){
                        match = true;
                        break;
                    }
                    else if(response.equals("t")){

                    }
                    else{
                        System.out.println("That was not a valid response.");
                    }
                }
            }
            catch(Exception e){
                System.out.println("Could not access the database.");
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