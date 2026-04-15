/**
 * Author: Charles J. Walker
 * CS455 - Artificial Intelligence
 * Pokemon Ontology Reasoner
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

import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import openllet.owlapi.OpenlletReasonerFactory;

public class PokemonOntology {

    private final OWLReasoner reasoner;
    private final OWLDataFactory factory;
    // Pokemon species, types, and object properties namespace
    private static final String NS_POKEMON = "http://www.example.org/pokemon-ontology#";
    // Moves and ontology-header classes namespace
    private static final String NS_ONTOLOGY = "http://www.semanticweb.org/giosu/ontologies/2025/5/pokemon-ontology-v2#";
    private final SimpleShortFormProvider sfp = new SimpleShortFormProvider();

    OWLObjectProperty canLearnMove;
    OWLObjectProperty hasEvolutionarySuccessor;
    OWLObjectProperty hasMoveType;
    OWLObjectProperty hasPokemon;
    OWLObjectProperty hasType;
    OWLObjectProperty isImmuneTo;
    OWLObjectProperty isStrongAgainst;
    OWLObjectProperty isWeakTo;
    OWLClass pokemonSpecies;
    OWLClass pokemonType;
    OWLClass pokemonMove;
    OWLClass pokemonTrainer;
    OWLDataProperty pokedexNumber;


    public PokemonOntology(String path) throws OWLOntologyCreationException {

        //Required OWL API setup to load the ontology and create a reasoner.
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        factory  = manager.getOWLDataFactory();

        // Allows for printing short names instead of full IRIs in query results.
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));

        // Initialize object properties from the ontology for later use in queries.
        canLearnMove                = factory.getOWLObjectProperty(IRI.create(NS_ONTOLOGY + "canLearnMove"));
        hasEvolutionarySuccessor    = objProp("hasEvolutionarySuccessor");
        hasMoveType                 = factory.getOWLObjectProperty(IRI.create(NS_ONTOLOGY + "hasMoveType"));
        hasPokemon                  = factory.getOWLObjectProperty(IRI.create(NS_ONTOLOGY + "hasPokemon"));
        hasType                     = objProp("hasType");
        isImmuneTo                  = objProp("isImmuneTo");
        isStrongAgainst             = objProp("isStrongAgainst");
        isWeakTo                    = objProp("isWeakTo");
        // Pre-build frequently used OWL classes and data property.
        pokemonSpecies      = cls(NS_POKEMON,  "PokemonSpecies");
        pokemonType         = cls(NS_POKEMON,  "PokemonType");
        pokemonMove         = cls(NS_ONTOLOGY, "PokemonMove");
        pokemonTrainer      = cls(NS_ONTOLOGY, "PokemonTrainer");
        pokedexNumber       = dataProp("pokedexNumber");

        // Create the reasoner and precompute inferences.
        reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);
        reasoner.precomputeInferences(
            InferenceType.CLASS_HIERARCHY,
            InferenceType.CLASS_ASSERTIONS
        );

        //Checks for consistency and prints a message if the ontology was loaded successfully.
        if (!reasoner.isConsistent())
            throw new IllegalStateException("Ontology is inconsistent.");

        System.out.println("Loaded and classified: " + path + "\n");
    }
//-------------------------------------------------------------------------------------------
//----------------------------------- IRI builder helpers -----------------------------------
//-------------------------------------------------------------------------------------------

    /** Returns a class using the given namespace (NS_POKEMON or NS_ONTOLOGY). */
    private OWLClass cls(String ns, String name) {
        return factory.getOWLClass(IRI.create(ns + name));
    }

    /** Returns an individual using the given namespace (NS_POKEMON or NS_ONTOLOGY). */
    private OWLNamedIndividual ind(String ns, String name) {
        return factory.getOWLNamedIndividual(IRI.create(ns + name));
    }

    /** Returns an object property in the Pokemon namespace. */
    private OWLObjectProperty objProp(String name) {
        return factory.getOWLObjectProperty(IRI.create(NS_POKEMON + name));
    }

    /** Returns a data property in the Pokemon namespace. */
    private OWLDataProperty dataProp(String name) {
        return factory.getOWLDataProperty(IRI.create(NS_POKEMON + name));
    }

//-------------------------------------------------------------------------------------------
//-------------------------------------- Query helpers --------------------------------------
//-------------------------------------------------------------------------------------------

    /** Returns all reasoner-inferred instances of a class expression as a stream. */
    private Stream<OWLNamedIndividual> streamInstances(OWLClassExpression cls) {
        return reasoner.getInstances(cls, false).getFlattened().stream();
    }

    /** Returns all values of an object property for an individual as a flat set. */
    private Set<OWLNamedIndividual> getValues(OWLNamedIndividual individual, OWLObjectProperty property) {
        return reasoner.getObjectPropertyValues(individual, property).getFlattened();
    }

    /** Returns true if the reasoner finds the given Pokedex number as a data property value. */
    private boolean hasPokedexNumber(OWLNamedIndividual individual, int number) {
        return reasoner.getDataPropertyValues(individual, pokedexNumber).stream()
            .anyMatch(lit -> lit.parseInteger() == number);
    }

