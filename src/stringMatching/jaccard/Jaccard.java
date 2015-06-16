package stringMatching.jaccard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Jaccard 
{
	int gram = 3;
	float getSimilarity(String string1, String string2)
	{
		float matchValue = 0;
		ArrayList<String> string1Token = tokenize(string1,gram);
		ArrayList<String> string2Token = tokenize(string2, gram);
		
		int numberOfIntersections;
		int unionOfTokens;
		
		Set<String> string1TokenSet = new HashSet<String>();
		string1TokenSet.addAll(string1Token);
		int token1Size = string1TokenSet.size();
		
		Set<String> string2TokenSet = new HashSet<String>();
		string2TokenSet.addAll(string2Token);
		int token2Size = string2TokenSet.size();
		
		Set<String> allTokens = new HashSet<String>();
		allTokens.addAll(string1Token);
		allTokens.addAll(string2Token);
		int allTokenSize = allTokens.size();
		
		numberOfIntersections = token1Size + token2Size - allTokenSize;
		unionOfTokens = allTokenSize;
		
		matchValue = (float) numberOfIntersections/ (float) unionOfTokens;
		return matchValue;
	}
	private ArrayList<String> tokenize(String string, int gram)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		int stringLength = string.length();
		int count = 0;
		while(count < stringLength)
		{
			tokens.add(string.substring(count,count+gram));
			count++;
		}
		return tokens;
	}
}
