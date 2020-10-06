package ScanExamProofOfConcepts;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class InsertingImage {
	
	
	/**
	 * Ajoute une image imagePath sur toutes les pages du pdf inputFile puis l'enregistre dans outputFile
	 *
	 * @param inputFile  Chemin du pdf cible
	 * @param imagePath  Chemin de l'image a insérer
	 * @param outputFile Chemin du fichier a écrire
	 *
	 * @throws IOException If there is an error writing the data.
	 */
	public static void createPdfFromImageInAllPages(String inputFile, String imagePath, String outputFile)
			throws IOException {
		try (PDDocument doc = PDDocument.load(new File(inputFile))) {
			float scale = 0.3f;

			PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);
			for (PDPage page: doc.getPages()) {
				try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
						true)) {
					
					contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
				}
			}
			doc.save(outputFile);
		}
	}	
}