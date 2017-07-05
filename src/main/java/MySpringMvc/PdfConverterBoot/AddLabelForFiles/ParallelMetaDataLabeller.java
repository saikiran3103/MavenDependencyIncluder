package MySpringMvc.PdfConverterBoot.AddLabelForFiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.xml.xmp.XmpWriter;

public class ParallelMetaDataLabeller implements Runnable {

	
	String lineReadFromTextFile;
	
	 public ParallelMetaDataLabeller(String lineReadFromTextFile){
	        this.lineReadFromTextFile=lineReadFromTextFile;
	     
	    }
	
	@Override
	public void run() {


		
		System.out.println("lineReadFromTextFile   "+lineReadFromTextFile);
		String[] splitted = lineReadFromTextFile.split("#label:");
		String filePath = splitted[0];
		System.out.println("filepath   " + filePath);
		String fileLabel = splitted[1];
		System.out.println("labelread   " + fileLabel);
		try {

			

			File officefile = new File(filePath);

			if (officefile.exists()) {

				System.out.println("Working on file " + officefile.getName());
				String name = officefile.getName();

				String labeledFilePath = officefile.getAbsolutePath();

				if (officefile.getName().endsWith(".pdf") || officefile.getName().endsWith(".PDF")) {

					System.out.println("inside pdf");

					FileInputStream fileInputStream = new FileInputStream(officefile);
					PdfReader reader = new PdfReader(fileInputStream);

					PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(labeledFilePath));

					// get and edit meta-data
					HashMap<String, String> info = reader.getInfo();

					info.put("Comments", fileLabel);

					// add updated meta-data to pdf
					stamper.setMoreInfo(info);

					// update xmp meta-data
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					XmpWriter xmp = new XmpWriter(baos, info);
					xmp.close();
					stamper.setXmpMetadata(baos.toByteArray());
					stamper.close();
					baos.close();
					System.out.println("added label for " + name);
				} else if (officefile.getName().endsWith(".docx") || officefile.getName().endsWith(".DOCX")) {

					System.out.println("inside docx");
					FileInputStream fileInputStream = new FileInputStream(officefile);

					XWPFDocument xWPFDocument = new XWPFDocument(fileInputStream);

					POIXMLProperties propsForDoc = xWPFDocument.getProperties();

					propsForDoc.getCoreProperties().setDescription(fileLabel);

					propsForDoc.commit();

					FileOutputStream fileOutputStreamForLabeledfile = new FileOutputStream(labeledFilePath);
					xWPFDocument.write(fileOutputStreamForLabeledfile);

					fileOutputStreamForLabeledfile.close();
					fileInputStream.close();
					System.out.println("added label for " + name);

				}

				else if (officefile.getName().endsWith(".xlsx") || officefile.getName().endsWith(".XLSX")) {

					System.out.println("inside xlsx");

					FileInputStream fileInputStream = new FileInputStream(officefile);

					XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

					POIXMLProperties poixmlPropertiesForXlsx = workbook.getProperties();

					poixmlPropertiesForXlsx.getCoreProperties().setDescription(fileLabel);

					poixmlPropertiesForXlsx.commit();
					FileOutputStream fileOutputStreamForLabeledfile = new FileOutputStream(labeledFilePath);
					workbook.write(fileOutputStreamForLabeledfile);
					fileOutputStreamForLabeledfile.close();
					fileInputStream.close();
					System.out.println("added label for " + name);
				}

				else if (officefile.getName().endsWith(".PPTX") || officefile.getName().endsWith(".pptx")) {

					System.out.println("inside PPTX");

					FileInputStream fileInputStream = new FileInputStream(officefile);

					XMLSlideShow ppt = new XMLSlideShow(fileInputStream);

					POIXMLProperties pptxFileProps = ppt.getProperties();
					pptxFileProps.getCoreProperties().setDescription(fileLabel);

					pptxFileProps.commit();

					FileOutputStream fileOutputStreamForLabeledfile = new FileOutputStream(labeledFilePath);
					ppt.write(fileOutputStreamForLabeledfile);

					fileOutputStreamForLabeledfile.close();
					fileInputStream.close();
					System.out.println("added label for " + name);
				}

				else if (officefile.getName().endsWith(".ppt") || officefile.getName().endsWith(".PPT")
						|| (officefile.getName().endsWith(".xls") || officefile.getName().endsWith(".XLS"))
						|| (officefile.getName().endsWith(".doc") || officefile.getName().endsWith(".DOC"))) {

					System.out.println("inside doc,xls,ppt");

					FileInputStream fileInputStream = new FileInputStream(officefile);

					NPOIFSFileSystem fs = new NPOIFSFileSystem(fileInputStream);

					HPSFPropertiesOnlyDocument doc = new HPSFPropertiesOnlyDocument(fs);

					SummaryInformation si = doc.getSummaryInformation();
					if (si == null)
						doc.createInformationProperties();

					si.setComments(fileLabel);

					FileOutputStream fileOutputStreamForLabeledfile = new FileOutputStream(labeledFilePath);
					doc.write(fileOutputStreamForLabeledfile);
					fileOutputStreamForLabeledfile.close();
					fileInputStream.close();
					System.out.println("added label for " + name);
				} else {
					System.err.println("Not a office file hence skipping");
				}
			} else {
				System.err.println("File doesnot exist " + officefile);
			}
		} catch (Exception exceptionProcess) {
			System.err.println("error occured " + exceptionProcess.getMessage());
		}
	
		
	}

	
	
	
	
	
	
	
	
	
}
