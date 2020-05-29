import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

public class Scrooge {
    ArrayList<PublicKey> publicKeys;
    // an array of arrays that give the users a simple way to get the
    // recived coins transactions
    ArrayList<ArrayList<Transaction>> peopleInfo;
    BlockChain blockChain;
    private Block currentBlock;
    private byte[] lastBlockHash;
    private int blocksize = 10;
    public Users users;
    public PrintStream ps;

    public Scrooge(ArrayList<PublicKey> publicKeys, int numberOfUsers, Users users, PrintStream ps)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException,
            NoSuchProviderException, IOException, InvalidKeySpecException, NoSuchProviderException, IOException,
            InvalidKeySpecException, NoSuchProviderException, IOException, InvalidKeySpecException,
            NoSuchProviderException, IOException {
        this.ps = ps;
        this.users = users;
        this.publicKeys = new ArrayList<PublicKey>(publicKeys);
        this.peopleInfo = new ArrayList<ArrayList<Transaction>>();
        for (int i = 0; i < publicKeys.size(); i++) {
            this.peopleInfo.add(new ArrayList<Transaction>());
        }
        currentBlock = new Block(null);
        this.blockChain = new BlockChain();
        createFirstBlock(numberOfUsers);
    }

    // in here i will add 10 transactions with 10 coins to the scrooge as a start
    private void createFirstBlock(int numberOfUsers) throws InvalidKeyException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException, IOException {
        for (int i = 0; i < numberOfUsers; i++) {
            // Coin coin = new Coin(i, 0, 1);
            // ArrayList<Coin> coins = new ArrayList<Coin>();
            // coins.add(coin);
            Transaction coinCreationTransaction = new Transaction("coin Creation", 10, null, publicKeys.get(0),
                    publicKeys.get(i));

            coinCreationTransaction.signature = sign(coinCreationTransaction);
            createCoin(coinCreationTransaction);
        }
        boolean shouldAdd = currentBlock.checkLength();
        createNewBlock(shouldAdd);
    }

    // create new coins only scrooge can create a coin and send it to the wanted one
    public boolean createCoin(Transaction coinCreationTransaction) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException, IOException {

        boolean signatureVerify = checkScroogeSignature(coinCreationTransaction);
        if (!signatureVerify) {
            // System.out.println("coin creation failed: you are not the scrooge");
            utilities.output("coin creation failed: you are not the scrooge", System.out, this.ps);
            return false;

        }
        coinCreationTransaction.id = blockChain.size() * blocksize + this.currentBlock.size();
        int index = 0;
        // make a coin with the value
        Coin newCoin = new Coin(coinCreationTransaction.sentValue);
        coinCreationTransaction.createdCoins.add(newCoin);
        for (Coin coin : coinCreationTransaction.createdCoins) {
            coin.transactionId = coinCreationTransaction.id;
            coin.indexInTransaction = index++;
        }
        this.currentBlock.add(coinCreationTransaction);
        // if (result) {
        // System.out.println("the trasaction sent succesfully");
        // }
        boolean shouldAdd = currentBlock.checkLength();
        createNewBlock(shouldAdd);
        return true;
        // // updating the users data
        // int indexOfUser = publicKeys.indexOf(coinCreationTransaction.receiver);
        // peopleInfo.get(indexOfUser).add(coinCreationTransaction);
    }

