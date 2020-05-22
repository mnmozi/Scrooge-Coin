import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    Block currentBlock;
    byte[] lastBlockHash;
    int blocksize = 1;

    public Scrooge(ArrayList<PublicKey> publicKeys) throws InvalidKeyException, NoSuchAlgorithmException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException, IOException, InvalidKeySpecException,
            NoSuchProviderException, IOException, InvalidKeySpecException, NoSuchProviderException, IOException,
            InvalidKeySpecException, NoSuchProviderException, IOException {

        this.publicKeys = new ArrayList<PublicKey>(publicKeys);
        this.peopleInfo = new ArrayList<ArrayList<Transaction>>();
        for (int i = 0; i < publicKeys.size(); i++) {
            this.peopleInfo.add(new ArrayList<Transaction>());
        }
        currentBlock = new Block(null);
        this.blockChain = new BlockChain();
        createFirstBlock();
    }

    // in here i will add 10 transactions with 10 coins to the scrooge as a start
    private void createFirstBlock() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException,
            InvalidKeySpecException, NoSuchProviderException, IOException {
        for (int i = 0; i < 10; i++) {
            Coin coin = new Coin(i, 0, 1);
            ArrayList<Coin> coins = new ArrayList<Coin>();
            coins.add(coin);
            Transaction coinCreationTransaction = new Transaction("coin Creation", coins, null, publicKeys.get(0),
                    publicKeys.get(0));

            coinCreationTransaction.signature = sign(coinCreationTransaction);
            createCoin(coinCreationTransaction);
        }
        boolean shouldAdd = currentBlock.checkLength();
        createNewBlock(shouldAdd);
    }

    // create new coins only scrooge can create a coin and send it to the wanted one
    public void createCoin(Transaction coinCreationTransaction) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException, IOException {

        boolean signatureVerify = checkScroogeSignature(coinCreationTransaction);
        if (!signatureVerify) {
            System.out.println("coin creation failed: you are not the scrooge");
            return;
        }
        coinCreationTransaction.id = blockChain.size() * blocksize + this.currentBlock.size();
        int index = 0;
        for (Coin coin : coinCreationTransaction.createdCoins) {
            coin.transactionId = coinCreationTransaction.id;
            coin.indexInTransaction = index++;
        }
        boolean result = this.currentBlock.add(coinCreationTransaction);
        System.out.println("the trasaction is: " + result);
        boolean shouldAdd = currentBlock.checkLength();
        createNewBlock(shouldAdd);

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
            byte[] bytes = transaction.toString().getBytes();
            sign.update(bytes);
            return sign.verify(signature);
        }
        return false;
    }

    private void createNewBlock(boolean shouldAdd) throws NoSuchAlgorithmException {
        if (shouldAdd) {
            this.lastBlockHash = this.blockChain.AddBlock(this.currentBlock);
            // loop over the transactions in that block and update the user's info
            for (Transaction transaction : currentBlock.transactions) {
                if (transaction.type == "coin Creation") {
                    int indexOfUser = publicKeys.indexOf(transaction.receiver);
                    peopleInfo.get(indexOfUser).add(transaction);
                } else if (transaction.type.equals("Transaction")) {
                    // update the user's easy access data
                    int indexOfReciver = publicKeys.indexOf(transaction.receiver);
                    peopleInfo.get(indexOfReciver).add(transaction);

                    // remove the transaction from the sender array

                    int indexOfSender = publicKeys.indexOf(transaction.sender);
                    // loop over the user info and get the transaction with the sent coins
                    // transaction id
                    for (int i = 0; i < peopleInfo.get(indexOfSender).size(); i++) {
                        if (peopleInfo.get(indexOfSender).get(i).id == transaction.id) {
                            peopleInfo.get(indexOfSender).remove(i);
                            break;
                        }
                    }
                }
            }

            this.currentBlock = new Block(utilities.toHexString(lastBlockHash));
        }
    }

    public boolean AddTransaction(Transaction transaction)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {

        // validate the transaction in the rest of the blockchain
        if (transaction.type.equals("Transaction")) {
            // check the signature first
            boolean signatureVerify = transaction.checkSignature();
            if (!signatureVerify)
                return false;
            // get the coin id which contains the transaction Id and go for that ID and
            // check if the transaction is for him
            for (Coin coin : transaction.consumedCoins) {
                int transactionId = coin.transactionId;
                // since I made the BlockChain is a array list we can jump to the block of the
                // transaction :D
                int lengthOfBlockChain = this.blockChain.size();
                int wantedBlock = transactionId / blocksize;
                if (wantedBlock > lengthOfBlockChain - 1) {
                    System.out.println("the block of the transaction does not exist");
                    return false;
                }
                int wantedTrnsactionInBlock = (transactionId % blocksize);
                Transaction wantedTransaction = blockChain.blockChain.get(wantedBlock).transactions
                        .get(wantedTrnsactionInBlock);

                if (!wantedTransaction.createdCoins.get(coin.indexInTransaction).equals(coin)) {
                    System.out.println("the coin that you want to spend is not equal to the one in the blockChain");
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
                                    System.out.println("the coin was consumed before that transaction");
                                    return false;
                                }
                            }
                        }
                    }
                }
                // second we check the current block that wasn't published yet
                for (int i = 0; i < this.currentBlock.size(); i++) {
                    Transaction currenTransaction = currentBlock.transactions.get(i);
                    for (Coin innerCoin : currenTransaction.consumedCoins) {
                        if (coin.equals(innerCoin)) {
                            System.out.println("the coin was consumed before that transaction");
                            return false;
                        }
                    }
                }
            }

            // else the transaction is valied
            transaction.id = blockChain.size() * blocksize + this.currentBlock.size();
            int index = 0;
            for (Coin coin : transaction.createdCoins) {
                coin.transactionId = transaction.id;
                coin.indexInTransaction = index++;
            }
            currentBlock.add(transaction);
            boolean shouldAdd = currentBlock.checkLength();
            createNewBlock(shouldAdd);
            return true;

        }
        System.out.println("the type is not recognized");
        return false;
    }

    public boolean checkCoin() {
        // check if the coin wasn't used in the blockChain
        return false;
    }

    private byte[] sign(Transaction transaction) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException, InvalidKeyException, SignatureException, SignatureException {
        File f = new File("users/0/private");
        FileInputStream fis = new FileInputStream(f);
        byte[] encKey = new byte[fis.available()];
        fis.read(encKey);
        fis.close();

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        Signature sign = Signature.getInstance("SHA256withDSA");
        sign.initSign(privateKey);
        byte[] bytes = transaction.toString().getBytes();
        sign.update(bytes);
        return sign.sign();
    }
}