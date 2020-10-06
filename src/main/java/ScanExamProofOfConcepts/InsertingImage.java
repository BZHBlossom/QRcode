package ScanExamProofOfConcepts;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class InsertingImage {
	
	
	/**
	 * Add an image to an existing PDF document.
	 *
	 * @param inputFile  The input PDF to add the image to.
	 * @param imagePath  The filename of the image to put in the PDF.
	 * @param outputFile The file to write to the pdf to.
	 *
	 * @throws IOException If there is an error writing the data.
	 */
	public static void createPdfFromImageInAllPages(String inputFile, String imagePath, String outputFile)
			throws IOException {
		try (PDDocument doc = PDDocument.load(new File(inputFile))) {
			float scale = 0.3f;

			
			int i = 0;
			PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
			for (PDPage page: doc.getPages()) {
				try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
						true)) {
					System.out.println(pdImage.getWidth() * scale + " " + pdImage.getHeight() * scale);
					contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
				}
			}
			doc.save(outputFile);
		}
	}

	public static void insertIn(String fileName, String fileNameOverlay) throws IOException {

		// load source images
		BufferedImage image = ImageIO.read(new File(fileName));
		BufferedImage overlay = ImageIO.read(new File(fileNameOverlay));

		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		g.dispose();

		// Save as new image
		ImageIO.write(combined, "PNG", new File("./" + fileName.replace(".png", "") +"-combined.png"));
	}

	 /**
     * Add an image to an existing PDF document.
     *
     * @param inputFile The input PDF to add the image to.
     * @param imagePath The filename of the image to put in the PDF.
     * @param outputFile The file to write to the pdf to.
     *
     * @throws IOException If there is an error writing the data.
     */
    public static void createPDFFromImage( String inputFile, String imagePath, String outputFile )
            throws IOException
    {
        try (PDDocument doc = PDDocument.load(new File(inputFile)))
        {
            //we will add the image to the first page.
            PDPage page = doc.getPage(0);

            // createFromFile is the easiest way with an image file
            // if you already have the image in a BufferedImage, 
            // call LosslessFactory.createFromImage() instead
            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true))
            {
                // contentStream.drawImage(ximage, 20, 20 );
                // reduce this value if the image is too large
                float scale = 0.3f;
                contentStream.drawImage(pdImage, 480, 700, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            }
            doc.save(outputFile);
        }
    }
	
	
}