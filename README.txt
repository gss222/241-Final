Graham Shanno
CSE 241 Project
ReadMe

Directory setup:

Top level-> gss222shanno
-gss222 (folder)
-ojdbc8.jar
-gss222.jar
-README.txt
-Makefile
-Tables.txt

gss222 folder->
-all  java files and classes
-Manifest.txt

Makefile- 
moves all of the classes and files from gss222 into the upper level directory,
compiles the java files, produces the new gss222.jar, then moves java/classes back into gss222
-in case of compiler error, 'make clean' will move the classes back into gss222 

Commands:

make
java -jar gss222.jar



NOTES ON ER DESIGN AND INTERFACE DESIGN:

-If a customer entered their SSN, savings_ID, checking_ID, card_number, or account_ID incorrectly, I displayed
all the respective IDs that were in the database for usability and efficiency. Data for savings_ID and checking_ID was a randomly generated, 8-12 digit passcode.
Data for account_ID is a randomly generated 8 digit code . Used mockaroo.com for random data generation into SQL
-A loan can be taken out by a customer, but that customer does not have to have an account with the bank. 
The transaction tuple added for a loan payment will have an account_ID attribute of null. 
-Every loan, credit payment, loan payment, and deposit must happen at a branch with a teller. 
-possible transaction types: credit payment, loan payment, deposit, withdrawal. Each transaction tuple will be associated with a branchID, and have
a randomly generated, 4 digit code (primary key)
-interest rates are randomly generated (an int between 0 and 5)
-purchaseID and transactionID (primary keys for transaction and purchase) -> randomly generated 4 digit numbers
-can add a new customer account through the loan interface if the social security number is not recognized


Classes and interfaces:

Main-> establishes the oracle connection, and displays the menu screen. Will prompt the user to enter a digit 
for what interface they wish to use. Will then go to that interface. Program won't terminate until the user enters the number to quit ('5')

DepositWithdrawal-> Lists all of the branches and their respective IDs. User will input what branch they are
at. If the branch they choose only has an atm, they will only be allowed to make a debit withdrawal. If the branch
has a teller, they can deposit/withdraw into a choice of their savings or checking account. The respective account
balances will be updated, and a new transactions tuple will be inserted. (will randomly generate a transaction_ID, 
and add the transaction_type, amount, respective account_ID, and the respective branch_ID)

