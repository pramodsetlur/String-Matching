import java.util.ArrayList;
import java.util.Map;


public class Accuracy 
{
	//Constructor to create the HashMaps
	Accuracy(Map<String, ArrayList<String>> namesMap,Map<String, ArrayList<String>> addressMap,Map<String, ArrayList<String>> cityMap,Map<String, ArrayList<String>> typeMap) 
	{
		ArrayList<String[]> groundTruth = StringMatching.getCSVData(StringMatching.getHomePath()+"groundtruth.csv");
		
		for(int i = 0;i<groundTruth.size()-1;i++)
		{
			String[] d1Line = groundTruth.get(i);
			String[] trueLine = groundTruth.get(i+1);
			
			String classD1Line = d1Line[d1Line.length-1];
			String classTrueLine = trueLine[trueLine.length-1];
			
			//Only if the current line and the following line matches then it needs to be hashed.
			if(classD1Line.equals(classTrueLine))
			{
				String nameD1Line = d1Line[0];
				String nameTrueLine = trueLine[0];
				addToHashMap(nameD1Line,nameTrueLine,namesMap);
				
				String addressD1Line = d1Line[1];
				String addressTrueLine = trueLine[1];
				addToHashMap(addressD1Line,addressTrueLine,addressMap);
				
				String cityD1Line = d1Line[2];
				String cityTrueLine = trueLine[2];
				addToHashMap(cityD1Line,cityTrueLine,cityMap);
				
				String typeD1Line = d1Line[3];
				String typeTrueLine = trueLine[3];
				addToHashMap(typeD1Line,typeTrueLine,typeMap);
			}
		}
	}
	
	//Adding data to the map
	private void addToHashMap(String d1String, String trueString,Map<String, ArrayList<String>> map) 
	{
		//if the map already contains the key then just update the value by adding the trueString to the arraylist of values
		if(map.containsKey(d1String))
		{
			boolean isPresent = false;
			ArrayList<String> values = map.get(d1String);
			for(String value:values)
			{
				if(value.equals(trueString))
				{
					isPresent = true;
					break;
				}
			}
			if(!isPresent)
			{
				values.add(trueString);
				map.put(d1String, values);
			}
		}
		else
		{
			ArrayList<String> values = new ArrayList<String>();
			values.add(trueString);
			map.put(d1String, values);
		}
	}

	/*
	 * For every string in the d1 and an algorithm's prediction of the answer needs to be compared with the hashed map to check for an accuracy. 
	 * A counter is maintained to find out the actual matches and it is finally divided by the number of rows in d1 as the accuracy
	 */
	public float calculateAccuracyForEachAlgo(ArrayList<String> d1,ArrayList<String> eachAlgoResults, Map<String, ArrayList<String>> map) 
	{
		int accuracy = 0;
		for(int counter = 0; counter < d1.size(); counter++)
		{
			String d1String = d1.get(counter);
			String matchedString = eachAlgoResults.get(counter);
			
			if(map.containsKey(d1String))
			{
				ArrayList<String> trueResults = map.get(d1String);
				for(String possibleResult:trueResults)
				{
					if(matchedString.equals(possibleResult) || matchedString.equals(d1String))
						accuracy++;
				}
			}
		}
		return accuracy/(float)d1.size();
	}
}
