## Examples

Here I will assume i'm user 1 and as default my password will be "kingbuckethead01" and I want to send 5 coins to uesr 2 
   
   1. to view my coins (The transactions where i recive coins)
      - myinfo 1
   2. after seeing my transaction I will select one of them. Since I only have one coin that is worth 10 (The default coin) I can reference it
      - send 5 1 2 kingbuckethead01 0
      
Scrooge will print the block that he is still working on with my transaction accepted and a new transaction where scrooge sending me the remainder of my coin in a new coin (But I can't user it or the coin I consumed untill he add that block to the blockChain)
When scrooge add the block that he is working on I will see my remainder transaction when I use (myinfo 1)

---

Here I'm the scrooge and I want to send user 1 a coin with a value 50.
   1. I need to know my password which is "kingbuckethead00" as default and will simply write the following
      - createcoin 50 kingbuckethead00 1

Then I will get a message "COIN CREATED SCROOGE"
   
## commands

1. info (USER_INDEX_IN_THE_SCROOGE_ARRAY)

   - this will give the user an array with all the transactions where he is the reciver of the coins. Now the user can get reference of that coins that he recived and send it to another user in a new transaction.

2. send (VALUE) (SENDER) (RECEIVER) (YOURPASSWORD) (COINS_REFERENCES)
- BUT there are multiple situations in this case
- The password specified are wrong so the transaction will not complete
- The referenced coins in the transaction sum (total value) are not enough for the value he specified
  - Here the transaction will not be processed and will get a log of that.
- The referenced coins are just the value he specified
  - Then the coin will be consumed.
- The coins in the transaction sum (total value) are more than the value he specified.
  - Here the scrooge will create a new coin with value of the remainder and send it to the sender and the value will be send to the       reciver.
- One of the referenced coins was consumed before
  - The transaction will no be processed
  
3. createcoin <VALUE> <SCROOGEPASSWORD> <RECEIVER>
   - here the scrooge will make a coin with the value specified and make a transaction to the receiver
4. checkblockchain <USER>
    - here the user will check the blockchian of the scrooge by the hash of the last block he reseived when the last block was added
    - if the blockchain was modified the user will get a massege "the block Chain was manipulated" otherwise the user will get "the block chain is well".

5. printblockchain <NUMBER_OF_BLOCK_TO_PRINT>
   - here the last specified blocks in the blockchain will be printed with its transactions










