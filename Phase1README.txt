Gavin Earley and Adam Hartvigsen's Final Project

How to Compile/Run my code:
Untar the Coreference file.
First navigate to my Coreference/src directory. Make sure that the lib folder is in the 
src folder. Then run the following commands to compile and run my code.

COMPILE: javac -cp '.:lib/stanford-ner.jar' coreference/Coreference.java 

RUN: java -cp '.:lib/stanford-ner.jar' coreference/Coreference <input.txt> <output path>

Cade Machine primarily ran on:
lab1-13.eng.utah.edu


Known bugs/Limitations:
We were able to find a good XML parser and started working on the string matching. We are still looking in to other methods that we want to work out. So far it takes in the files correctly and the response files are labeled and formatted correctly. We will continue working on other strategies to get the project to be more robust and correctly identifying corefs for the given files.
