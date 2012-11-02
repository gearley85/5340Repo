package coreference;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

/** 
 * @author Gavin Earley & Adam Hartvigsen
 */
public class ReadFile {
	//Global variable
	private String path;
	
	/**
	 * Constructor Sets global variable path to file_path
	 * 
	 * @param file_path name of the file to read in.
	 */
	public ReadFile(String file_path)
	{
		this.path=file_path;
	}
	
	/*
	 * Returns a String array where each element in the array element contains a line from the file
	 * whose name is contained in path.
	 * 
	 * @return textData String array containing the lines of path file
	 */
	public String[] OpenFile() throws IOException
	{
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		int numbLines = readLines();
		String[] textData = new String[numbLines];
		
		for(int i=0; i<numbLines; i++)
		{
			String temp=textReader.readLine();
			if(temp==null)
			{
				//continue;
			}
			else
			{
				if(!temp.isEmpty()&&!(temp.trim().equals(""))&&!(temp.trim().equals("\n"))&&temp!=null)
				{
					textData[i]=temp;
				}
			}
		}
		
		textReader.close();
		return textData;
	}
	
	/**
	 * Returns the number of lines contained in the path file.
	 * 
	 * @return numbLines the number of lines in path file
	 */
	public int readLines() throws IOException
	{
		FileReader file_to_read = new FileReader(path);
		BufferedReader bf = new BufferedReader(file_to_read);
		
		int numbLines=0;
		
		while ((bf.readLine())!=null)
		{
			numbLines++;
		}
		
		bf.close();
		return numbLines;
	}
}

