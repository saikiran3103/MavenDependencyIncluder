package MySpringMvc.PdfConverterBoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.OutputTarget;

public class PdfConverter {

	public static void main(String[] args) throws IOException {

		String os = System.getProperty("os.name").toLowerCase();

		long totalSizeOfFiles = 0L;

		String extractedPath = null;

		System.out.println("operating system of the client is  " + os);
		String folderPath = args[0];

		System.out.println("folderPath  " + folderPath);
		List<File> filesInFolder = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile).map(Path::toFile)
				.collect(Collectors.toList());
		int noOfFiles = 0;

		for (File officefile : filesInFolder) {

			if (officefile.getName().endsWith(".pdf") || officefile.getName().endsWith(".PDF")) {

				noOfFiles++;

				totalSizeOfFiles = totalSizeOfFiles + officefile.length();

				System.out.println("Extracting text for the file" + officefile.getName());

				int nameIndex = officefile.getName().lastIndexOf(".");

				String textNaming1 = officefile.getName().substring(0, nameIndex);

				String path = officefile.getAbsolutePath();
				String seperateOutsideFolder = null;
				String textdirectoryString = null;
				if (os.contains("windows")) {

					seperateOutsideFolder = path.substring(0, path.lastIndexOf("\\"));

				}

				else {
					seperateOutsideFolder = path.substring(0, path.lastIndexOf("/"));

				}
				System.out.println(seperateOutsideFolder + "  seperateOutsideFolder");

				File textdirectory = new File(seperateOutsideFolder);
				textdirectory.mkdir();

				if (os.contains("windows")) {
					textdirectoryString = textdirectory.getPath() + "\\" + textNaming1.concat(".txt");
				} else {
					textdirectoryString = textdirectory.getPath() + "/" + textNaming1.concat(".txt");
				}

				System.out.println("directory made for text folder " + textdirectory.getAbsolutePath());

				StringBuilder seperateTextFolderBuilder = new StringBuilder(textdirectoryString);
				int indexToAppendTextFolderName = 0;
				if (path.contains("Downloads")) {

					indexToAppendTextFolderName = path.lastIndexOf("Downloads") + 10;
				} else {
					indexToAppendTextFolderName = path.lastIndexOf("Documents") + 9;
				}
				textdirectoryString = seperateTextFolderBuilder
						.insert(indexToAppendTextFolderName, "TextFolder" + File.separator).toString();

				int indexFortextDirectory = textdirectoryString.lastIndexOf(File.separator);

				String textfolderExtract = textdirectoryString.substring(0, indexFortextDirectory);

				File textDirectory = new File(textfolderExtract);
				textDirectory.mkdirs();

				extractedPath = textDirectory.getPath();

				final String FILENAME = textdirectoryString;

				Document pdf = PDF.open(officefile);
				StringBuilder text = new StringBuilder(1024);
				pdf.pipe(new OutputTarget(text));
				try {
					pdf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("text extracted from " + officefile.getName() + " ,now writing to a output file");

				try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true))) {

					bw.write(text.toString());

					// no need to close it.
					// bw.close();

					System.out.println("Done converting " + officefile.getName());

				}

				catch (IOException e1) {
					System.out.println("error occured  " + e1.getMessage());
					Thread.currentThread().interrupt();

					e1.printStackTrace();
					return;
				}
			} else {
				System.out.println(officefile.getName() + " is not a pdf file skipping the file");
			}
		}
		System.out.println("No of pdf files proecssed = " + noOfFiles);
		System.out.println("Total size of files= " + totalSizeOfFiles / (1024 * 1024) + " MB");
		System.out.println("Folder given as input = " + folderPath);
		System.out.println("Extracted text to " + extractedPath);
		System.out.println("End Of Execution");
		System.exit(0);
	}
}