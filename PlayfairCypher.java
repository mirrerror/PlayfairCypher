import java.util.*;

public class PlayfairCypher {
    private static final String RUSSIAN_ALPHABET = "АБВГДЕЖЗИКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final int MATRIX_SIZE = 5;  // 5 rows and 7 columns
    private char[][] playfairMatrix;
    private Map<Character, int[]> charPositions;

    public void createMatrix(String key) {
        Set<Character> usedChars = new LinkedHashSet<>();
        for (char c : key.toCharArray()) {
            if (RUSSIAN_ALPHABET.indexOf(c) != -1) {
                usedChars.add(c);
            }
        }
        for (char c : RUSSIAN_ALPHABET.toCharArray()) {
            usedChars.add(c);
        }

        playfairMatrix = new char[MATRIX_SIZE][7];
        charPositions = new HashMap<>();
        int index = 0;
        for (char c : usedChars) {
            int row = index / 7;
            int col = index % 7;
            playfairMatrix[row][col] = c;
            charPositions.put(c, new int[]{row, col});
            index++;
        }
    }

    public String processMessage(String message, String key, boolean encrypt) {
        if (key.length() < 7) {
            throw new IllegalArgumentException("Key length must be at least 7 characters.");
        }

        createMatrix(key.toUpperCase());
        message = formatMessage(message.toUpperCase());
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < message.length(); i += 2) {
            char a = message.charAt(i);
            char b = message.charAt(i + 1);
            int[] posA = charPositions.get(a);
            int[] posB = charPositions.get(b);

            if (posA[0] == posB[0]) {  // Same row
                result.append(playfairMatrix[posA[0]][(posA[1] + (encrypt ? 1 : 6)) % 7]);
                result.append(playfairMatrix[posB[0]][(posB[1] + (encrypt ? 1 : 6)) % 7]);
            } else if (posA[1] == posB[1]) {  // Same column
                result.append(playfairMatrix[(posA[0] + (encrypt ? 1 : 4)) % 5][posA[1]]);
                result.append(playfairMatrix[(posB[0] + (encrypt ? 1 : 4)) % 5][posB[1]]);
            } else {  // Rectangle swap
                result.append(playfairMatrix[posA[0]][posB[1]]);
                result.append(playfairMatrix[posB[0]][posA[1]]);
            }
        }

        return result.toString();
    }

    private String formatMessage(String message) {
        StringBuilder formatted = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (RUSSIAN_ALPHABET.indexOf(c) != -1) {
                formatted.append(c);
            }
        }

        for (int i = 0; i < formatted.length() - 1; i += 2) {
            if (formatted.charAt(i) == formatted.charAt(i + 1)) {
                formatted.insert(i + 1, 'Ь');  // Use "Ь" as padding if same letters are adjacent
            }
        }

        if (formatted.length() % 2 != 0) {
            formatted.append('Ь');  // Pad with "Ь" if message length is odd
        }

        return formatted.toString();
    }

    public void printMatrix() {
        for (char[] row : playfairMatrix) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PlayfairCypher cipher = new PlayfairCypher();

        System.out.println("Enter the key (at least 7 characters, Russian alphabet only):");
        String key = scanner.nextLine().toUpperCase();

        System.out.println("Choose operation: (1) Encrypt or (2) Decrypt");
        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the message:");
        String message = scanner.nextLine().toUpperCase();

        boolean encrypt = choice == 1;
        String result = cipher.processMessage(message, key, encrypt);

        System.out.println(encrypt ? "Encrypted message:" : "Decrypted message:");
        System.out.println(result);
    }
}
