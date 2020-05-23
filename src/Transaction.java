import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;

public class Transaction {
    int id;
    String type; // There are two types Payment and CoinCreation
    int sentValue;
    ArrayList<Coin> createdCoins;
    ArrayList<Coin> consumedCoins; // if the type is coin creation consumedCoins will be null
    PublicKey sender;
    PublicKey receiver;
    ArrayList<String> prevTransactionHash;
    byte[] signature;

    // public Transaction(int id, String type, ArrayList<Coin> coins,
    // ArrayList<Coin> consumedCoins, PublicKey sender,
    // PublicKey receiver) {
    // this.id = id;
    // this.type = type;
    // this.receiver = receiver;
    // this.sender = sender;
    // if (type.equals("coin Creation")) {
    // this.createdCoins = new ArrayList<Coin>(coins);
    // this.consumedCoins = null;
    // } else if (type.equals("Transaction")) {
    // this.createdCoins = coins;
    // this.consumedCoins = consumedCoins;
    // }
    // }

    public Transaction(String type, int sentValue, ArrayList<Coin> consumedCoins, PublicKey sender,
            PublicKey receiver) {
        this.id = -1;
        this.type = type;
        this.receiver = receiver;
        this.sender = sender;
        this.createdCoins = new ArrayList<Coin>();
        this.sentValue = sentValue;
        if (type.equals("coin Creation")) {
            this.consumedCoins = null;
        } else if (type.equals("Transaction")) {
            this.consumedCoins = consumedCoins;
        }
    }

    public boolean verifyBalance() {
        int created = 0;
        int consumed = 0;
        for (Coin coin : this.createdCoins) {
            created += coin.value;
        }
        for (Coin coin : this.consumedCoins) {
            consumed += coin.value;
        }
        return created == consumed;
    }

    public boolean checkSignature() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (this.signature != null) {
            Signature sign = Signature.getInstance("SHA256withDSA");
            sign.initVerify(this.sender);
            sign.update(this.transactionToSign().getBytes());
            return sign.verify(this.signature);
        }
        return false;
    }

    public boolean equals(Transaction transaction) {
        if (this.id == transaction.id && this.createdCoins.equals(transaction.createdCoins)
                && this.consumedCoins.equals(transaction.consumedCoins) && this.type.equals(transaction.type)
                && this.sender.equals(transaction.sender) && this.receiver.equals(transaction.receiver)
                && this.signature.equals(transaction.signature)) {
            return true;
        }
        return false;
    }

    public String transactionToSign() {
        String output = " Type: " + this.type;

        if (this.type.equals("Transaction")) {
            output += " Consumed: " + "\n";
            for (Coin coin : this.consumedCoins) {
                output += coin.toString();
            }
        } else if (this.type.equals("coin Creation")) {
            output += " sentValue: " + this.sentValue;

        }
        if (this.prevTransactionHash != null) {
            output += "\n" + "The Prev Transactions hash: ";
            for (String prevHash : prevTransactionHash) {
                output += prevHash + "\n";
            }
        }
        output += " Sender: " + sender + " receiver: " + receiver;
        return output;

    }

    public String toString() {
        String output = " Type: " + this.type + "\n";

        if (this.type.equals("Transaction")) {
            output += " Consumed: " + "\n";
            for (Coin coin : this.consumedCoins) {
                output += "  " + coin.toString();
            }
            output += " sentValue: " + this.sentValue + "\n";
            output += " created Coins: " + "\n";
            if (this.id != -1) {
                for (Coin coin : this.createdCoins) {
                    output += "  " + coin.toString();
                }
            }
        } else if (this.type.equals("coin Creation")) {
            output += " sentValue: " + this.sentValue + "\n";
            output += " created Coins: " + "\n";
            if (this.id != -1) {
                for (Coin coin : this.createdCoins) {
                    output += coin.toString();
                }
            }
        }
        if (this.prevTransactionHash != null) {
            output += "\n" + "The Prev Transactions hash: ";
            for (String prevHash : prevTransactionHash) {
                output += prevHash + "\n";
            }
        }
        output += "\n";
        output += " Sender: " + sender.toString() + "\n" + " receiver: " + receiver.toString();
        return output;
    }
}