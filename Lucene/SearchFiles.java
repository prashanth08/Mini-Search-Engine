import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchFiles {

	private SearchFiles() {}

	/** This function is only for test search. */
	public static List<String> searchQuery(String indexDir, String queryString, int numResults, CharArraySet stopwords) {
		String field = "contents";
		List<String> hitPaths = new ArrayList<String>();

		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
			IndexSearcher searcher = new IndexSearcher(reader);
//			Analyzer analyzer = new MyAnalyzer(Version.LUCENE_44, stopwords);

//			QueryParser parser = new QueryParser(Version.LUCENE_44, field, analyzer);
			Analyzer analyzer = new MyAnalyzer(stopwords);

			QueryParser parser = new QueryParser(field, analyzer);
			Query query;
			query = parser.parse(QueryParser.escape(queryString));

			TopDocs results = searcher.search(query, null, numResults);
			
			for (ScoreDoc hit : results.scoreDocs) {
				String path = searcher.doc(hit.doc).get("path");
				hitPaths.add(path.substring(0, path.length()-4)); // chop off the file extension (".txt")
			}
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		} catch (ParseException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}

		return hitPaths;
	}
}
