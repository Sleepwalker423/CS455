@echo off
:: ---------------------------------------------------------------
:: compile.bat
:: Run from the PokemonOntology\ directory
:: ---------------------------------------------------------------

mvn compile

if %ERRORLEVEL% == 0 (echo Compilation successful.) else (echo Compilation failed.)
