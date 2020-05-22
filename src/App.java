import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        // this will save the the public keys of the users to uses them later
        // ArrayList<PublicKey> publicKeys = new ArrayList<PublicKey>();

        // create the users folder that will contain all the users info
        // File file = new File("users");
        // boolean bool = file.mkdir();
        // if (!bool) {
        // System.out.println("Sorry couldn’t create specified directory: users");
        // }
        // // create 10 users first where the first user is the scrooge
        // for (int i = 0; i < 10; i++) {
        // // generate public and private key for the user and save them in a separate
        // // files
        // KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        // keyPairGenerator.initialize(1024);
        // KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // PublicKey publicKey = keyPair.getPublic();
        // publicKeys.add(publicKey);
        // PrivateKey privateKey = keyPair.getPrivate();
        // byte[] publicKeyBytes = publicKey.getEncoded();
        // byte[] privatecKeyBytes = privateKey.getEncoded();
        // // creating the file directories
        // File filePerUser = new File("users/" + i);
        // boolean bool2 = filePerUser.mkdir();
        // if (!bool2) {
        // System.out.println("Sorry couldn’t create specified directory: " + i);
        // }
        // // save the keys at the files
        // FileOutputStream publicfos = new FileOutputStream("users/" + i + "/public");
        // publicfos.write(publicKeyBytes);
        // publicfos.close();
        // FileOutputStream privatefos = new FileOutputStream("users/" + i +
        // "/private");
        // privatefos.write(privatecKeyBytes);
        // privatefos.close();
        // }

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        byte[] keyy = privateKey.getEncoded();
        FileOutputStream keyfoss = new FileOutputStream("private");
        keyfoss.write(keyy);
        keyfoss.close();

        File ff = new File("private");
        FileInputStream fiss = new FileInputStream(ff);
        byte[] encKeyy = new byte[fiss.available()];
        fiss.read(encKeyy);
        fiss.close();

        PKCS8EncodedKeySpec privateKeySpecc = new PKCS8EncodedKeySpec(encKeyy);
        KeyFactory keyFactoryy = KeyFactory.getInstance("DSA", "SUN");
        PrivateKey privateKeyfile = keyFactoryy.generatePrivate(privateKeySpecc);

        Signature sign = Signature.getInstance("SHA256withDSA");
        sign.initSign(privateKeyfile);
        byte[] bytes = "Hello how are you".getBytes();
        sign.update(bytes);
        byte[] signature = sign.sign();
        System.out.println(signature);

        Signature sign2 = Signature.getInstance("SHA256withDSA");
        sign2.initVerify(publicKey);
        sign2.update(bytes);

        System.out.println(sign2.verify(signature));

        byte[] key = publicKey.getEncoded();
        FileOutputStream keyfos = new FileOutputStream("suepk");
        keyfos.write(key);
        keyfos.close();

        File f = new File("suepk");
        FileInputStream fis = new FileInputStream(f);
        byte[] encKey = new byte[fis.available()];
        fis.read(encKey);
        fis.close();

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
        PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

        System.out.println(pubKey.equals(publicKey) + " the keys are");

        Signature sign22 = Signature.getInstance("SHA256withDSA");
        sign22.initVerify(pubKey);
        sign22.update(bytes);
        System.out.println(sign22.verify(signature));
    }
}
