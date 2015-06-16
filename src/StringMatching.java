import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import stringMatching.jaccard.Jaccard;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Soundex;

public class StringMatching 
{
	//Change the homepath to point to d1.csv, d2.csv and groundTruth.csv. The other CSVs for name, address, city and type will be created in this path itself
	private static String homePath;
	
	//Hashmaps containing each string in groundtruth.csv and the corresponding truth values.
	private static Map<String, ArrayList<String>> namesMap = new HashMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>> addressMap = new HashMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>> cityMap = new HashMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>> typeMap = new HashMap<String, ArrayList<String>>();
	
	//Arraylist to store Levenshtein, Needleman-Wunch, Smith-Waterman, Jaro-Wrinkler, Soundex and Cosine Similarity results. This same arraylist is reused for various columns
	private static ArrayList<ArrayList<String>> allStringMatchingResult;
	static Accuracy accuracy;
	
	public static String getHomePath() 
	{
		return homePath;
	}

	public static void setHomePath(String homePath)
	{
		//Either the path read from commandline is accepted else the default one on my path is used
		if(homePath.equals(""))
			StringMatching.homePath = "C:\\Users\\Pramod P. Setlur\\Google Drive\\USC Subjects\\IIW\\Assignments\\HW9\\";
		else
		{
			StringBuilder fullPath = new StringBuilder(homePath);
			if(!homePath.endsWith("\\"))
				fullPath.append("\\\\");
			
			StringMatching.homePath = fullPath.toString();
			//System.out.println(homePath);
		}
			
	}

	public static void main(String[] args) 
	{	
		String inputHomePath = readHomePath();
		setHomePath(inputHomePath);
		
		String csvFile1 = getHomePath()+"d1.csv";
		String csvFile2 = getHomePath()+"d2.csv";
		
		//Reading the entire CSV FILE
		ArrayList<String[]> lines1 = getCSVData(csvFile1);
		ArrayList<String[]> lines2 = getCSVData(csvFile2);
		
		//Splitting the columns
		ArrayList<String> names1 = getColumnData(lines1,0);
		ArrayList<String> names2 = getColumnData(lines2,0);
		
		ArrayList<String> address1 = getColumnData(lines1,1);
		ArrayList<String> address2 = getColumnData(lines2,1);
		
		ArrayList<String> city1 = getColumnData(lines1,2);
		ArrayList<String> city2 = getColumnData(lines2,2);
		
		ArrayList<String> type1 = getColumnData(lines1,3);
		ArrayList<String> type2 = getColumnData(lines2,3);
		
		//Objects array
		Levenshtein levenshtein = new Levenshtein();
		NeedlemanWunch needlemanWunch  = new NeedlemanWunch();
		SmithWaterman smithWaterman = new SmithWaterman();
		JaroWinkler jaroWinkler = new JaroWinkler();
		Soundex soundex = new Soundex();
		CosineSimilarity cosineSimilarity = new CosineSimilarity();
		JaccardSimilarity jaccard = new JaccardSimilarity();
		
		//Constructor called to setup the hashmaps
		accuracy = new Accuracy(namesMap,addressMap,cityMap,typeMap);
		
		Object[] objects = {levenshtein,needlemanWunch,smithWaterman,jaroWinkler,soundex,cosineSimilarity,jaccard};
		
		//Processing the names - First Retrieving the 6 diff algo results, then print them into the CSV, then calculate the accuracy
		allStringMatchingResult = getAllStringMatchings(names1,names2,objects);
		printDataToFile(names1,allStringMatchingResult.get(0),allStringMatchingResult.get(1),allStringMatchingResult.get(2),allStringMatchingResult.get(3),allStringMatchingResult.get(4),allStringMatchingResult.get(5),allStringMatchingResult.get(6),homePath+"names.csv");
		calculateAccuracy(names1,allStringMatchingResult,namesMap,"Names");
		
		//Processing the address - First Retrieving the 6 diff algo results, then print them into the CSV, then calculate the accuracy
		allStringMatchingResult = getAllStringMatchings(address1,address2,objects);
		printDataToFile(address1,allStringMatchingResult.get(0),allStringMatchingResult.get(1),allStringMatchingResult.get(2),allStringMatchingResult.get(3),allStringMatchingResult.get(4),allStringMatchingResult.get(5),allStringMatchingResult.get(6),homePath+"address.csv");
		calculateAccuracy(address1,allStringMatchingResult,addressMap,"Address");

		//Processing the city - First Retrieving the 6 diff algo results, then print them into the CSV, then calculate the accuracy
		allStringMatchingResult = getAllStringMatchings(city1,city2,objects);
		printDataToFile(city1,allStringMatchingResult.get(0),allStringMatchingResult.get(1),allStringMatchingResult.get(2),allStringMatchingResult.get(3),allStringMatchingResult.get(4),allStringMatchingResult.get(5),allStringMatchingResult.get(6),homePath+"city.csv");
		calculateAccuracy(city1,allStringMatchingResult,cityMap,"Cities");

		//Processing the type - First Retrieving the 6 diff algo results, then print them into the CSV, then calculate the accuracy
		allStringMatchingResult = getAllStringMatchings(type1,type2,objects);
		printDataToFile(type1,allStringMatchingResult.get(0),allStringMatchingResult.get(1),allStringMatchingResult.get(2),allStringMatchingResult.get(3),allStringMatchingResult.get(4),allStringMatchingResult.get(5),allStringMatchingResult.get(6),homePath+"type.csv");
		calculateAccuracy(type1,allStringMatchingResult,typeMap,"Types");
	}

