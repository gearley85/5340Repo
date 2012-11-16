/**
 * A tag consists of an id, noun phrase, and reference. The reference refers to the id of the noun 
 * phrases antecedent.
 */
package coreference;

/** 
 * @author Gavin Earley & Adam Hartvigsen
 */
public class Tag 
{
	private String id;
	private String np;
	private String ref;
	
	/**
	 * Constructor sets the values for id, np, and ref. ref is initially set to null.
	 * 
	 * @param lId The coreference id 
	 * @param lNp The coreference noun phrase
	 */
	public Tag(String lId, String lNp)
	{
		id = lId;
		np = lNp;
		ref = null;
	}
	
	/**
	 * Returns the id string
	 * 
	 * @return id 
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Returns the np string
	 * 
	 * @return np
	 */
	public String getNp()
	{
		return np;
	}
	
	/**
	 * Returns the ref string
	 * 
	 *  @return ref
	 */
	public String getRef()
	{
		return ref;
	}
	
	/**
	 * Sets the ref string to a new string
	 * 
	 * @param lRef new reference
	 */
	public void setRef(String lRef)
	{
		 ref = lRef;
	}
	
	/**
	 * Prints the corefs to a string to take them out of the file
	 * @return
	 */
	public String tagPrinter()
	{
		return "<COREF ID=\""+id+"\">"+np+"</COREF>";
	}
	
	/**
	 *  Overwritten ToString method
	 */
	@Override
	public String toString()
	{
		if(ref == null)
		{
			return "<COREF ID=\""+id+"\">"+np+"</COREF>"+"\n";
		}
		else
		{
			return "<COREF ID=\""+id+"\" REF=\""+ref+"\">"+np+"</COREF>"+"\n";
		}
	}
}
