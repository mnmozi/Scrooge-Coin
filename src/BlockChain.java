import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class BlockChain {
    ArrayList<Block> blockChain;

    public BlockChain() {
        this.blockChain = new ArrayList<Block>();
    }

    public byte[] AddBlock(Block block) throws NoSuchAlgorithmException {
        // This block is the first one
        if (blockChain.size() == 0) {
            block.prevBlockHashHex = null;
        } else {
            // another block in the system
            Block lastBlock = blockChain.get(blockChain.size() - 1);
            byte[] lastBlockHash = utilities.getSHA(lastBlock.tohashString());
            String hashHex = utilities.toHexString(lastBlockHash);
            block.prevBlockHashHex = hashHex;
        }
        blockChain.add(block);
        return utilities.getSHA(block.toString());
    }

    // public boolean coinAvailability() {

    // }

    // public boolean coinOwnership() {

    // }
    // take the user hash and see if there is a block in the chain with that hash
    // if so then we return ture and print how many blocks does the user did not
    // see
    public boolean sameBlockChain(byte[] hash) {
        String currhashHex = utilities.toHexString(hash);
        for (int i = this.blockChain.size() - 1; i >= 1; i--) {
            Block lastblock = this.blockChain.get(i);
            byte[] rehash = utilities.getSHA(lastblock.toString());
            String rehashHex = utilities.toHexString(rehash);
            if (!currhashHex.equals(rehashHex)) {
                return false;
            }
            currhashHex = lastblock.prevBlockHashHex;
        }
        return true;

    }

    public String toString() {
        int i = 0;

        String output = "CURRUNT Block Chain \n";
        for (Block block : blockChain) {
            output += block.toString() + "(" + i + ")" + "\n";
            i++;
        }
        return output;

    }

    public String printLast(int last) {
        int blocksize = this.blockChain.size();
        String output = "";
        if (last == -1) {
            for (int i = 0; i < blocksize; i++) {
                Block block = this.blockChain.get(i);
                output += block.toString() + "(" + i + ")" + "\n";
            }
        } else {
            for (int i = blocksize - (last); i < blocksize; i++) {
                Block block = this.blockChain.get(i);
                output += block.toString() + "(" + i + ")" + "\n";
            }
        }
        return output;
    }

    public int size() {
        return blockChain.size();
    }
}