	private static String readHomePath()
	{
		System.out.print("Enter HomePath: ");
		String homePath = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try 
		{
			homePath = br.readLine();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return homePath;
	}

	//accuracy is calculated for each algorithm
	private static void calculateAccuracy(ArrayList<String> d1, ArrayList<ArrayList<String>> allStringMatchingResult, Map<String, ArrayList<String>> map, String columnName) 
	{
		System.out.println(columnName);
		for(int i = 0; i < allStringMatchingResult.size(); i++)
		{
			ArrayList<String> eachAlgoResults = allStringMatchingResult.get(i);
			float accuracyRecvd = accuracy.calculateAccuracyForEachAlgo(d1,eachAlgoResults,map);
			printAlgoName(i);
			System.out.println(accuracyRecvd);
		}
		System.out.println();
		
	}

	private static void printAlgoName(int i) 
	{
		switch(i)
		{
			case 0: System.out.print("Levenshtein: ");
					break;
			case 1: System.out.print("Needleman-Wunch: ");
					break; 
			case 2: System.out.print("Smith-Waterman: ");
					break;
			case 3: System.out.print("Jaro-Wrinkler: ");
					break;
			case 4: System.out.print("Soundex: ");
					break;
			case 5: System.out.print("TF-IDF: ");
					break;
		}
	}

	//This function returns string matched for every algorithm. it in turn calls getSimilarityScore to fetch the result for each algorithm
	private static ArrayList<ArrayList<String>> getAllStringMatchings(
			ArrayList<String> column1, ArrayList<String> column2, Object[] objects) 
	{
		ArrayList<ArrayList<String>> allStringMatchingResults = new ArrayList<ArrayList<String>>();
		for(Object object:objects)
		{
			ArrayList<String> stringMatching = getSimilarityScore(column1, column2, object);
			allStringMatchingResults.add(stringMatching);
		}
		return allStringMatchingResults;
	}

	//Prints to the file given the results of all the algos and the file name
	private static void printDataToFile(ArrayList<String> column,
			ArrayList<String> levenshtein,
			ArrayList<String> needlemanWunch,
			ArrayList<String> smithWaterman,
			ArrayList<String> jaroWinkler, 
			ArrayList<String> soundex, 
			ArrayList<String> cosineSimilarity, 
			ArrayList<String> jaccard, String fileName) 
	{
		File outputFile = new File(fileName);
		FileWriter fw;
		BufferedWriter bw = null;
		try 
		{
			fw = new FileWriter(outputFile);
			bw = new BufferedWriter(fw);
			bw.write("d1,LAVENSHTEIN,NEEDLEMAN-WUNCH,SMITH-WATERMAN,JARO-WRINKLER,SOUNDEX,TF-IDF,JACCARD\n");
			for(int length = 0;length<column.size();length++)
			{
				bw.write(column.get(length)+",");
				bw.write(levenshtein.get(length)+",");
				bw.write(needlemanWunch.get(length)+",");
				bw.write(smithWaterman.get(length)+",");
				bw.write(jaroWinkler.get(length)+",");
				bw.write(soundex.get(length)+",");
				bw.write(cosineSimilarity.get(length)+"\n");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(null!=bw)
			{
				try 
				{
					bw.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
					
		}
	}

	//Returns the similarity score for each algo. Reflection is used and is possible because all the algorithms have a getSimilarity method to be called which return the similarity score
	private static ArrayList<String> getSimilarityScore(ArrayList<String>column1,ArrayList<String>column2,Object object) 
	{
		ArrayList<String> similarStrings = new ArrayList<String>();
		final Class<?> clazz = object.getClass();
		try 
		{
			final Method getSimilarity = clazz.getMethod("getSimilarity",String.class,String.class);
			for(String string1:column1)
			{
				String targetString = new String("NULL");
				float maxValue = 0;
				for(String string2:column2)
				{
					Float matchedSimilarityValue = (Float)getSimilarity.invoke(object,string1, string2);
					if(matchedSimilarityValue>maxValue)
					{
						maxValue = matchedSimilarityValue;
						targetString = string2;
					}
				}
				similarStrings.add(targetString);				
			}
		} 
		catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			e.printStackTrace();
		}
		return similarStrings;
	}

	//Separates the column from the string[]. the column number passed is taken into account
	private static ArrayList<String> getColumnData(ArrayList<String[]> lines,int columnNumber) 
	{
		ArrayList<String> column = new ArrayList<String>();
		for(String[] stringArray:lines)
		{
			column.add(stringArray[columnNumber]);
		} 
		return column;
	}

	//To read the entire CSV File
	static ArrayList<String[]> getCSVData(String csvFile)
	{
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		ArrayList<String[]> csvLines = new ArrayList<String[]>();
		try
		{
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); //To read the header which we dont need in the arraylist
			while ((line = br.readLine()) != null) 
			{
				String[] csvLine = line.split(cvsSplitBy);
				csvLines.add(csvLine);
			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) 
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return csvLines;
	}
}
