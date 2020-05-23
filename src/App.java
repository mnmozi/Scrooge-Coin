import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static boolean sign(Transaction transaction, int user, String key, PrintStream ps)
            throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, InvalidKeyException, SignatureException, SignatureException {

        File encryptedFile = new File("users/" + user + "/private.encrypted");
        File decryptedFile = new File("users/" + user + "/private");

        try {
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            // System.out.println(ex.getMessage());
            utilities.output(ex.getMessage(), System.out, ps);
            return false;
        }
        File f = new File("users/" + user + "/private");
        FileInputStream fis = new FileInputStream(f);
        byte[] encKey = new byte[fis.available()];
        fis.read(encKey);
        fis.close();
        f.delete();

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        Signature sign = Signature.getInstance("SHA256withDSA");
        sign.initSign(privateKey);
        byte[] bytes = transaction.transactionToSign().getBytes();
        sign.update(bytes);
        transaction.signature = sign.sign();
        return true;
    }

    public static void standardHelp(PrintStream ps) {
        // System.out.println("............." + "\n" + "............." + "\n" +
        // ".............");
        utilities.output("............." + "\n" + "............." + "\n" + ".............", System.out, ps);
        // System.out.println(
        // "The command should be one of the upcomming commands:" + "\n" + "myinfo
        // <USER_NUMBER_FROM_THE_LIST>"
        // + "\n" + "send <VALUE> <FROM> <TO> <PASSWORD> <TRANSACTION_FROM_USER_INFO>" +
        // "\n"
        // + "createcoin <VALUE> <PASSWORD_OF_SCROOGE> <TO>" + "\n" + "checkblockchain
        // <user>" + "\n"
        // + "printblockchain" + " <NUMBER_OF_BLOCK_TO_PRINT>" + "\n" + "exit");

        utilities.output(
                "The command should be one of the upcomming commands:" + "\n" + "myinfo <USER_NUMBER_FROM_THE_LIST>"
                        + "\n" + "send <VALUE> <FROM> <TO> <PASSWORD> <TRANSACTION_FROM_USER_INFO>" + "\n"
                        + "createcoin <VALUE> <PASSWORD_OF_SCROOGE> <TO>" + "\n" + "checkblockchain <user>" + "\n"
                        + "printblockchain" + " <NUMBER_OF_BLOCK_TO_PRINT>" + "\n" + "exit",
                System.out, ps);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException {
        PrintStream ps;
        ps = new PrintStream(new FileOutputStream("output.txt"));
        int numberOfUsers = 100;
        Users users = new Users(numberOfUsers);
        Scrooge scrooge = new Scrooge(users.getPublicKeys(), numberOfUsers, users, ps);

        Scanner in = new Scanner(System.in);
        standardHelp(ps);
        while (in.hasNextLine()) {
            String command = in.nextLine();
            String[] commandParts = command.split(" ");
            if (commandParts.length == 2 && commandParts[0].equals("myinfo")) {
                if (commandParts.length > 2) {
                    utilities.output("invalid command", System.out, ps);
                    // System.out.println("invalid command");
                    continue;
                }
                int userIndex = Integer.parseInt(commandParts[1]);
                int i = 0;
                for (Transaction transaction : users.getPeopleInfo().get(userIndex)) {
                    // System.out.println("-------------" + "Transaction: " + i + "-------------");
                    utilities.output("-------------" + "Transaction: " + i + "-------------", System.out, ps);
                    // System.out.println(transaction.toString());
                    utilities.output(transaction.toString(), System.out, ps);
                    i++;
                }
            } else if (commandParts[0].equals("send")) {
                if (commandParts.length < 6) {
                    // System.out.println("invalid command");
                    utilities.output("invalid command", System.out, ps);
                    continue;
                }

                // get coins that he want to consume
                int value = Integer.parseInt(commandParts[1]);
                int from = Integer.parseInt(commandParts[2]);
                int to = Integer.parseInt(commandParts[3]);
                String password = commandParts[4];
                ArrayList<Transaction> userTransaction = users.getPeopleInfo().get(from);
                ArrayList<Coin> consumedCoins = new ArrayList<Coin>();
                ArrayList<String> transactionHashs = new ArrayList<String>();
                boolean errorOccured = false;
                for (int i = 5; i < commandParts.length; i++) {
                    int wantedTransaction = Integer.parseInt(commandParts[i]);
                    if (wantedTransaction > userTransaction.size() - 1) {
                        // System.out.println("the is no transactoin at that index");
                        utilities.output("the is no transactoin at that index", System.out, ps);
                        errorOccured = true;
                        break;
                    }
                    // if (wantedTransaction >
                    // userTransaction.get(wantedTransaction).createdCoins.size() - 1) {
                    // System.out.println("The Transaction you specified is not in your User
                    // array");
                    // continue;
                    // }
                    for (Coin currCoin : userTransaction.get(wantedTransaction).createdCoins) {
                        consumedCoins.add(currCoin);
                    }
                    transactionHashs.add(
                            utilities.toHexString(utilities.getSHA(userTransaction.get(wantedTransaction).toString())));

                }
                if (errorOccured) {
                    standardHelp(ps);
                    continue;
                }
                Transaction transaction = new Transaction("Transaction", value, consumedCoins,
                        users.getPublicKeys().get(from), users.getPublicKeys().get(to));
                transaction.prevTransactionHash = transactionHashs;

                boolean status = sign(transaction, from, password, ps);
                if (!status) {
                    // System.out.println("The password is wrong");
                    utilities.output("The password is wrong", System.out, ps);
                    standardHelp(ps);
                    continue;
                }
                scrooge.AddTransaction(transaction);
            } else if (commandParts[0].equals("createcoin")) {
                if (commandParts.length != 4) {
                    // System.out.println("invalid command");
                    utilities.output("invalid command", System.out, ps);
                    continue;
                }
                int value = Integer.parseInt(commandParts[1]);
                String password = commandParts[2];
                int to = Integer.parseInt(commandParts[3]);
                Transaction coinCreation = new Transaction("coin Creation", value, null, users.getPublicKeys().get(0),
                        users.getPublicKeys().get(to));
                boolean status = sign(coinCreation, 0, password, ps);
                if (!status) {
                    // System.out.println("The password is wrong");
                    utilities.output("The password is wrong", System.out, ps);
                    standardHelp(ps);
                    continue;
                }
                boolean result = scrooge.createCoin(coinCreation);
                if (result)
                    // System.out.println("COIN CREATED SCROOGE");
                    utilities.output("COIN CREATED SCROOGE", System.out, ps);
            } else if (command.equals("exit")) {
                break;
            } else if (commandParts.length == 2 && commandParts[0].equals("checkblockchain")) {
                int user = Integer.parseInt(commandParts[1]);
                ArrayList<byte[]> userHashArray = users.getSavedHash().get(user);
                Boolean result = scrooge.blockChain.sameBlockChain(userHashArray.get(userHashArray.size() - 1));
                if (result) {
                    // System.out.println("the block chain is well");
                    utilities.output("the block chain is well", System.out, ps);
                } else
                    // System.out.println("the scroope manipulated the block Chain");
                    utilities.output("the scroope manipulated the block Chain", System.out, ps);
            } else if (commandParts.length == 2 && commandParts[0].equals("printblockchain")) {
                int last = Integer.parseInt(commandParts[1]);
                // System.out.println(scrooge.blockChain.printLast(last));
                utilities.output(scrooge.blockChain.printLast(last), System.out, ps);
            }
            standardHelp(ps);

        }
        in.close();

    }

}