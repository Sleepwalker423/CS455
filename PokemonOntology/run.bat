@echo off
:: ---------------------------------------------------------------
:: run.bat
:: Run from the PokemonOntology\ directory
:: Optional: run.bat pokemon-ontology.rdf
:: ---------------------------------------------------------------
if "%~1"=="" (
    mvn exec:java
) else (
    mvn exec:java -Dexec.args="%~1"
)
