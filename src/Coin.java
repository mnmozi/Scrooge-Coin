public class Coin {
    int indexInTransaction;
    int transactionId;
    int value;

    public Coin(int transactionId, int indexInTransaction, int value) {
        this.value = value;
        this.transactionId = transactionId;
        this.indexInTransaction = indexInTransaction;
    }

    public Coin(int value) {
        this.indexInTransaction = -1;
        this.transactionId = -1;
        this.value = value;
    }

    public String toString() {
        return "Created_At: " + transactionId + ", At_index: " + indexInTransaction + ", Worth: " + value + "\n";
    }

    public boolean equals(Coin coin) {
        if (coin.transactionId == this.transactionId && coin.indexInTransaction == this.indexInTransaction
                && coin.value == this.value) {
            return true;
        } else
            return false;
    }
}