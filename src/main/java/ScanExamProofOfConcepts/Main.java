package ScanExamProofOfConcepts;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.zxing.WriterException;


public class Main {
	private static final String QR_CODE = "./MyQRCode.png";
	private static final String PDFCIBLE = "./pfo_example.pdf";

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);

		System.out.println("Text du QRCode à générer ?");
		String stringAEncoder = sc.next();

		try {

			QRCodeGenerator.generateQRCodeImage(stringAEncoder, 350, 350, QR_CODE);

		} catch (WriterException e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
		}

		System.out.println("Le QR Code a été généré");
		System.out.println("Insertion du QR OCde");
		
		QRCodeReader.PDFtoPNG(PDFCIBLE);
		PDDocument document = PDDocument.load(new File(PDFCIBLE));

		String FICHIERCIBLE = PDFCIBLE.replace(".pdf", "");

		for (int page = 0; page < document.getNumberOfPages(); ++page) {
			String pdfPage = FICHIERCIBLE + "-page-" + (page + 1) + ".png";
			InsertingImage.insertIn(pdfPage, QR_CODE);

		}

		for (int page = 0; page < document.getNumberOfPages(); ++page) {
			String pdfPage = FICHIERCIBLE + "-page-" + (page + 1) + "-combined" + ".png";

			String decodedText = QRCodeReader.decodeQRCode(new File(pdfPage));
			if (decodedText == null) {
				System.out.println("No QR Code found in the image");
			} else {
				System.out.println("Decoded text = " + decodedText);
			}
		}

		sc.close();
		System.out.println("Fin de la lecture des QRCodes");
	}

}
