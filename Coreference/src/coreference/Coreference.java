package coreference;

import java.io.IOException;

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
	public static void main(String[] args) {
		//grab cmd line arguement
		if(args.length !=2 ) {
			  System.err.println("Not enough files, exactly 2 required");
			  System.exit(1);
			}

		int i=0;
		String inputList= args[0]; //input list file
		String directory =args[1]; //directory for output files

		try
		{
			//read in input file to get files to process
			ReadFile listFile = new ReadFile(inputList);
			String[] listArray = listFile.OpenFile();
			
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
			
		}
		
	}

}
