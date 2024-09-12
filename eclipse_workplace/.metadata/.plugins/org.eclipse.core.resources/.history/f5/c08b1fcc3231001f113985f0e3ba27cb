package pak;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void grepWithRegex(String pattern, String filename) {
        Pattern regexPattern = Pattern.compile(pattern);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = regexPattern.matcher(line);
                if (matcher.find()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error opening file: " + filename);
            e.printStackTrace(); 
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("pattern:");
        String pattern = scanner.nextLine();

        System.out.println("filename:");
        String filename = scanner.nextLine();

        grepWithRegex(pattern, filename);

        scanner.close();
    }
}