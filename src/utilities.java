import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class utilities {

    public static byte[] getSHA(String input) {
        // Static getInstance method is called with hashing SHA
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte

    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public static void output(String text, PrintStream ps1, PrintStream ps2) {
        ps1.println(text);
        ps2.println(text);
    }
}