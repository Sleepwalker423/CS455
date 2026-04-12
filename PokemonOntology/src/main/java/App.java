/**
 * Author: Charles J. Walker
 * CS455 - Artificial Intelligence
 * Pokemon Ontology Reasoner
 *
 * Entry point for the application. Launches the main menu loop.
 *
 * Compile: mvn compile
 * Run:     mvn exec:java
 *
 * Code optimized using Claude Sonnet 4.6 with review of the author.
 */
public class App {

    public static void main(String[] args) {
        new Menu().mainMenuLoop();
    }
}
