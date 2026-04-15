/**
 * Author: Charles J. Walker
 * CS455 - Artificial Intelligence
 * Pokemon Ontology Reasoner
 *
 * Handles all user input prompts, including integer range validation
 * and string collection from standard input.
 *
 * Compile: mvn compile
 * Run:     mvn exec:java
 *
 * Code optimized using Claude Sonnet 4.6 with review of the author.
 *
 * Third-party libraries used in this project:
 *   - OWL API 5.5.0 (https://owlapi.sourceforge.net/) — LGPL v3
 *   - Openllet 2.6.5 (https://github.com/Galigator/openllet) — MIT License
 *   - Logback 1.2.13 (https://logback.qos.ch/) — EPL v1.0 / LGPL 2.1
 */
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Predicate;

public class Input {
    
    private final Scanner scan = new Scanner(System.in);

    // Prompts the user for an integer within [min, max] and loops until a valid value is entered.
    public int intPrompt(int min, int max, String requestString) {

        while (true) {
            System.out.print(requestString + " (" + min + "-" + max + "): ");
            try {
                int userInput = scan.nextInt();
                scan.nextLine(); // consume leftover newline after nextInt()
                if (userInput >= min && userInput <= max) {
                    return userInput;
                }
                System.out.println("Input out of range. Please enter a value between " + min + " and " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a single positive integer.");
                scan.nextLine(); // clear the invalid token
            }
        }
    }

    /**Prompts the user for any non-empty string and loops until one is entered. */
    public String stringPrompt(String requestString) {
        return prompt(requestString + ": ", inputString -> !inputString.trim().isEmpty(), "Input cannot be empty. Please try again.");
    }

    /** Prompts the user and loops until the input satisfies the caller-supplied validator. */
    public String prompt(String promptMessage, Predicate<String> validator, String errorMessage) {
        while (true) {
            System.out.print(promptMessage);
            try {
                String input = scan.nextLine().trim();
                if (validator.test(input)) {
                    return input;
                }
                System.out.println(errorMessage);
            } catch (NoSuchElementException e) {
                System.out.println("No input available.");
                return "";
            } catch (IllegalStateException e) {
                System.out.println("Scanner is closed.");
                return "";
            }
        }
    }

    public void close() {
        scan.close();
    }
}


