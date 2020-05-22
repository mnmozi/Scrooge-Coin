import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public static boolean sign(Transaction transaction, int user, String key)
            throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, InvalidKeyException, SignatureException, SignatureException {

        File encryptedFile = new File("users/" + user + "/private.encrypted");
        File decryptedFile = new File("users/" + user + "/private");

        try {
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
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

    public static void standardHelp() {
        System.out.println("............." + "\n" + "............." + "\n" + ".............");
        System.out.println(
                "The command should be one of the upcomming commands:" + "\n" + "myinfo <USER_NUMBER_FROM_THE_LIST>"
                        + "\n" + "send <VALUE> <FROM> <TO> <PASSWORD> <TRANSACTION_FROM_USER_INFO>" + "\n"
                        + "createcoin <VALUE> <PASSWORD_OF_SCROOGE> <TO>" + "\n" + "checkblockchain <user>" + "\n"
                        + "printblockchain" + "\n" + "exit");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException {

        int numberOfUsers = 10;
        Users users = new Users(numberOfUsers);
        Scrooge scrooge = new Scrooge(users.getPublicKeys(), numberOfUsers, users);

        Scanner in = new Scanner(System.in);
        standardHelp();
        while (in.hasNextLine()) {
            String command = in.nextLine();
            String[] commandParts = command.split(" ");
            if (commandParts[0].equals("myinfo")) {
                if (commandParts.length > 2) {
                    System.out.println("invalid command");
                    continue;
                }
                int userIndex = Integer.parseInt(commandParts[1]);
                int i = 0;
                for (Transaction transaction : users.getPeopleInfo().get(userIndex)) {
                    System.out.println("-------------" + "Transaction: " + i + "-------------");
                    System.out.println(transaction.toString());
                    i++;
                }
            } else if (commandParts[0].equals("send")) {
                if (commandParts.length < 6) {
                    System.out.println("invalid command");
                    continue;
                }

                // get coins that he want to consume
                int value = Integer.parseInt(commandParts[1]);
                int from = Integer.parseInt(commandParts[2]);
                int to = Integer.parseInt(commandParts[3]);
                String password = commandParts[4];
                ArrayList<Transaction> userTransaction = users.getPeopleInfo().get(from);
                ArrayList<Coin> consumedCoins = new ArrayList<Coin>();
                boolean errorOccured = false;
                for (int i = 5; i < commandParts.length; i++) {
                    int wantedTransaction = Integer.parseInt(commandParts[i]);
                    if (wantedTransaction > userTransaction.size() - 1) {
                        System.out.println("the is no transactoin at that index");
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
                }
                if (errorOccured) {
                    standardHelp();
                    continue;
                }
                Transaction transaction = new Transaction("Transaction", value, consumedCoins,
                        users.getPublicKeys().get(from), users.getPublicKeys().get(to));
                boolean status = sign(transaction, from, password);
                if (!status) {
                    System.out.println("The password is wrong");
                    standardHelp();
                    continue;
                }
                scrooge.AddTransaction(transaction);
            } else if (commandParts[0].equals("createcoin")) {
                if (commandParts.length != 4) {
                    System.out.println("invalid command");
                    continue;
                }
                int value = Integer.parseInt(commandParts[1]);
                String password = commandParts[2];
                int to = Integer.parseInt(commandParts[3]);
                Transaction coinCreation = new Transaction("coin Creation", value, null, users.getPublicKeys().get(0),
                        users.getPublicKeys().get(to));
                boolean status = sign(coinCreation, 0, password);
                if (!status) {
                    System.out.println("The password is wrong");
                    standardHelp();
                    continue;
                }
                boolean result = scrooge.createCoin(coinCreation);
                if (result)
                    System.out.println("COIN CREATED SCROOGE");
            } else if (command.equals("exit")) {
                break;
            } else if (commandParts.length == 2 && commandParts[0].equals("checkblockchain")) {
                int user = Integer.parseInt(commandParts[1]);
                ArrayList<byte[]> userHashArray = users.getSavedHash().get(user);
                Boolean result = scrooge.blockChain.sameBlockChain(userHashArray.get(userHashArray.size() - 1));
                if (result) {
                    System.out.println("the block chain is well");
                } else
                    System.out.println("the scroope manipulated the block Chain");
            } else if (command.equals("printblockchain")) {
                System.out.println(scrooge.blockChain.toString());
            }
            standardHelp();

        }
        in.close();

    }

}