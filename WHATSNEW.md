# New Param: 'stat_type' 

The header 'stat_type' can be typed in the alterPatterns.csv file to change how the suspicious accounts decide how much money to transact.

Here are the inputs currently allowed in the 'stat_type" column
-[0]: The default method for determining transaction amount. Typically a random number between min amount and max amount. This is the default mode if stat_type is absent from the header
-[1]: A chi-squared sampling with three degrees of freedom is sampled and shifted between the min and max amount. 

[Coming Soon:] Implementation for 'stat_type' in the accounts.csv param file to allow control of how the transaction amount is determined for all accounts. 


# New Scbema: schemaBankFormat

Added [dataType] 'country' support for accounts in all schemas such that country data carries over into final output. 


The header of each output csv is the comma seperated list of order [name] components in the schema. 

The following is a pre-existing list of keywords to put in the [dataType] field of the schema which will be understood to the code. This is how the code knows how to grab information from the tmp files and assign them to proper hearder in the final output. 
-[Transactions]
    -[transaction_id]: The unique transaction identifier assigned by the simulator
    -[orig_id]: The account number of the sender 
    -[dest_id]: The acount number of the receiver 
    -[amount]: The transaction amount
    -[timestamp]: The simulation step at which the transaction occured. 
    -[sar_flag]: Boolean telling whether or not the transaction is a suspicious activity report
    -[alert_id]: Integer representing if the transaction was alerted.
-[Account]
    -