Loan (also allows the customer to make a new account if they don't have a social security nummber on record)
-> Similar to DepositWithdrawal, the interface will list all the available branches. If the user is at a
branch that only has an atm, they will not be able to make a new loan. If they enter their social security number
incorrectly/SSN is not on record, interface will prompt the user if they want to make a new customer account. Once
they correctly make a new account, the interface will ask the user to enter their new social security number
to make the purchase with. 

Purchases-> Customer will be prompted to enter their card number that they are making the purchase with. The
interface will determine if the card is debit or credit, and then check to make sure that the purchase is not
greater than the balance (if debit), or purchase + balance_due is not greater than the credit limit (for credit).
If not, the account balances will be updated, and a new purchase tuple will be generated (amount, card_number, 
and a randomly generated 4 digit purchaseID). 

LoanCreditPayment-> interface will prompt the customer if they want to make a loan or credit payment. If loan, they
must enter their social security number used for the loan. For credit, they must insert their credit card number.
Then it will ask how much the payment is, and make sure that payment is not greater than the actual balance that
is due. A transaction tuple will be added to the database with transaction type 'loan payment' or 'credit payment'
and the (balance_due) for credit and (loan_balance) for loan will be updated. 

Sample run:

Enter username:
gss222
Enter password:
2v98pd6n!

Welcome to Nickel Savings and Loan!
___________________________________
Users:
1. Customer Card Purchases
2. Account Deposit/Withdrawal
3. Taking out a new loan
4. Make a payment on a loan or creditcard
5. Quit
Enter your user type. Example- Enter '1' to make a purchase with a card. User:
1

5602234256705813
4913001735854958
3571024435012797
2017792415873651
3555923742751908
3546151354370617
3576749797431004
6763514391721549
3585635867655243
4405653112594816
6771053926289465
5108751684112343
3742888837972831
5602247781270651
3568905614762985
3481571166411412
3034378792679711
3538457274950796
3560287604805829
5602228935117170
5602213041363607
3557723415284262
3563464126746792
3529262409805894
3533071597463414
4041593792722404
6398441854126227
3558985410367967
3532091084446079
5602259233043374
3583447467839173
3552399489445066
5641826719220875
5602252772309033
5100175692926262
3573333179321092
6333039228647657
6304809262660332
3569519085856678
3723014601314127
3552455821192645
5100137332477155
3566874877732303
5641826497050452
2014130835911232
4936915329690033
6331102283361272
3021118596800211
5602228632729068
3544790675593209
2016580999737471
6759766988431226
5602253412093045
3551290932049822
3554909916821289

Welcome: Please enter you card number that you plan on making the purchase with:
3554909916821289
We found your card on the file system.

Here are your details for that debit card:
Card number: 3554909916821289
CheckingID: DduEMwrS
Here are the corresponding checking account details:
CheckingID: DduEMwrS
Balance: 7804
Interest rate: 7
AccountID: 36042728

What is the purchase price?:
8000
The purchase amount is greater than your balance. Not valid.
What is the purchase price?:
7000

Purchase has been added to the system and balances have been updated. Thank you.
New account balance = 804.0
--------------------------------------------------------------------------------
Users:
1. Customer Card Purchases
2. Account Deposit/Withdrawal
3. Taking out a new loan
4. Make a payment on a loan or creditcard
5. Quit
Enter your user type. Example- Enter '1' to make a purchase with a card. User:
3

Welcome. Here are a list of active branches we have, and their respective ID's:
Location                    Branch ID
--------                    ---------
Philadelphia                   101
Bethlehem                      102
New York                       103
Mount Frere                    104
Valley Forge                   105
Pittsburgh                     106
Newark                         107
Scranton                       108
Trenton                        109
Syracuse                       110
Please enter the branch's ID that you are performing this transaction at:
101

Welcome to the Philadelphia branch!
This branch only has an atm, and cannot perform a loan transaction. Sorry.

Users:
1. Customer Card Purchases
2. Account Deposit/Withdrawal
3. Taking out a new loan
4. Make a payment on a loan or creditcard
5. Quit
Enter your user type. Example- Enter '1' to make a purchase with a card. User:
3

Welcome. Here are a list of active branches we have, and their respective ID's:
Location                    Branch ID
--------                    ---------
Philadelphia                   101
Bethlehem                      102
New York                       103
Mount Frere                    104
Valley Forge                   105
Pittsburgh                     106
Newark                         107
Scranton                       108
Trenton                        109
Syracuse                       110
Please enter the branch's ID that you are performing this transaction at:
102

Welcome to the Bethlehem branch!
This branch has both an atm and a teller. Would you like to take out a loan? Enter 'yes' for yes, or 'quit' to quit:
yes

909-98-1111
265-63-4478
776-80-8561
889-85-8764
689-70-1757
445-05-6936
237-54-2160
152-83-6705
136-76-5234
723-37-5395
543-01-5530
533-82-4995
383-15-7450
346-60-3720
273-10-8681
371-50-4325
191-09-4588
147-20-4252
524-80-9793
299-03-5281
696-56-5859
347-25-6233
662-28-2372
799-44-2582
405-98-9534
143-32-8779
787-26-2969
441-58-5233
339-93-5908
165-75-6213
230-28-5814
394-00-1928
000-00-0000

What is your social security number?:
3


We did not find your social security number on file. Would you like to create a new customer account? Enter 'yes' for yes, 'no' for no, or 't' to reenter your number:
yes

What is your first and last name?:
Graham Shanno
What is your address?:
608 waterfall way
What is your social security number?. Enter in the format XXX-XX-XXXX:
000-11-0000

Welcome to Nickel Savings and Loan, we added your customer profile.


909-98-1111
000-11-0000
265-63-4478
776-80-8561
889-85-8764
689-70-1757
445-05-6936
237-54-2160
152-83-6705
136-76-5234
723-37-5395
543-01-5530
533-82-4995
383-15-7450
346-60-3720
273-10-8681
371-50-4325
191-09-4588
147-20-4252
524-80-9793
299-03-5281
696-56-5859
347-25-6233
662-28-2372
799-44-2582
405-98-9534
143-32-8779
787-26-2969
441-58-5233
339-93-5908
165-75-6213
230-28-5814
394-00-1928
000-00-0000

What is your social security number?:
000-11-0000

We found your social security number on file.
What kind of loan would you like to take out? Enter 'mortgage' for mortgage and 'unsecured' for unsecured:
mortgage
What is the balance for the loan?:
9000
How many months will you be paying off this loan?:
10

New loan has been entered.
Interest rate: 0
Monthly payment: 900.0
Balance: 9000.0

Thank you.

Users:
1. Customer Card Purchases
2. Account Deposit/Withdrawal
3. Taking out a new loan
4. Make a payment on a loan or creditcard
5. Quit
Enter your user type. Example- Enter '1' to make a purchase with a card. User:
5
Thank you for using Nickel Savings and Loan!