//-------------------------------------------------------------------------------------------
//-------------------------------------- Print helpers --------------------------------------
//-------------------------------------------------------------------------------------------

    /** Prints all reasoner-inferred instances of an OWL class, sorted alphabetically. */
    private void printAllOfClass(OWLClass owlClass) {
        streamInstances(owlClass).map(sfp::getShortForm).sorted().forEach(System.out::println);
        System.out.println();
    }

    /** Prints a stream of individuals as sorted names, each indented by two spaces. */
    private void printIndented(Stream<OWLNamedIndividual> stream) {
        stream.map(sfp::getShortForm).sorted()
              .forEach(name -> System.out.println("  " + name));
    }

    /**
     * Prints all values of an object property for an individual, each prefixed with a label.
     * e.g. printObjectProperty(charizard, hasType, "Type") prints "    Type: FireType"
     */
    private void printObjectProperty(OWLNamedIndividual individual, OWLObjectProperty property, String label) {
        getValues(individual, property).stream()
            .map(sfp::getShortForm).sorted()
            .forEach(value -> System.out.println("    " + label + ": " + value));
    }

    private void printPokemonProfile(OWLNamedIndividual individual) {
        String pokeDexNum = reasoner.getDataPropertyValues(individual, pokedexNumber).stream()
            .map(lit -> String.valueOf(lit.parseInteger())).findFirst().orElse("?");
        System.out.println("=== " + sfp.getShortForm(individual) + " (#" + pokeDexNum + ") ===");
        getValues(individual, hasType).forEach(type -> {
                System.out.println("  Type: " + sfp.getShortForm(type));
                printObjectProperty(type, isStrongAgainst, "Strong against");
                printObjectProperty(type, isWeakTo, "Weak to");
                printObjectProperty(type, isImmuneTo, "Immune to");
            });
        System.out.println();
    }

//-------------------------------------------------------------------------------------------
//------------------------------------ List-all methods -------------------------------------
//-------------------------------------------------------------------------------------------

    /** Prints all move individuals in the ontology. */
    public void listAllMoves() {
        System.out.println("=== Moves ===");
        printAllOfClass(pokemonMove);
    }

    /** Prints all Pokemon species individuals in the ontology. */
    public void listAllPokemon() {
        System.out.println("=== Pokemon ===");
        printAllOfClass(pokemonSpecies);
    }

    /** Prints all type individuals in the ontology. */
    public void listAllTypes() {
        System.out.println("=== Types ===");
        printAllOfClass(pokemonType);
    }

