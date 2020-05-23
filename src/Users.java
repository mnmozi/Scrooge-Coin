import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Users {
    private ArrayList<ArrayList<byte[]>> savedHash;
    private ArrayList<PublicKey> publicKeys;
    private ArrayList<ArrayList<Transaction>> peopleInfo;

    public Users(int numberOfUsers) throws NoSuchAlgorithmException, IOException {
        this.savedHash = new ArrayList<ArrayList<byte[]>>();
        this.publicKeys = new ArrayList<PublicKey>();
        this.peopleInfo = new ArrayList<ArrayList<Transaction>>();
        setUsers(numberOfUsers);
    }

    private void setUsers(int numberOfUsers) throws NoSuchAlgorithmException, IOException {
        File file = new File("users");
        file.mkdir();
        // if (!bool) {
        // System.out.println("Sorry couldn’t create specified directory: users");
        // }
        // create 10 users first where the first user is the scrooge
        for (int i = 0; i < numberOfUsers; i++) {
            // generate public and private key for the user and save them in a separate
            // files
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            this.publicKeys.add(publicKey);
            this.savedHash.add(new ArrayList<byte[]>());
            this.peopleInfo.add(new ArrayList<Transaction>());
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] publicKeyBytes = publicKey.getEncoded();
            byte[] privatecKeyBytes = privateKey.getEncoded();
            // creating the file directories
            File filePerUser = new File("users/" + i);
            filePerUser.mkdir();
            // if (!bool2) {
            // System.out.println("Directory " + i + " is already there");
            // }
            // save the keys at the files
            FileOutputStream publicfos = new FileOutputStream("users/" + i + "/public");
            publicfos.write(publicKeyBytes);
            publicfos.close();
            FileOutputStream privatefos = new FileOutputStream("users/" + i + "/private");
            privatefos.write(privatecKeyBytes);
            privatefos.close();

            // after saving the file we will encrypt the private file (The one that contain
            // the private key) and save the encryption in key.encrypted
            // we will give each user a password with 16 byte we will make it for all like
            // that
            String key;
            if (i < 10) {
                key = "kingbuckethead0" + i;
            } else {
                key = "kingbuckethead" + i;
            }
            // String key = "bucketheadghost" + i; // where i is the index of the user in
            // the array
            File inputFile = new File("users/" + i + "/private");
            File encryptedFile = new File("users/" + i + "/private.encrypted");

            try {
                CryptoUtils.encrypt(key, inputFile, encryptedFile);
            } catch (CryptoException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
            File f = new File("users/" + i + "/private");
            f.delete();
            // if (f.delete()) {
            // System.out.println(f.getName() + " deleted");
            // }
        }
    }

    public ArrayList<PublicKey> getPublicKeys() {
        return this.publicKeys;
    }

    public boolean addToPeopleInfo(Transaction transaction, int user) {
        return this.peopleInfo.get(user).add(transaction);
    }

    public ArrayList<ArrayList<byte[]>> getSavedHash() {
        return this.savedHash;
    }

    public ArrayList<ArrayList<Transaction>> getPeopleInfo() {
        return this.peopleInfo;
    }

    public void addToSavedHash(byte[] hash) {
        for (ArrayList<byte[]> userHash : this.savedHash) {
            userHash.add(hash);
        }
    }
}