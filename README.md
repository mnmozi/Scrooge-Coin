## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Dependency Management

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).

## Dependency Management

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).

## commands

1. info (USER_INDEX_IN_THE_SCROOGE_ARRAY)

   - this will give the user an array with all the transactions where he is the reciver of the coins. Now the user can get reference of that coins that he recived and send it to another user in a new transaction.

2. send (VALUE) (FROM) (TO) (COIN_REFERENCE)
   BUT there are multiple situations in this case

- The referenced coins in the transaction sum (total value) are not enough for the value he specified
  - Here the transaction will not be processed and will get a log of that.
- The referenced coins are just the value he specified
  - Then the coin will be consumed.
- The coins in the transaction sum (total value) are more than the value he specified.
  - Here the scrooge will create a new coin with value of the remainder and send it to the sender and the value will be send to the reciver.
- One of the referenced coins was consumed before
  - The transaction will no be processed
