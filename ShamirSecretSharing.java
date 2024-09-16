import com.google.gson.Gson;
import java.math.BigInteger;
import java.util.Map;
import java.util.HashMap;




public class ShamirSecretSharing {
    public static BigInteger reconstructSecret1(Map<Integer, BigInteger> shares, int k) {
    BigInteger secret = BigInteger.ZERO;

    for (Map.Entry<Integer, BigInteger> share1 : shares.entrySet()) {
        BigInteger xi = BigInteger.valueOf(share1.getKey());
        BigInteger yi = share1.getValue();

        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;

        for (Map.Entry<Integer, BigInteger> share2 : shares.entrySet()) {
            if (!share1.getKey().equals(share2.getKey())) {
                BigInteger xj = BigInteger.valueOf(share2.getKey());
                numerator = numerator.multiply(xj.negate());  // (0 - xj)
                denominator = denominator.multiply(xi.subtract(xj));  // (xi - xj)
            }
        }

        // Use the modular inverse of the denominator
        BigInteger lagrangeTerm = yi.multiply(numerator).multiply(denominator.modInverse(BigInteger.valueOf(256)));
        secret = secret.add(lagrangeTerm).mod(BigInteger.valueOf(256)); // assuming modulo 256 for byte representation
    }

    return secret;
}

    // Method to parse JSON input
    public static Map<Integer, BigInteger> parseInput(String jsonInput) {
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(jsonInput, Map.class);

        Map<Integer, BigInteger> shares = new HashMap<>();
        Map<String, Object> keys = (Map<String, Object>) data.get("keys");

        int n = ((Double) keys.get("n")).intValue();
        int k = ((Double) keys.get("k")).intValue();

        System.out.println("Total shares (n): " + n);
        System.out.println("Threshold (k): " + k);

        // Extract the shares and convert them into BigInteger
        for (String key : data.keySet()) {
            if (key.equals("keys")) continue; // Skip the "keys" entry
            Map<String, String> shareData = (Map<String, String>) data.get(key);
            String base = shareData.get("base");
            String value = shareData.get("value");
            
            BigInteger shareValue = new BigInteger(value, Integer.parseInt(base));
            shares.put(Integer.parseInt(key), shareValue);
        }

        return shares;
    }

    public static BigInteger reconstructSecret(Map<Integer, BigInteger> shares, int k) {
        return reconstructSecret1(shares,k);
    }

    public static void main(String[] args) {
        // Example JSON input (You can replace it with user input)
        String jsonInput = "{\"keys\": {\"n\": 4, \"k\": 3}, \"1\": {\"base\": \"16\", \"value\": \"4\"}, \"2\": {\"base\": \"2\", \"value\": \"111\"}, \"3\": {\"base\": \"16\", \"value\": \"12\"}, \"6\": {\"base\": \"4\", \"value\": \"213\"}}";

        // Parse the JSON input to extract the shares
        Map<Integer, BigInteger> shares = parseInput(jsonInput);

        // Reconstruct the secret
        BigInteger secret = reconstructSecret(shares, 3);  // k = 3

        System.out.println("The reconstructed secret is: " + secret);
    }
}
