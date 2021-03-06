package coreference;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;


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
	private static ArrayList<String> nerList;
	private static Document dom;
	public static String fileID;
	private static String wholeFile;
	
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
				String fileNum = temp.substring(temp.lastIndexOf('/')+1 , (temp.lastIndexOf('.')));
				processFile(listArray[i],fileNum);
			}
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Process the xml in the given file
	 * @param file
	 * @throws Exception 
	 */
	public static void processFile(String file, String fileNum) throws Exception
	{
		//reset currCoRefs and other lists for each file
		currCoRefs = new ArrayList<Tag>();
		npChunks = new ArrayList<String>();
		nerList = new ArrayList<String>();

		//process Xml
		parseXmlFile(file);
		
		//parse the xml out of that file
		//and put into currCoRefs list
		parseDocument();
		
		//NP chunking, throw into chunk arraylist
		chunker(file);
		
		//Do String matching
		stringMatcher();
		
		//Run NER
		nerFunction(file);
	
		//Try to match Corefs
		coReferNER();
		
		//Print out our output to a file
		printOutput(currCoRefs,fileNum);	
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
		while (categorieslist.getLength() > 0) 
		{
		    Node node = categorieslist.item(0);
		    node.getParentNode().removeChild(node);
		}
	}
	
	/**
	 * Creates and returns a TAG element
	 * @param coEl
	 * @return tag
	 */
	private static Tag getTag(Element coEl)
	{
		String stringID = coEl.getAttribute("ID");
		Tag tag = new Tag(stringID,coEl.getTextContent());
		return tag;
	}
	
	/**
	 * Put the lines of the file into one large string, then remove the corefs
	 * @param file
	 */
	private static void chunker(String file)
	{
		try
		{
			//open the file
			ReadFile openFile = new ReadFile(file);
			String[] arrayLines = openFile.OpenFile();
			
			
			//throw all lines into one ginormous string
			StringBuilder builder = new StringBuilder();
			for (String st: arrayLines) 
			{
			    builder.append(st).append(' ');
			}
			
			wholeFile = builder.toString();
				
			//now loop through our coref list and remove them out of the giant string
			//and replaces it with id= and the corefs id use later for String matching
			for(Tag t: currCoRefs)
			{
				String tempTag= t.tagPrinter();
				wholeFile=wholeFile.replace(tempTag, "id="+t.getId());
			}
			wholeFile=wholeFile.replace("null", "");
			
			//at this point each word is pulled out as a chunk
			StringTokenizer parser = new StringTokenizer(wholeFile, " //");
			while(parser.hasMoreTokens())
			{
				String temp = parser.nextToken();
				npChunks.add(temp);
			}					
		}
		catch (IOException e )
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	
	/**
	 * Do string matching for the given corefs once we have the given chunks
	 */
	private static void stringMatcher()
	{
		// Loop through the currCoRefs tag list 
		for(int i = 0; i < currCoRefs.size(); i++)
		{
			Tag tempTag = currCoRefs.get(i); // Grab current tag
			if(i == 0){} // do nothing for the first element
			else
			{
				ArrayList<String> currList = tempTag.getNPList(); //Get current tag noun phrase list
				ArrayList<String> prevList;
				//Loop through previous coref tags from the tag just previous back to the first of the list
				for(int j = i-1; j > 0; j--)
				{
					Tag pig = currCoRefs.get(j); //grab the prev tag
					prevList = pig.getNPList(); //grab prev tag's npList
					
					// loop for the current tags np list
					for(int k = 0; k < currList.size(); k++)
					{
						// loop for the prev tags np list
						for(int g = 0; g < prevList.size(); g++)
						{
							// checks if the current word from each list is  equal and if the current
							// tags reference is null
							if(currList.get(k).equals(prevList.get(g)) && tempTag.getRef() == null)
							{
								// checks if the current word is not the, a, of, to, for
								if(!currList.get(k).toLowerCase().equals("the") && 
										!currList.get(k).toLowerCase().equals("a") && 
										!currList.get(k).toLowerCase().equals("of") &&
										!currList.get(k).toLowerCase().equals("to") &&
										!currList.get(k).toLowerCase().equals("for"))
								{
									tempTag.setRef(pig.getId()); // set current tags ref to prev tags id
								}
							}
						}
					}
				}
			}
		}
		
		String id="A"; //starting string for new tag ids
		
		// loop for the coref tags
		for(int i = 0; i < currCoRefs.size(); i++)
		{
			if(currCoRefs.get(i).getRef() == null) //check if current tags ref = null
			{
				// find the position to start searching for previous references
				String currTagId = "id=" + currCoRefs.get(i).getId();
				ArrayList<String> currNPList = currCoRefs.get(i).getNPList();
				int pos = 0;
				for (int j = 0; j < npChunks.size(); j++) {
					String temp = npChunks.get(j);
					if (currTagId.equals(temp)) {
						pos = j - 1;
					}
				}
				// string matching coref tags with all previous words
				for (int g = 0; g < currNPList.size(); g++) 
				{
					String tempCurrNP = currNPList.get(g);
					for (int k = pos; k >= 0; k--) 
					{
						if (tempCurrNP.equals(npChunks.get(k))) 
						{
							if(!tempCurrNP.toLowerCase().equals("the") &&
									!tempCurrNP.toLowerCase().equals("a") &&
									!tempCurrNP.toLowerCase().equals("of") &&
									!tempCurrNP.toLowerCase().equals("to") &&
									!tempCurrNP.toLowerCase().equals("for"))
							{
								currCoRefs.add(new Tag(id,npChunks.get(k))); //create new tag
								currCoRefs.get(i).setRef(id); //set currents tags ref
								id += "A"; // make new unique tag
							}
						}
					}
				}
			}
		}
	}
	
	public static void nerFunction(String file) throws IOException
	{
		String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
	    @SuppressWarnings("unchecked")
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
	    String fileContents = IOUtils.slurpFile(file);
	    List<List<CoreLabel>> out = classifier.classify(fileContents);
	    for (List<CoreLabel> sentence : out) 
	    {
	    	for (CoreLabel word : sentence) 
	    	{
	    		//throw this in a list to use later 
	            nerList.add(word.word()+'/'+word.getString(AnswerAnnotation.class));
	    	}
	    } 
	}
	
	/**
	 * Begin to try and match coreferences
	 */
	private static void coReferNER() {
		for (int i = 0; i < currCoRefs.size(); i++) {
			Tag tempTag = currCoRefs.get(i);

			if (i == 0) {
			} else {
				int cur = 0;
				int prev = 0;
				ArrayList<String> currList = tempTag.getNPList();
				ArrayList<String> prevList;
				for (int j = i - 1; j > 0; j--) {
					Tag pig = currCoRefs.get(j);
					prevList = pig.getNPList();
					for (int k = 0; k < currList.size(); k++) {
						for (int g = 0; g < prevList.size(); g++) {
							String curNerTemp = "";
							String prevNerTemp = "";

							// grab word place in NERList

							for (int y = 0; y < nerList.size(); y++) {
								if (nerList.get(y).contains(currList.get(k))) 
								{
									curNerTemp = nerList.get(y);
								}
								cur = nerList.indexOf(curNerTemp);
								
								if (nerList.get(y).contains(prevList.get(g))) 
								{
									prevNerTemp = nerList.get(y);
								}
								prev = nerList.indexOf(prevNerTemp);
							}

							String curClassType = "";
							String prevClassType = "";

							// grab current word's info
							if (cur >= 0) {
								String bigCurWord = nerList.get(cur);
								bigCurWord.substring(0, bigCurWord.indexOf("/"));
								curClassType = bigCurWord.substring(
										bigCurWord.indexOf("/")+1,
										bigCurWord.length());
							}

							// grab Prev word's info
							if (prev >= 0) {
								String bigPrevWord = nerList.get(prev);
								bigPrevWord.substring(0, bigPrevWord.indexOf("/"));
								prevClassType = bigPrevWord.substring(
										bigPrevWord.indexOf("/")+1,
										bigPrevWord.length());
							}
													
							if (curClassType.equals("PERSON")
									&& prevClassType.equals("PERSON")
									&& tempTag.getRef() == null) 
							{
								tempTag.setRef(pig.getId());
							}

							if (curClassType.equals("ORGANIZATION")
									&& prevClassType.equals("ORGANIZATION")
									&& tempTag.getRef() == null) 
							{
								tempTag.setRef(pig.getId());
							}
							if (curClassType.equals("LOCATION")
									&& prevClassType.equals("LOCATION")
									&& tempTag.getRef() == null) 
							{
								tempTag.setRef(pig.getId());
							}
						}
					}
				}
			}
		}

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
