/*
 * A tag consists of an id, noun phrase, and reference. The reference refers to the id of the noun 
 * phrases antecedent.
 */
package coreference;

public class Tag {
	private String id;
	private String np;
	private String ref;
	
	/*
	 * Constructor sets the values for id, np, and ref. ref is initially set to null.
	 * 
	 * @param lId, lNp The coreferences id and noun phrase,
	 */
	public Tag(String lId, String lNp){
		id = lId;
		np = lNp;
		ref = null;
	}
	
	/*
	 * Returns the id string
	 * 
	 * @return id 
	 */
	public String getId(){
		return id;
	}
	
	/*
	 * Returns the np string
	 * 
	 * @return np
	 */
	public String getNp(){
		return np;
	}
	
	/*
	 * Returns the ref string
	 * 
	 *  @return ref
	 */
	public String getRef(){
		return ref;
	}
	
	/*
	 * Sets the ref string to a new string
	 * 
	 * @param lRef new reference
	 */
	public void setRef(String lRef){
		 ref = lRef;
	 }
}
