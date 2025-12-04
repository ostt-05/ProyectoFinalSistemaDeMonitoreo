import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtil {
    // Clave simétrica de 16 bytes (En producción esto no debe estar hardcodeado)
    private static final String KEY = "AndoBienDesvelado5";
    private static final String ALGORITHM = "AES";

    public static String encriptar(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String desencriptar(String dataEncriptada) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(dataEncriptada);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            System.err.println("Error al desencriptar: " + e.getMessage());
            return null;
        }
    }
}