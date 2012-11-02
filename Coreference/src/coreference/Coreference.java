package coreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Final Project
 * 
 * @author Gavin Earley & Adam Hartvigsen
 */
public class Coreference 
{
	//Global Variables
	private static String directory;
	private static ArrayList<Tag> currCoRefs;
	private static Document dom;
	public static String fileID;
	
	/**
	 * Main program function
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception 
	{
		//grab cmd line arguement
		if(args.length !=2 ) 
		{
			System.err.println("Not enough files, exactly 2 required");
			System.exit(1);
		}

		String inputList = args[0]; //input list file
		directory = args[1]; //directory for output files

		try
		{
			//read in input file to get files to process
			ReadFile listFile = new ReadFile(inputList);
			String[] listArray = listFile.OpenFile();
			
			//loop through our list and process each file
			for(int i=0; i<listArray.length; i++)
			{
				String temp = listArray[i];
				String fileNum = temp.substring(0, (temp.indexOf('.')));
				processFile(listArray[i],fileNum);
			}
			
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Parses the xml file and creates a DOM object model
	 * @param fileName
	 */
	private static void parseXmlFile(String fileName)
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try 
		{	
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(fileName);
			
		}
		catch(ParserConfigurationException pce) 
		{
			pce.printStackTrace();
		}
		catch(SAXException se) 
		{
			se.printStackTrace();
		}
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Parses the created DOM object and creates Tag objects
	 * Adds the Tag objecst to our currCoRefs list
	 */
	private static void parseDocument()
	{
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <COREF> elements
		NodeList nl = docEle.getElementsByTagName("COREF");
		if(nl != null && nl.getLength() > 0) 
		{
			for(int i = 0 ; i < nl.getLength();i++) 
			{	
				//get the Coref element
				Element el = (Element)nl.item(i);
				
				//get the Tag object
				Tag tempTag = getTag(el);
				
				//add it to list
				currCoRefs.add(tempTag);
			}
		}
	}
	
	/**
	 * Creates and returns a TAG element
	 * @param coEl
	 * @return
	 */
	private static Tag getTag(Element coEl)
	{
		String stringID = coEl.getAttribute("ID");
		
		String phrase = getNP(coEl);
		Tag tag = new Tag(stringID,phrase);
		return tag;
	}
	
	/**
	 * Grabs the Noun phrase of the given Coref tag
	 * @param coEl
	 * @return
	 */
	private static String getNP(Element coEl)
	{
		return coEl.getTextContent();	
	}
	
	/**
	 * Process the xml in the given file
	 * @param file
	 * @throws Exception 
	 */
	public static void processFile(String file, String fileNum) throws Exception
	{
		//reset currCoRefs for each file
		currCoRefs = new ArrayList<Tag>();
		
		//process Xml
		parseXmlFile(file);
		
		//parse the xml out of that file
		//and put into currCoRefs list
		parseDocument();
		
		//Do String matching
		stringMatcher(file);
		//Run POS tagging
		posTagger();
		
		//Print out our output to a file
		printOutput(currCoRefs,fileNum);	
	}
	
	
	/**
	 * Do string matching for the given corefs
	 * 
	 * @param file
	 */
	private static void stringMatcher(String file)
	{
		//loop through file line by line and see what lines up on the corefs
	}
	
	/**
	 * Tag the parts of speech to see if we can 
	 * do some additional coreferencing
	 */
	private static void posTagger()
	{
		
	}
	
	/**
	 * Process the output for each file
	 * @param tag
	 * @param fileID
	 * @throws Exception
	 */
	public static void printOutput(ArrayList<Tag> list, String fileID) throws Exception
	{	
		PrintWriter pw = new PrintWriter(new FileWriter(directory+fileID+".response"));
	   
		for(Tag t : list)
		{
			pw.print(t.toString());
		}
	     
	    pw.println();
	    pw.close(); 
	}
}
