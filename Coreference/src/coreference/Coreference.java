package coreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	//No generics
		List currCoRefs = new ArrayList<Tag>();
		static Document dom;
	
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
	
	private static void parseXmlFile(String fileName){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fileName);
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
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
			
			for(int i=0; i<currentFileArray.length; i++)
			{
				parseXmlFile(currentFileArray[i]);
			}
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
