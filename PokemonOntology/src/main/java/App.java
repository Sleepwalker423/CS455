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
 * Third-party libraries used in this project:
 *   - OWL API 5.5.0 (https://owlapi.sourceforge.net/) — LGPL v3
 *   - Openllet 2.6.5 (https://github.com/Galigator/openllet) — MIT License
 *   - Logback 1.2.13 (https://logback.qos.ch/) — EPL v1.0 / LGPL 2.1
 */
public class App {

    public static void main(String[] args) {
        new Menu().mainMenuLoop();
    }
}
