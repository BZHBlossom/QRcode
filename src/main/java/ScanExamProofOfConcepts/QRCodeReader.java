package ScanExamProofOfConcepts;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.*;

import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QRCodeReader {

	/**
	 * @param pdfRenderer Pdf a lire
	 * @param startPages  page de lecture de début
	 * @param totalPages  page de lecture de fin
	 * @throws IOException
	 * 
	 *                     Lit les QRCodes du document pdfRenderer entre les pages
	 *                     startPages et totalPages
	 */
	public static void lecture(PDFRenderer pdfRenderer, int startPages, int endPages) throws IOException {
		long startThread = System.currentTimeMillis();

		for (int page = startPages; page < endPages; ++page) {
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			BufferedImage dest = bim.getSubimage(0, bim.getHeight() - (int) (bim.getHeight() * 0.3f),
					(int) (bim.getWidth() * 0.3f), (int) (bim.getHeight() * 0.3f));
			/*
			 * JFrame frame = new JFrame(); frame.getContentPane().setLayout(new
			 * FlowLayout()); frame.getContentPane().add(new JLabel(new ImageIcon(dest)));
			 * frame.pack(); frame.setVisible(true);
			 */
			System.out.println(QRCodeReader.decodeQRCodeBuffered(bim));
		}

		System.out.println("temps décodage du thread : " + (System.currentTimeMillis() - startThread) + " ms");
	}

	/**
	 * @param qrCodeimage la bufferedImage a décoder
	 * @return le texte decode du QRCOde se trouvant dans qrCodeImage
	 * @throws IOException
	 * 
	 *                     Décode le contenu de qrCodeImage et affiche le contenu
	 *                     décodé dans le system.out
	 */
	public static String decodeQRCodeBuffered(BufferedImage bufferedImage) throws IOException {
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		Map<DecodeHintType, Object> map = new HashMap<>();
		map.put(DecodeHintType.ALLOWED_EAN_EXTENSIONS, BarcodeFormat.QR_CODE);

		try {

			MultiFormatReader mfr = new MultiFormatReader();
			mfr.setHints(map);
			Result result = mfr.decodeWithState(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}
	}

	/**
	 * Ajoute une image imagePath sur toutes les pages du pdf inputFile puis
	 * l'enregistre dans outputFile
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
			for (PDPage page : doc.getPages()) {
				try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true,
						true)) {

					contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
				}
			}
			doc.save(outputFile);
		}
	}

}
