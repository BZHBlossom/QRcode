package ScanExamProofOfConcepts;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.rendering.*;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.*;
import java.io.*;

public class QRCodeReader {

	/**
	 * @param qrCodeimage
	 * @return le texte decode du QRCOde se trouvant dans qrCodeImage
	 * @throws IOException
	 */
	public static String decodeQRCode(File qrCodeimage) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(qrCodeimage);
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}
	}
	
	/**
	 * @param qrCodeimage
	 * @return le texte decode du QRCOde se trouvant dans qrCodeImage
	 * @throws IOException
	 */
	public static String decodeQRCodeBuffered(BufferedImage bufferedImage) throws IOException {
		LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			Result result = new MultiFormatReader().decode(bitmap);
			return result.getText();
		} catch (NotFoundException e) {
			System.out.println("There is no QR code in the image");
			return null;
		}
	}
	
	

	/**
	 * @param pdfFilename
	 * 
	 *                    Crée un nouveau fichier pdfFilename.png a partir de
	 *                    pdfFileName
	 */
	public static void PDFtoPNG(String pdfFilename) {
		PDDocument document;
		try {
			document = PDDocument.load(new File(pdfFilename));
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				ImageIOUtil.writeImage(bim, pdfFilename.replace(".pdf", "") + "-page-" + (page + 1) + ".png", 300);
			}
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		try {

			// PDFtoPNG("correctedpdf");

			// File file = new File("corrected-2.png");
			// File file = new File("MyQRCode.png");

			File file = new File("correctedpdf.png");

			String decodedText = decodeQRCode(file);
			if (decodedText == null) {
				System.out.println("No QR Code found in the image");
			} else {
				System.out.println("Decoded text = " + decodedText);
			}
		} catch (IOException e) {
			System.out.println("Could not decode QR Code, IOException :: " + e.getMessage());
		}

		System.out.println("");

	}
}
