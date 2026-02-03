import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

public class Main {
    public static void main(String[] args){
        try {
            // default user data
            String masterPassword = "SuperPassword123";
            String secretSitePassword = "LloydsPassword123";

            // generate random Salt & IV
            byte[] salt = new byte [16];
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            System.out.println("--- SentinelVault Encryption Test ---");
            System.out.println("Original Password: " + secretSitePassword);

            // derive key from master pw


        }


    }


}