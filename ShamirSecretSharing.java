import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.math.BigInteger;

public class ShamirSecretSharing {
    static class Root {
        String base;
        String value;
    }

    static class Keys {
        int n;
        int k;
    }

    static class InputData {
        Keys keys;
        Map<String, Root> roots;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String jsonInput = scanner.nextLine();
        scanner.close();

        Gson gson = new Gson();
        InputData data = gson.fromJson(jsonInput, InputData.class);

        int secret = findSecret(data);
        System.out.println(secret);
    }

    private static int findSecret(InputData data) {
        List<Point> points = new ArrayList<>();
        for (Map.Entry<String, Root> entry : data.roots.entrySet()) {
            int x = Integer.parseInt(entry.getKey());
            int y = decodeValue(entry.getValue().value, Integer.parseInt(entry.getValue().base));
            points.add(new Point(x, y));
        }

        if (points.size() < data.keys.k) {
            throw new IllegalArgumentException("Not enough roots provided");
        }

        return reconstructPolynomial(points);
    }

    private static int decodeValue(String value, int base) {
        return new BigInteger(value, base).intValue();
    }

    private static int reconstructPolynomial(List<Point> points) {
        int result = 0;
        for (int i = 0; i < points.size(); i++) {
            int term = points.get(i).y;
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    term = term * (-points.get(j).x) / (points.get(i).x - points.get(j).x);
                }
            }
            result += term;
        }
        return result;
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
