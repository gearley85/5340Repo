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
		if(args.length !=1 ) {
			  System.err.println("Not enough files, exactly 1 required");
			  System.exit(1);
			}

		int i=0;
		String test= args[0]; //test file

		try
		{
			//read in test file
			ReadFile testFile = new ReadFile(test);
			String[] arrayTest = testFile.OpenFile();
			
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
			
		}
		
	}

}
