import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DirectIndex {

    private HashMap<String,HashMap<String,Integer>> directIndexMap;
    DirectIndex() {
        directIndexMap = new HashMap<String, HashMap<String, Integer>>();
    }

    public HashMap<String, HashMap<String, Integer>> getDirectIndexMap() {
        return directIndexMap;
    }

    public void buildIndex(String indexPath, String docsPath, String stopWordsFile) throws Exception{
        if (docsPath == null || docsPath.isEmpty()) {
            System.err.println("Document directory cannot be null");
            System.exit(1);
        }
        // Check whether the directory is readable
        final File docDir = new File(docsPath);
        if (!docDir.exists() || !docDir.canRead()) {
            System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        CharArraySet stopwords = readStopFile(stopWordsFile);
        //TreeMap<String,HashMap<String,Integer>> directIndexMap = new TreeMap<String, HashMap<String, Integer>>();

        for(File fileEntry : docDir.listFiles()) {
           // BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
            StandardTokenizer src = new StandardTokenizer(new FileReader(fileEntry));
            TokenStream tok = new StandardFilter(src);
            tok = new LowerCaseFilter(tok);
            tok = new StopFilter(tok, stopwords);
            tok = new PorterStemFilter(tok);

            //Now iterate through the token and print the tokens.
            CharTermAttribute charTermAttribute = tok.addAttribute(CharTermAttribute.class);
            tok.reset();
            HashMap<String,Integer> innerMap = new HashMap<String, Integer>();
            String documentName = getDocumentName(fileEntry.getName());

            while(tok.incrementToken()) {
                String term = charTermAttribute.toString();
                if(!innerMap.containsKey(term)) {
                    innerMap.put(term,1);
                } else {
                    int termCount = innerMap.get(term);
                    innerMap.put(term,termCount+1);
                }
            }
            tok.close();
            directIndexMap.put(documentName,innerMap);

        }
        dumpIndexToFile(indexPath, directIndexMap);
    }

    private void dumpIndexToFile(String indexFilePath, HashMap<String, HashMap<String, Integer>> directIndexMap) throws IOException {
        //dump the direct index
        FileWriter writer = new FileWriter(indexFilePath+"/index_dump.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        Set<String> docNames = directIndexMap.keySet();
        for(String docName : docNames) {
            bufferedWriter.write(docName);
            bufferedWriter.write("\t");
            bufferedWriter.write(directIndexMap.get(docName)+"");
            bufferedWriter.write("\r\n");
        }
        bufferedWriter.close();
        writer.close();
    }

    private String getDocumentName(String name) {
        name = name.replace(".txt","");
        return name;
    }

    public CharArraySet readStopFile(String filename) {
        CharArraySet stopWordsSet;
        ArrayList<String> stopWords = new ArrayList<String>();
        BufferedReader in;
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
}
