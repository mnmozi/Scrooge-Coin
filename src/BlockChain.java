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
            byte[] lastBlockHash = utilities.getSHA(lastBlock.toString());
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

    public int size() {
        return blockChain.size();
    }
}