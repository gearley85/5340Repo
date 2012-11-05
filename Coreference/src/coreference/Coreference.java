package coreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private static ArrayList<String> npChunks;
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
				String fileNum = temp.substring(temp.lastIndexOf('/')+1 , (temp.indexOf('.')));
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
		Node categories = docEle.getElementsByTagName("COREF").item(0);
		NodeList categorieslist = categories.getChildNodes();
		while (categorieslist.getLength() > 0) {
		    Node node = categorieslist.item(0);
		    node.getParentNode().removeChild(node);
		}
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(docEle);
			transformer.transform(source, result);

			String xmlString = result.getWriter().toString();
			//System.out.println(xmlString);
		}
		catch(Exception e)
		{
			
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
		
		//String phrase = getNP(coEl);
		Tag tag = new Tag(stringID,coEl.getTextContent());
		return tag;
	}
	
	/**
	 * Grabs the Noun phrase of the given Coref tag
	 * @param coEl
	 * @return
	 */
	//private static String getNP(Element coEl)
	//{
		//return coEl.getTextContent();	
	//}
	
	/**
	 * Process the xml in the given file
	 * @param file
	 * @throws Exception 
	 */
	public static void processFile(String file, String fileNum) throws Exception
	{
		//reset currCoRefs for each file
		currCoRefs = new ArrayList<Tag>();
		npChunks = new ArrayList<String>();

		//process Xml
		parseXmlFile(file);
		
		//parse the xml out of that file
		//and put into currCoRefs list
		parseDocument();
		
		//NP chunking, throw into chunk arraylist
		chunker(file);
		
		//Do String matching
		stringMatcher();
	
		//Try to match Corefs
		coRefer();
		
		//Print out our output to a file
		printOutput(currCoRefs,fileNum);	
	}
	
	
	
	/**
	 * Divide the file into np chunks
	 * @param file
	 */
	private static void chunker(String file)
	{
		
	}
	
	
	
	/**
	 * Do string matching for the given corefs once we have the given chunks
	 * 
	 * @param file
	 */
	private static void stringMatcher()
	{
		String id="A";
		//loop through chunks and see what lines up on the corefs that were given
		for(String np :npChunks)
		{
			for(Tag t: currCoRefs)
			{
				//create new corefs if we have a match on np's not in corefs already
				if(np.contains(t.getNp()))
				{
					//increment our ids
					if(!(id.equals("A")))
					{
						id.replace(id.charAt(0), (char) (id.charAt(0)+1));
					}
					//add in the new coref Tag and associate existing with the ID for the new tag
					currCoRefs.add(new Tag(id,np));
					t.setRef(id);
				}
			}
		}
		
		
	}
	
	/**
	 * Begin to try and match coreferences
	 */
	private static void coRefer()
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
		pw.print("<TXT>");
	   
		for(Tag t : list)
		{
			pw.print(t.toString());
		}
		pw.print("</TXT>");
	     
	    pw.println();
	    pw.close(); 
	}
}
