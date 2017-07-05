package MySpringMvc.PdfConverterBoot.AddLabelForFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.tika.exception.TikaException;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

public class MetaDataStamper {

	public static void main(String[] args)
			throws IOException, OpenXML4JException, XmlException, SAXException, TikaException, DocumentException {

		
		ExecutorService parallelLabelExecutor = Executors.newFixedThreadPool(1);

		String textFilePath = args[0];

		File file = new File(textFilePath);

		List<String> lines = FileUtils.readLines(file, "UTF-8");

		System.out.println("Reading file using Buffered Reader");
		System.out.println("nof lines or files present in text file is    " + lines.size());

		for (String line : lines) {

			Runnable threadLabeller = new ParallelMetaDataLabeller(line);
			
			parallelLabelExecutor.execute(threadLabeller);
			
	
	}
		
		
		parallelLabelExecutor.shutdown();

		try {
			parallelLabelExecutor.awaitTermination(90000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished labelling ,please check logs for details");
	}
}