//-------------------------------------------------------------------------------------------
//--------------------------------- Pokemon search methods ----------------------------------
//-------------------------------------------------------------------------------------------

    /**
     * Prints all reasoner-inferred instances of the named class (e.g., "FireTypePokemon").
     * Uses the Pokemon namespace for the class IRI.
     */
    public void listInstancesOf(String className) {
        System.out.println("=== " + className + " ===");
        printIndented(streamInstances(cls(NS_POKEMON, className)));
        System.out.println();
    }

    /**
     * Prints the Pokemon with the given Pokedex number along with its type
     * strengths and weaknesses. Searches all PokemonSpecies individuals for a
     * matching hasPokedexNumber data property value.
     */
    public void pokedexNumberSearch(int number) {
        System.out.println("=== Pokemon #" + number + " ===");
        streamInstances(pokemonSpecies)
            .filter(individual -> hasPokedexNumber(individual, number))
            .sorted(Comparator.comparing(sfp::getShortForm))
            .forEach(this::printPokemonProfile);
    }

    /**
     * Prints all Pokemon whose type has isWeakTo pointing at the given type individual
     * (e.g., pass "FireType" to find all Pokemon weak to fire).
     */
    public void listPokemonWithWeakness(String typeName) {
        System.out.println("=== Pokemon weak to " + typeName + " ===");

        printIndented(
            //Find Pokemon with the condition "hasType" "isWeakTo" the given type "typeName".
            streamInstances(factory.getOWLObjectIntersectionOf(
            //Contains all Pokemon species.
            pokemonSpecies,
            factory.getOWLObjectSomeValuesFrom(hasType,
                factory.getOWLObjectSomeValuesFrom(isWeakTo,
                    factory.getOWLObjectOneOf(ind(NS_POKEMON, typeName)))))));
        System.out.println();
    }

    /**
     * Prints all Pokemon whose type has isStrongAgainst pointing at the given type individual
     * (e.g., pass "FireType" to find all Pokemon strong against fire).
     */
    public void listPokemonWithStrength(String typeName) {
        System.out.println("=== Pokemon strong against " + typeName + " ===");
        printIndented(streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonSpecies,
            factory.getOWLObjectSomeValuesFrom(hasType,
                factory.getOWLObjectSomeValuesFrom(isStrongAgainst,
                    factory.getOWLObjectOneOf(ind(NS_POKEMON, typeName)))))));
        System.out.println();
    }

    /**
     * Prints all Pokemon in the same evolutionary line as the named Pokemon.
     * Finds the EvolutionaryLine individual whose hasPokemon set includes the target,
     * then lists every Pokemon in that line.
     */
    public void listPokemonInEvolutionaryLine(String pokemonName) {
        System.out.println("=== Evolutionary line of " + pokemonName + " ===");
        OWLNamedIndividual target = ind(NS_POKEMON, pokemonName);
        // Use the reasoner's transitive hasEvolutionarySuccessor to find all descendants.
        Stream<OWLNamedIndividual> descendants = getValues(target, hasEvolutionarySuccessor).stream();
        // Use a class expression to find all ancestors (Pokemon X such that X hasEvolutionarySuccessor target).
        Stream<OWLNamedIndividual> ancestors = streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonSpecies,
            factory.getOWLObjectSomeValuesFrom(hasEvolutionarySuccessor,
                factory.getOWLObjectOneOf(target))));
        printIndented(Stream.concat(Stream.concat(Stream.of(target), ancestors), descendants).distinct());
        System.out.println();
    }

    /**
     * Prints a full profile of the named Pokemon: its type(s), what each type is
     * strong against, and what each type is weak to.
     */
    public void listIndividualsStrengthsAndWeaknesses(String individualName) {
        printPokemonProfile(ind(NS_POKEMON, individualName));
    }

    /**
     * Prints all Pokemon species that can learn the given move.
     * Move individuals live in the Ontology namespace.
     */
    public void listPokemonByMove(String moveName) {
        System.out.println("=== Pokemon that can learn " + moveName + " ===");
        printIndented(streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonSpecies,
            factory.getOWLObjectSomeValuesFrom(canLearnMove,
                factory.getOWLObjectOneOf(ind(NS_ONTOLOGY, moveName))))));
        System.out.println();
    }

    /**
     * Prints all Pokemon of two given type individuals (dual-type search).
     * Both type names should be in the Pokemon namespace (e.g., "FireType", "FlyingType").
     */
    public void listPokemonByTwoTypes(String type1, String type2) {
        System.out.println("=== Pokemon with types " + type1 + " and " + type2 + " ===");
        printIndented(streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonSpecies,
            factory.getOWLObjectSomeValuesFrom(hasType, factory.getOWLObjectOneOf(ind(NS_POKEMON, type1))),
            factory.getOWLObjectSomeValuesFrom(hasType, factory.getOWLObjectOneOf(ind(NS_POKEMON, type2))))));
        System.out.println();
    }

//-------------------------------------------------------------------------------------------
//----------------------------------- Move search methods -----------------------------------
//-------------------------------------------------------------------------------------------

    /** Prints the type of the given move individual (move lives in the Ontology namespace). */
    public void listMoveInfo(String moveName) {
        System.out.println("=== Move: " + moveName + " ===");
        OWLNamedIndividual targetMove = ind(NS_ONTOLOGY, moveName);
        printObjectProperty(targetMove, hasMoveType, "Type");
        System.out.println();
    }

    /**
     * Prints all moves whose hasMoveType value matches the given type individual
     * (e.g., pass "FireType" to list all Fire moves).
     */
    public void listMovesByType(String typeName) {
        System.out.println("=== Moves of type " + typeName + " ===");
        printIndented(streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonMove,
            factory.getOWLObjectSomeValuesFrom(hasMoveType,
                factory.getOWLObjectOneOf(ind(NS_POKEMON, typeName))))));
        System.out.println();
    }

    /** Prints all moves that the named Pokemon can learn. */
    public void listMovesByPokemon(String pokemonName) {
        System.out.println("=== Moves " + pokemonName + " can learn ===");
        printIndented(getValues(ind(NS_POKEMON, pokemonName), canLearnMove).stream());
        System.out.println();
    }

