package coreference;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class ReadFile {
	
	private String path;
	
	public ReadFile(String file_path)
	{
		this.path=file_path;
	}

	public String[] OpenFile() throws IOException
	{
		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		int numbLines =readLines();
		String[] textData = new String[numbLines];
		
		int i;

		for(i=0; i<numbLines; i++)
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
	
	public int readLines() throws IOException
	{
		FileReader file_to_read = new FileReader(path);
		BufferedReader bf = new BufferedReader(file_to_read);
		
		String aLine;
		int numbLines=0;
		
		while ((aLine=bf.readLine())!=null)
		{
			numbLines++;

		}
		
		bf.close();
		
		return numbLines;
	}
	
}

