import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {

	public static void main(String[] args) throws Exception {
		String indexPath = "index";
		String docsPath = "docs";
		
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter writer = new IndexWriter(dir, iwc);
		
		Files.walk(Paths.get(docsPath)).forEach(filePath -> {
			File file = new File(filePath.toString());
			
			if (Files.isRegularFile(filePath)) {
				Document doc = new Document();
				String content;
				try {
					content = FileUtils.readFileToString(file, "utf-8");
					doc.add(new TextField("content", content, Store.YES));
					writer.addDocument(doc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		writer.close();

		IndexReader reader = DirectoryReader
				.open(FSDirectory.open(new File(indexPath).toPath()));
		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setFieldNames(new String[]{"content"});
		mlt.setAnalyzer(analyzer);
		
		String[] interesting1 = mlt.retrieveInterestingTerms(5);
		String[] interesting2 = mlt.retrieveInterestingTerms(25);
		String[] interesting3 = mlt.retrieveInterestingTerms(38);
		
		Query query = mlt.like(3);

		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs topDocs = searcher.search(query, 10);
	}

}
