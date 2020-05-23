
import java.util.ArrayList;

public class Block {
    int ID;
    byte[] selfHash;
    ArrayList<Transaction> transactions;
    String prevBlockHashHex;
    int transactionsInblock = 10;

    public Block(String prevBlockHash) {
        this.prevBlockHashHex = prevBlockHash;
        this.transactions = new ArrayList<Transaction>();

    }

    public int size() {
        return this.transactions.size();
    }

    public boolean add(Transaction transaction) {
        return this.transactions.add(transaction);
    }

    public boolean checkLength() {
        if (this.transactions.size() == transactionsInblock) {
            return true;
        }
        return false;
    }

    public String tohashString() {
        String output = "-------------Block------------- \n";
        for (int i = 0; i < this.transactions.size(); i++) {
            output += this.transactions.get(i).toString() + "\n";
        }
        output += "Hash of Previous Block is: " + this.prevBlockHashHex;
        return output;
    }

    public String toString() {
        String output = "-------------Block------------- \n";
        output += "ID: " + this.ID + "\n";
        output += "Self Hash: " + utilities.toHexString(this.selfHash) + "\n";
        output += "-------------Transactions-------------" + "\n";
        for (int i = 0; i < this.transactions.size(); i++) {
            output += this.transactions.get(i).toString() + "\n";
        }
        output += "Hash of Previous Block is: " + this.prevBlockHashHex;
        return output;
    }

}