    private boolean checkScroogeSignature(Transaction transaction)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] signature = transaction.signature;
        if (signature != null) {
            Signature sign = Signature.getInstance("SHA256withDSA");
            sign.initVerify(publicKeys.get(0));
            byte[] bytes = transaction.transactionToSign().getBytes();
            sign.update(bytes);
            return sign.verify(signature);
        }
        return false;
    }

    private void createNewBlock(boolean shouldAdd) throws NoSuchAlgorithmException {
        if (shouldAdd) {
            this.currentBlock.ID = this.blockChain.size();
            this.currentBlock.selfHash = utilities.getSHA(this.currentBlock.tohashString());
            this.lastBlockHash = this.blockChain.AddBlock(this.currentBlock);
            users.addToSavedHash(this.lastBlockHash);
            // loop over the transactions in that block and update the user's info
            for (Transaction transaction : currentBlock.transactions) {
                if (transaction.type == "coin Creation") {
                    int indexOfUser = publicKeys.indexOf(transaction.receiver);
                    users.addToPeopleInfo(transaction, indexOfUser);
                } else if (transaction.type.equals("Transaction")) {
                    int indexOfReciver = publicKeys.indexOf(transaction.receiver);
                    users.addToPeopleInfo(transaction, indexOfReciver);
                }
            }
            // System.out.println("-------------SCROOOGE ADDED A NEW BLOCK TO THE BLOCK
            // CHAIN-------------");
            utilities.output("-------------SCROOOGE ADDED A NEW BLOCK TO THE BLOCK CHAIN-------------", System.out,
                    this.ps);
            this.currentBlock = new Block(utilities.toHexString(lastBlockHash));
        }
    }

    public boolean AddTransaction(Transaction transaction) throws InvalidKeyException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException, IOException {

        // validate the transaction in the rest of the blockchain
        if (transaction.type.equals("Transaction")) {
            // check the signature first
            boolean signatureVerify = transaction.checkSignature();
            if (!signatureVerify)
                return false;
            // get the coin id which contains the transaction Id and go for that ID and
            // check if the transaction is for him
            int totalConsumedCoins = 0;
            for (Coin coin : transaction.consumedCoins) {
                int transactionId = coin.transactionId;
                totalConsumedCoins += coin.value;
                // since I made the BlockChain is a array list we can jump to the block of the
                // transaction :D
                int lengthOfBlockChain = this.blockChain.size();
                int wantedBlock = transactionId / blocksize;
                if (wantedBlock > lengthOfBlockChain - 1) {
                    // System.out.println("the block of the transaction does not exist");
                    utilities.output("the block of the transaction does not exist", System.out, this.ps);
                    return false;
                }
                int wantedTrnsactionInBlock = (transactionId % blocksize);
                Transaction wantedTransaction = blockChain.blockChain.get(wantedBlock).transactions
                        .get(wantedTrnsactionInBlock);

                if (!wantedTransaction.createdCoins.get(coin.indexInTransaction).equals(coin)) {
                    // System.out.println("the coin that you want to spend is not equal to the one
                    // in the blockChain");
                    utilities.output("the coin that you want to spend is not equal to the one in the blockChain",
                            System.out, this.ps);
                    return false;
                }

                // check if the coin wasn't consumed in the upcomming transactions
                // first check in the published blocks
                for (int i = wantedBlock; i < blockChain.size(); i++) {
                    Block currentBlock = blockChain.blockChain.get(i);
                    for (Transaction transactionsInBlock : currentBlock.transactions) {
                        if (transactionsInBlock.consumedCoins != null) {

                            for (Coin innerCoin : transactionsInBlock.consumedCoins) {
                                if (coin.equals(innerCoin)) {
                                    // System.out.println("the coin was consumed before that transaction");
                                    utilities.output("the coin was consumed before that transaction", System.out,
                                            this.ps);
                                    utilities.output(this.currentBlock.tohashString(), System.out, this.ps);
                                    return false;
                                }
                            }
                        }
                    }
                }
                // second we check the current block that wasn't published yet
                for (int i = 0; i < this.currentBlock.size(); i++) {
                    Transaction currenTransaction = currentBlock.transactions.get(i);
                    if (currenTransaction.consumedCoins == null) {
                        continue;
                    }
                    for (Coin innerCoin : currenTransaction.consumedCoins) {
                        if (coin.equals(innerCoin)) {
                            // System.out.println("the coin was consumed before that transaction");
                            utilities.output("the coin was consumed before that transaction", System.out, this.ps);
                            utilities.output(this.currentBlock.tohashString(), System.out, this.ps);
                            return false;
                        }
                    }
                }
            }

            // else the transaction is valied the scrooge consume the whole coin and send
            // the sender a new coin with the remainder
            if (transaction.sentValue <= totalConsumedCoins) {
                if (transaction.sentValue < totalConsumedCoins) {
                    Transaction coinCreationTransaction = new Transaction("coin Creation",
                            totalConsumedCoins - transaction.sentValue, null, publicKeys.get(0), transaction.sender);
                    coinCreationTransaction.signature = sign(coinCreationTransaction);
                    createCoin(coinCreationTransaction);
                }

                //
                Coin newCoin = new Coin(transaction.sentValue);
                transaction.createdCoins.add(newCoin);
                transaction.id = blockChain.size() * blocksize + this.currentBlock.size();
                int index = 0;
                for (Coin coin : transaction.createdCoins) {
                    coin.transactionId = transaction.id;
                    coin.indexInTransaction = index++;
                }
                // System.out.println("Transaction send to scrooge");
                utilities.output("Transaction send to scrooge", System.out, this.ps);
                currentBlock.add(transaction);
                // System.out.println(this.currentBlock.tohashString());
                utilities.output(this.currentBlock.tohashString(), System.out, this.ps);
                boolean shouldAdd = currentBlock.checkLength();
                createNewBlock(shouldAdd);
                return true;
            } else {
                // System.out.println(
                // "The sum of the coins You want to consume are less than the value that you
                // want to send");
                utilities.output(
                        "The sum of the coins You want to consume are less than the value that you want to send",
                        System.out, this.ps);
            }

        }
        // System.out.println("the type is not recognized");
        utilities.output("the type is not recognized", System.out, this.ps);
        return false;
    }

    public void printCurrBlock() {
        utilities.output(this.currentBlock.tohashString(), System.out, this.ps);
    }

    private byte[] sign(Transaction transaction) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, InvalidKeyException, SignatureException, SignatureException {

        String key = "kingbuckethead00";
        File encryptedFile = new File("users/" + 0 + "/private.encrypted");
        File decryptedFile = new File("users/" + 0 + "/private");

        try {
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            // System.out.println(ex.getMessage());
            utilities.output(ex.getMessage(), System.out, this.ps);
            ex.printStackTrace();
        }

        File f = new File("users/0/private");
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

        return sign.sign();
    }
}