//-------------------------------------------------------------------------------------------
//--------------------------------- Trainer search methods ----------------------------------
//-------------------------------------------------------------------------------------------

    /** Prints the Pokemon on the given trainer's team (trainer lives in the Ontology namespace). */
    public void listTrainerInfo(String trainerName) {

        System.out.println("=== Trainer: " + trainerName + " ===");

        OWLNamedIndividual trainer = ind(NS_ONTOLOGY, trainerName);

        printObjectProperty(trainer, hasPokemon, "Pokemon");

        System.out.println();
    }

    /**
     * Prints all trainers who have the given Pokemon on their team.
     * Scans all PokemonTrainer individuals for a hasPokemon match.
     */
    public void listTrainersWithPokemon(String pokemonName) {
        System.out.println("=== Trainers with " + pokemonName + " ===");
        printIndented(streamInstances(factory.getOWLObjectIntersectionOf(
            pokemonTrainer,
            factory.getOWLObjectSomeValuesFrom(hasPokemon,
                factory.getOWLObjectOneOf(ind(NS_POKEMON, pokemonName))))));
        System.out.println();
    }

    /**
     * Suggests Pokemon to use against the named trainer's team.
     * For each of the trainer's Pokemon, lists which player Pokemon are effective against it,
     * then lists all Pokemon to avoid across the whole team.
     */
    public void listPokemonSuggestionsForTrainer(String trainerName) {
        OWLNamedIndividual trainer = ind(NS_ONTOLOGY, trainerName);
        Set<OWLNamedIndividual> trainerPokemon = getValues(trainer, hasPokemon);

        System.out.println("=== Suggested Pokemon against " + trainerName + " ===");

        //Sort the trainer's Pokemon alphabetically.
        trainerPokemon.stream().sorted(Comparator.comparing(sfp::getShortForm))

            //Loops through each pokemon on the trainer's team.
            .forEach(enemyPokemon -> {
                // Get the enemy Pokemon's types.
                Set<OWLNamedIndividual> enemyTypes = getValues(enemyPokemon, hasType);

                // Get the current enemy Pokemon's type/s for header
                String typeStr = enemyTypes.stream()
                    .map(sfp::getShortForm).sorted()
                    .collect(Collectors.joining("/"));

                //Find all types that are strong against the enemy pokemon's type/s. 
                Set<OWLNamedIndividual> effectiveTypes = enemyTypes.stream()
                    .flatMap(type -> getValues(type, isWeakTo).stream())
                    .collect(Collectors.toSet());

                //Find Pokemon that have any of the types listed in the effectiveTypes set.
                String suggestions = streamInstances(pokemonSpecies)
                    .filter(ps -> getValues(ps, hasType).stream().anyMatch(effectiveTypes::contains))
                    .map(sfp::getShortForm).sorted()
                    .collect(Collectors.joining(", "));

                //Print the header for the current enemy Pokemon. 
                System.out.println("  vs. " + sfp.getShortForm(enemyPokemon) + " (" + typeStr + "):");
                //Print the suggested Pokemon to use against the current enemy Pokemon.
                System.out.println("    " + suggestions);

            });
        
        System.out.println("=== Pokemon to avoid against " + trainerName + " ===");

        //Sort the trainer's Pokemon alphabetically.
        trainerPokemon.stream().sorted(Comparator.comparing(sfp::getShortForm))

            // Loops through each pokemon on the trainer's team.
            .forEach(enemyPokemon -> {

                // Get the enemy Pokemon's types.
                Set<OWLNamedIndividual> enemyTypes = getValues(enemyPokemon, hasType);

                // Get the current enemy Pokemon's type/s for header
                String typeStr = enemyTypes.stream()
                    .map(sfp::getShortForm).sorted()
                    .collect(Collectors.joining("/"));

                //Find all types that are weak against the enemy pokemon's type/s.
                Set<OWLNamedIndividual> uneffectiveTypes = enemyTypes.stream()
                    .flatMap(type -> getValues(type, isStrongAgainst).stream())
                    .collect(Collectors.toSet());
                
                //Find Pokemon that have any of the types listed in the uneffectiveTypes set.
                String toAvoid = streamInstances(pokemonSpecies)
                    .filter(ps -> getValues(ps, hasType).stream().anyMatch(uneffectiveTypes::contains))
                    .map(sfp::getShortForm).sorted()
                    .collect(Collectors.joining(", "));

                //Print the header for the current enemy Pokemon.
                System.out.println("  vs. " + sfp.getShortForm(enemyPokemon) + " (" + typeStr + "):");
                System.out.println("    " + toAvoid);
            }
        );
            

    }

//-------------------------------------------------------------------------------------------
//----------------------------------- Type search methods -----------------------------------
//-------------------------------------------------------------------------------------------

    /**
     * Prints the strengths, weaknesses, and immunities of the given type individual
     * (e.g., pass "FireType" to see Fire's matchup chart).
     */
    public void listTypeInfo(String typeName) {

        System.out.println("=== Type: " + typeName + " ===");
        OWLNamedIndividual type = ind(NS_POKEMON, typeName);

        printObjectProperty(type, isStrongAgainst, "Strong against");
        printObjectProperty(type, isWeakTo, "Weak to");
        printObjectProperty(type, isImmuneTo, "Immune to");
        System.out.println();
    }

}
