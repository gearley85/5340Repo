Gavin Earley and Adam Hartvigsen's Final Project Final README

How to Compile/Run my code:
Untar the Coreference file.
First navigate to my Coreference/src directory. Make sure that the lib folder is in the 
src folder. Then run the following commands to compile and run my code.

COMPILE: javac -cp '.:lib/stanford-ner.jar' coreference/Coreference.java 

RUN: java -cp '.:lib/stanford-ner.jar' coreference/Coreference <input.txt> <output path>

Cade Machines primarily ran on:
lab1-13.eng.utah.edu
lab1-6.eng.utah.edu

Known bugs/Limitations:
We mainly focused on the existing corefs. We added in the ability to create additional corefs from the existing noun phrases that were outside of the coref tags withing the documents. We also were able to add in some NER but did not get very complex on the details. We basically used it to do some additional coreferencing within the existing (and newly created) corefs.
