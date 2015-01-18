import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.core.StopAnalyzer;
// import lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class EvaluateQueries {
	public static void main(String[] args) {
		String cacmDocsDir = "data/cacm"; // directory containing CACM documents
		String medDocsDir = "data/med"; // directory containing MED documents
		
		String cacmIndexDir = "data/index/cacm"; // the directory where index is written into
		String medIndexDir = "data/index/med"; // the directory where index is written into
		
		String cacmQueryFile = "data/cacm_processed.query";    // CACM query file
		String cacmAnswerFile = "data/cacm_processed.rel";   // CACM relevance judgements file

		String medQueryFile = "data/med_processed.query";    // MED query file
		String medAnswerFile = "data/med_processed.rel";   // MED relevance judgements file
		
		String stopWordsFile = "data/stopwords/stopwords_indri.txt";
		
		int cacmNumResults = 100;
		int medNumResults = 100;

	    CharArraySet stopwords = new CharArraySet(0, false);
	    
		System.out.println(evaluate(cacmIndexDir, cacmDocsDir, cacmQueryFile,
				cacmAnswerFile, cacmNumResults, stopwords));
		
		System.out.println("\n");
		
		System.out.println(evaluate(medIndexDir, medDocsDir, medQueryFile,
				medAnswerFile, medNumResults, stopwords));

	}
	
public static CharArraySet readStopFile(String filename) {
		CharArraySet stopWordsSet;
		ArrayList<String> stopWords = new ArrayList<String>();
		BufferedReader in=null;
		try {
			in = new BufferedReader(new FileReader(new File(filename))); // Reads the file line by line
			String line;
			
			while ((line = in.readLine()) != null) {
			stopWords.add(line);	
			}
			in.close();
			
		} catch(IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
		stopWordsSet = new CharArraySet(stopWords,false);
		return stopWordsSet;
}

	private static Map<Integer, String> loadQueries(String filename) {
		HashMap<Integer, String> queryIdMap = new HashMap<Integer, String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(
					new File(filename)));
		} catch (FileNotFoundException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}

		String line;
		try {
			while ((line = in.readLine()) != null) {
				int pos = line.indexOf(',');
				queryIdMap.put(Integer.parseInt(line.substring(0, pos)), line
						.substring(pos + 1));
			}
		} catch(IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch(IOException e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		}
		return queryIdMap;
	}

	private static Map<Integer, HashSet<String>> loadAnswers(String filename) {
		HashMap<Integer, HashSet<String>> queryAnswerMap = new HashMap<Integer, HashSet<String>>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(
					new File(filename)));

			String line;
			while ((line = in.readLine()) != null) {
				String[] parts = line.split(" ");
				HashSet<String> answers = new HashSet<String>();
				for (int i = 1; i < parts.length; i++) {
					answers.add(parts[i]);
				}
				queryAnswerMap.put(Integer.parseInt(parts[0]), answers);
			}
		} catch(IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch(IOException e) {
				System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
			}
		}
		return queryAnswerMap;
	}

	private static double precision(HashSet<String> answers,
			List<String> results) {
		double matches = 0;
		for (String result : results) {
			if (answers.contains(result))
				matches++;
		}

		return matches / results.size();
	}	
	
//NAN	
	private static double avg_precision(HashSet<String> answers,
			List<String> results) {
		int i = 1 ;
		double num_rel = 0, avg_sum = 0;
		for (String result : results) {
			if (answers.contains(result))
			{ 
				num_rel++ ;
				avg_sum+= num_rel/i ;
			}
			i++;
		}
		return avg_sum/answers.size();
	}	
//NAN	

	private static double evaluate(String indexDir, String docsDir,
			String queryFile, String answerFile, int numResults,
			CharArraySet stopwords) {

		// Build Index
		IndexFiles.buildIndex(indexDir, docsDir, stopwords);

		// load queries and answer
		Map<Integer, String> queries = loadQueries(queryFile);
		Map<Integer, HashSet<String>> queryAnswers = loadAnswers(answerFile);

		// Search and evaluate
		double sum = 0, avg_sum = 0;
		for (Integer i : queries.keySet()) {
			  //NAN if (i == 1) {
				List<String> results = SearchFiles.searchQuery(indexDir, queries
						.get(i), numResults, stopwords);
				sum += precision(queryAnswers.get(i), results);
				avg_sum += avg_precision(queryAnswers.get(i), results);
				//System.out.printf("\nTopic %d  ", i);
				//System.out.print (results);
				//System.out.println();
			//NAN }
			
		}
		System.out.print ("MAP:");
		System.out.println(avg_sum/queries.size());
		return sum / queries.size();
	}
}
