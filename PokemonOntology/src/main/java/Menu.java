/**
 * Author: Charles J. Walker
 * CS455 - Artificial Intelligence
 * Pokemon Ontology Reasoner
 *
 * Manages all interactive menus for querying the Pokemon ontology,
 * including menus for Pokemon, types, moves, and trainers.
 *
 * Compile: mvn compile
 * Run:     mvn exec:java
 *
 * Third-party libraries used in this project:
 *   - OWL API 5.5.0 (https://owlapi.sourceforge.net/) — LGPL v3
 *   - Openllet 2.6.5 (https://github.com/Galigator/openllet) — MIT License
 *   - Logback 1.2.13 (https://logback.qos.ch/) — EPL v1.0 / LGPL 2.1
 */
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Menu {

    private final List<String> pokemonTypes = new ArrayList<>();
    private final List<String> trainers = new ArrayList<>();
    private final Input input = new Input();
    private final String path = "pokemon_with_moves_formatted.ttl";
    private PokemonOntology pokeOntology;


    Menu() {
        pokemonTypes.add("Bug");
        pokemonTypes.add("Dragon");
        pokemonTypes.add("Electric");
        pokemonTypes.add("Fighting");
        pokemonTypes.add("Fire");
        pokemonTypes.add("Flying");
        pokemonTypes.add("Ghost");
        pokemonTypes.add("Grass");
        pokemonTypes.add("Ground");
        pokemonTypes.add("Ice");
        pokemonTypes.add("Normal");
        pokemonTypes.add("Poison");
        pokemonTypes.add("Psychic");
        pokemonTypes.add("Rock");
        pokemonTypes.add("Water");

        trainers.add("Agatha");
        trainers.add("Blaine");
        trainers.add("Blue1");
        trainers.add("Blue2");
        trainers.add("Blue3");
        trainers.add("Brock");
        trainers.add("Bruno");
        trainers.add("Erika");
        trainers.add("Giovanni");
        trainers.add("Koga");
        trainers.add("Lance");
        trainers.add("Lorelei");
        trainers.add("LtSurge");
        trainers.add("Misty");
        trainers.add("Sabrina");

        try {
                pokeOntology = new PokemonOntology(path);
        } catch (OWLOntologyCreationException e) {
             System.err.println("Failed to load ontology: " + e.getMessage());
        }
    }

    public void mainMenuLoop() {

        int userNumber = -1;
        String userString = "";
        boolean exitProgram = false;

        while (exitProgram == false) {
            printMainMenu();
            userNumber = input.intPrompt(0, 7, "Please enter the number of the desired menu option");
            switch (userNumber) {
                case 1:
                    pokemonMenuLoop();
                    break;
                case 2:
                    moveMenuLoop();
                    break;
                case 3:
                    trainerMenuLoop();
                    break;
                case 4:
                    System.out.println("=== Type Menu ===");
                    userString = input.stringPrompt("Please enter the type name (e.g. Fire)");
                    pokeOntology.listTypeInfo(userString);
                    break;
                case 5:
                    pokeOntology.listAllPokemon();
                    break;
                case 6:
                    pokeOntology.listAllMoves();
                    break;
                case 7:
                    pokeOntology.listAllTypes();   
                    break;
                case 0:
                    exitProgram = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void pokemonMenuLoop() {
        int userNumber = -1;
        String userString = "";
        boolean returnToMainMenu = false;

        while (returnToMainMenu == false) {
            printPokemonMenu();
            userNumber = input.intPrompt(0, 7, "Please enter the number of the desired menu option");
            switch (userNumber) {
                case 1:
                    userString = input.stringPrompt("Please enter the name of the Pokemon you would like to search for");
                    pokeOntology.listIndividualsStrengthsAndWeaknesses(userString);
                    break;
                case 2:
                    userNumber = input.intPrompt(1, 151, "Please enter the pokedex number");
                    pokeOntology.pokedexNumberSearch(userNumber);
                    break;
                case 3:
                    pokemonTypeMenuLoop();
                    break;
                case 4:
                    userString = input.stringPrompt("Please enter the name of the move");
                    pokeOntology.listPokemonByMove(userString);
                    break;
                case 5:
                    userString = input.stringPrompt("Please enter the name of the weakness you would like to search for (e.g. Fire)");
                    pokeOntology.listPokemonWithWeakness(userString);
                    break;
                case 6:
                    userString = input.stringPrompt("Please enter the name of the strength you would like to search for (e.g. Fire)");
                    pokeOntology.listPokemonWithStrength(userString);
                    break;
                case 7:
                    userString = input.stringPrompt("Please enter the name of the Pokemon to search for its evolutionary line");
                    pokeOntology.listPokemonInEvolutionaryLine(userString);
                    break;
                case 0:
                    returnToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void moveMenuLoop() {
        int userNumber = -1;
        String userString = "";
        boolean returnToMainMenu = false;

        while (!returnToMainMenu) {
            printMoveMenu();
            userNumber = input.intPrompt(0, 3, "Please enter the number of the desired menu option");
            switch (userNumber) {
                case 1:
                    userString = input.stringPrompt("Please enter the name of the move");
                    pokeOntology.listMoveInfo(userString);
                    break;
                case 2:
                    userString = input.stringPrompt("Please enter the type name (e.g. Fire)");
                    pokeOntology.listMovesByType(userString);
                    break;
                case 3:
                    userString = input.stringPrompt("Please enter the name of the Pokemon");
                    pokeOntology.listMovesByPokemon(userString);
                    break;
                case 0:
                    returnToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void trainerMenuLoop() {
        int userNumber = -1;
        String userString = "";
        boolean returnToMainMenu = false;

        while (!returnToMainMenu) {
            printTrainerMenu();
            userNumber = input.intPrompt(0, 3, "Please enter the number of the desired menu option");
            switch (userNumber) {
                case 1:
                    printSelectTrainerMenu();
                    int trainerChoice1 = input.intPrompt(0, trainers.size(), "Please enter the number of the trainer");
                    if (trainerChoice1 == 0) 
                        break;
                    pokeOntology.listTrainerInfo(trainers.get(trainerChoice1 - 1));
                    break;
                case 2:
                    userString = input.stringPrompt("Please enter the name of the Pokemon");
                    pokeOntology.listTrainersWithPokemon(userString);
                    break;
                case 3:
                    printSelectTrainerMenu();
                    int trainerChoice3 = input.intPrompt(0, trainers.size(), "Please enter the number of the trainer");
                    if (trainerChoice3 == 0) 
                        break;
                    pokeOntology.listPokemonSuggestionsForTrainer(trainers.get(trainerChoice3 - 1));
                    break;
                case 0:
                    returnToMainMenu = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    private void pokemonTypeMenuLoop() {
        int userNumber = -1;
        boolean returnToParent = false;

        while (!returnToParent) {
            printPokemonTypesMenu();
            userNumber = input.intPrompt(0, 2, "Please enter the number of the desired menu option");
            switch (userNumber) {
                case 1: {
                    // Single-type search: show numbered type list, map selection to class name.
                    printPokemonTypesMenuSingle();

                    int typeChoice = input.intPrompt(1, pokemonTypes.size() + 1, "Select a type");

                    //Return to previous menu option.
                    if (typeChoice == pokemonTypes.size() + 1)
                         break;

                    String typeName = pokemonTypes.get(typeChoice - 1);
                
                    pokeOntology.listInstancesOf(typeName + "TypePokemon");
                    break;
                }
                case 2: {
                    // Dual-type search: pick first type, then second, then query.
                    printPokemonTypesMenuSingle();
                    int choice1 = input.intPrompt(1, pokemonTypes.size() + 1, "Select the first type");
                    
                    //Return to previous menu option.
                    if (choice1 == pokemonTypes.size() + 1) 
                        break;
                    
                    String type1 = pokemonTypes.get(choice1 - 1);

                    printPokemonTypesMenuCombination(type1);

                    int choice2 = input.intPrompt(1, pokemonTypes.size() + 1, "Select the second type");
                    
                    //Return to previous menu option.
                    if (choice2 == pokemonTypes.size() + 1) 
                        break;

                    String type2 = pokemonTypes.get(choice2 - 1);

                    // Individual name convention: Fire, Water, etc.
                    pokeOntology.listPokemonByTwoTypes(type1, type2);
                    break;
                }
                case 0:
                    returnToParent = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }

    public void printMainMenu() {
        System.out.println(
            "=== Main Menu ===\n" +
            "1. Search for a Pokemon \n" +
            "2. Search for a Move \n" +
            "3. Search for a Trainer \n" +
            "4. Search for a Type \n" +
            "5. List all Pokemon \n" +
            "6. List all moves \n" +
            "7. List all types \n" +
            "0. Exit Program"
        );
    }

    public void printPokemonMenu() {
        System.out.println(
            "=== Pokemon Menu ===\n" +
            "1. Search for a Pokemon by name \n" +
            "2. Search for a Pokemon by pokedex number \n" +
            "3. Search for Pokemon by type \n" +
            "4. Search for Pokemon by move \n" +
            "5. Search for Pokemon by weakness \n" +
            "6. Search for Pokemon by strength \n" +
            "7. Search for Pokemon by evolutionary line \n" +
            "0. Return to previous menu"
        );
    }

    public void printMoveMenu() {
        System.out.println(
            "=== Move Menu ===\n" +
            "1. Search for a Move by name \n" +
            "2. Search for Moves by type (e.g. Fire) \n" +
            "3. Search for Moves a Pokemon can learn \n" +
            "0. Return to previous menu"
        );
    }

    public void printTrainerMenu() {
        System.out.println(
            "=== Trainer Menu ===\n" +
            "1. Search for a Trainer by name \n" +
            "2. Search for Trainers by Pokemon they have \n" +
            "3. List the pokemon suggestions for a given trainer \n" +
            "0. Return to previous menu"
        );
    }

    public void printSelectTrainerMenu() {
        System.out.println(
            "=== Select a Trainer Menu ===\n" +
            "1. Agatha \n" +
            "2. Blaine \n" +
            "3. Blue1 \n" +
            "4. Blue2 \n" +
            "5. Blue3 \n" +
            "6. Brock \n" +
            "7. Bruno \n" +
            "8. Erika \n" +
            "9. Giovanni \n" +
            "10. Koga \n" +
            "11. Lance \n" +
            "12. Lorelei \n" +
            "13. LtSurge \n" +
            "14. Misty \n" +
            "15. Sabrina \n" +
            "0. Return to previous menu"
        );
    }

    public void printPokemonTypesMenu() {
           System.out.println(
            "=== Pokemon Types Menu ===\n" +
            "How would you like to search?\n" +
            "1. Search by a single type \n" +
            "2. Search by a combination of two types \n" +
            "0. Return to previous menu"
        );
    }

    public void printPokemonTypesMenuSingle() {
        System.out.println(
            "=== Pokemon Types Menu ===\n" +
            "Please select the corresponding number for the desired type from the following list:\n"
        );
        // Loops through the list of pokemon types an prints them with the corresponding number for selection.
        for(int i =0; i < pokemonTypes.size(); i++) {
            System.out.println((i+1) + ". " + pokemonTypes.get(i));
        }
        System.out.println((pokemonTypes.size() + 1) + ". Return to previous menu");
    }

    public void printPokemonTypesMenuCombination(String type1) {
        System.out.println(
            "=== Pokemon Types Menu ===\n" +
            "Please select the corresponding number for the second desired type from the following list:\n"
        );
        // Loops through the list of pokemon types an prints them with the corresponding number for selection.
        for(int i =0; i < pokemonTypes.size(); i++) {
            if (pokemonTypes.get(i).equals(type1)) {
                continue; // Skip the first selected type to avoid duplicates.
            }
            System.out.println((i+1) + ". " + pokemonTypes.get(i));
        }
        System.out.println((pokemonTypes.size() + 1) + ". Return to previous menu");
    }
}
