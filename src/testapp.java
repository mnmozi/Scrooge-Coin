import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

public class testapp {
    public static void sign(Transaction transaction, int user)
            throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidKeyException, InvalidKeyException, SignatureException, SignatureException {
        File f = new File("users/" + user + "/private");
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
        transaction.signature = sign.sign();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException,
            SignatureException, InvalidKeySpecException, NoSuchProviderException {
        ArrayList<PublicKey> publicKeys = new ArrayList<PublicKey>();

        // create the users folder that will contain all the users info
        File file = new File("users");
        boolean bool = file.mkdir();
        if (!bool) {
            System.out.println("Sorry couldn’t create specified directory: users");
        }
        // create 10 users first where the first user is the scrooge
        for (int i = 0; i < 10; i++) {
            // generate public and private key for the user and save them in a separate
            // files
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            publicKeys.add(publicKey);
            PrivateKey privateKey = keyPair.getPrivate();
            byte[] publicKeyBytes = publicKey.getEncoded();
            byte[] privatecKeyBytes = privateKey.getEncoded();
            // creating the file directories
            File filePerUser = new File("users/" + i);
            boolean bool2 = filePerUser.mkdir();
            if (!bool2) {
                System.out.println("Directory " + i + " is already there");
            }
            // save the keys at the files
            FileOutputStream publicfos = new FileOutputStream("users/" + i + "/public");
            publicfos.write(publicKeyBytes);
            publicfos.close();
            FileOutputStream privatefos = new FileOutputStream("users/" + i + "/private");
            privatefos.write(privatecKeyBytes);
            privatefos.close();
        }
        // test that the scrooge can create coins
        Scrooge scrooge = new Scrooge(publicKeys);
        Coin firstCoin = new Coin(10);
        Coin secondCoin = new Coin(20);
        ArrayList<Coin> coins = new ArrayList<Coin>();
        coins.add(firstCoin);
        coins.add(secondCoin);
        Transaction coinCreationTrans = new Transaction("coin Creation", coins, null, publicKeys.get(0),
                publicKeys.get(1));
        sign(coinCreationTrans, 0);
        scrooge.createCoin(coinCreationTrans);

        System.out.println("dsf");
        // test a transaction from 1 to 2
        ArrayList<Coin> user1Coin1 = new ArrayList<Coin>(scrooge.peopleInfo.get(1).get(0).createdCoins);
        Transaction sendfrom1to2 = new Transaction("Transaction", user1Coin1,
                scrooge.peopleInfo.get(1).get(0).createdCoins, publicKeys.get(1), publicKeys.get(2));
        sign(sendfrom1to2, 1);
        scrooge.AddTransaction(sendfrom1to2);
        // test doubleSend
        ArrayList<Coin> user1Coin1Double = new ArrayList<Coin>(scrooge.peopleInfo.get(1).get(0).createdCoins);
        Transaction sendfrom1to2Double = new Transaction("Transaction", user1Coin1Double,
                scrooge.peopleInfo.get(1).get(0).createdCoins, publicKeys.get(1), publicKeys.get(2));
        sign(sendfrom1to2Double, 1);
        scrooge.AddTransaction(sendfrom1to2Double);
        // send from the second to the 3rd
        ArrayList<Coin> user2Coin1 = new ArrayList<Coin>(scrooge.peopleInfo.get(2).get(0).createdCoins);
        Transaction sendfrom2to3 = new Transaction("Transaction", user2Coin1,
                scrooge.peopleInfo.get(2).get(0).createdCoins, publicKeys.get(2), publicKeys.get(3));
        sign(sendfrom2to3, 2);
        scrooge.AddTransaction(sendfrom2to3);
    }

}