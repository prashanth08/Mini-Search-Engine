import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;


public class MyAnalyzer extends StopwordAnalyzerBase {

	/** Maximum allowed token length */
	public static final int DEFAULT_MAX_TOKEN_LENGTH = 10000;

	private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;


	/** Builds an analyzer with the given stop words.
	 * @param matchVersion Lucene version to match See {@link
	 * <a href="#version">above</a>}
	 * @param stopWords stop words */
// 	public MyAnalyzer(Version matchVersion, CharArraySet stopWords) {
//		super(matchVersion, stopWords);
//	}
	public MyAnalyzer(CharArraySet stopWords) {
		super(stopWords);
	}

	/**
	 * Set maximum allowed token length.  If a token is seen
	 * that exceeds this length then it is discarded.  This
	 * setting only takes effect the next time tokenStream or
	 * tokenStream is called.
	 */
	public void setMaxTokenLength(int length) {
		maxTokenLength = length;
	}

	/**
	 * @see #setMaxTokenLength
	 */
	public int getMaxTokenLength() {
		return maxTokenLength;
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
//		final StandardTokenizer src = new StandardTokenizer(matchVersion, reader);
//		src.setMaxTokenLength(maxTokenLength);
//		TokenStream tok = new StandardFilter(matchVersion, src);
//		tok = new LowerCaseFilter(matchVersion, tok);
//
//		// Add additional filters here 
//		tok = new StopFilter(matchVersion, tok, stopwords);
		final StandardTokenizer src = new StandardTokenizer(reader);
		src.setMaxTokenLength(maxTokenLength);
		TokenStream tok = new StandardFilter(src);
		tok = new LowerCaseFilter(tok);

		// Add additional filters here 
		tok = new StopFilter(tok, stopwords);

		return new TokenStreamComponents(src, tok) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				src.setMaxTokenLength(MyAnalyzer.this.maxTokenLength);
				super.setReader(reader);
			}
		};
	}
}
