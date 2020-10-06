package ScanExamProofOfConcepts;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * This is an example that creates a reads a document and adds an image to it..
 *
 * The example is taken from the pdf file format specification.
 *
 * @author Ben Litchfield
 */
public class AddImageToPDF {
	/**
	 * Add an image to an existing PDF document.
	 *
	 * @param inputFile  The input PDF to add the image to.
	 * @param imagePath  The filename of the image to put in the PDF.
	 * @param outputFile The file to write to the pdf to.
	 *
	 * @throws IOException If there is an error writing the data.
	 */
	public static void createPDFFromImage(String inputFile, String imagePath, String outputFile) throws IOException {
		try (PDDocument doc = PDDocument.load(new File(inputFile))) {
			// we will add the image to the first page.
			PDPage page = doc.getPage(0);

			// createFromFile is the easiest way with an image file
			// if you already have the image in a BufferedImage,
			// call LosslessFactory.createFromImage() instead
			PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

			try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
					true)) {
				// contentStream.drawImage(ximage, 20, 20 );
				// better method inspired by http://stackoverflow.com/a/22318681/535646
				// reduce this value if the image is too large
				float scale = 0.3f;
				contentStream.drawImage(pdImage, 480, 700, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
			}
			doc.save(outputFile);
		}
	}

	public static void createPDFFromImageInAllPages(String inputFile, String imagePath, String outputFile)
			throws IOException {
		try (PDDocument doc = PDDocument.load(new File(inputFile))) {
			int nbPage = doc.getNumberOfPages();
			float scale = 0.3f;
			PDPage page = doc.getPage(0);

			PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
			for (int i = 0; i < nbPage; i++) {
				try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
						true)) {

					contentStream.drawImage(pdImage, 480, 700, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
					page = doc.getPage(i);
				}
			}
			doc.save(outputFile);
		}
	}

	/**
	 * This will load a PDF document and add a single image on it. <br>
	 * see usage() for commandline
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws IOException {
		createPDFFromImage("./pfo_example.pdf", "./MyQRCode.png", "./pfo_example_inserted.pdf");
	}

}
