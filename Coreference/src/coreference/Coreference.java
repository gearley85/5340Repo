package coreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * @author Gavin Earley & Adam Hartvigsen
 * Final Project
 *
 */
public class Coreference {

	/**
	 * @param args
	 */
	public static String directory;
	
	public static void main(String[] args) {
		//grab cmd line arguement
		if(args.length !=2 ) {
			  System.err.println("Not enough files, exactly 2 required");
			  System.exit(1);
			}


		String inputList= args[0]; //input list file
		 directory =args[1]; //directory for output files

		try
		{
			//read in input file to get files to process
			ReadFile listFile = new ReadFile(inputList);
			String[] listArray = listFile.OpenFile();
			
			//loop through our list and process each file
			for(int i=0; i<listArray.length; i++)
			{
				processFile(listArray[i]);
			}
			
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
			
		}
		
	}
	
	public static void processFile(String file)
	{
		//open the file and read each line
		try
		{
			//read each line of current file
			ReadFile currentFile = new ReadFile(file);
			String[] currentFileArray = currentFile.OpenFile();
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
			
		}
		
		//while looping through each line do what we need to do
	}
	
	public static void printOutput(Tag tag, int fileID) throws Exception{
		
		PrintWriter pw = new PrintWriter(new FileWriter(directory+fileID+".response"));
	   
	      pw.print("<COREF ID=");
	     
	    pw.println();
	    pw.close(); 
	}

}
