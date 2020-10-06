package ScanExamProofOfConcepts;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import com.google.zxing.WriterException;

public class Main extends Thread {
	private static final String QR_CODE = "./MyQRCode.png";
	//private static final String PDFCIBLE = "./CM-C-Unix.pdf";
	private static final String PDFCIBLE = "./pfo_example.pdf";
	private static final String PDFQR = "./pfo_example_inserted.pdf";
	

	public void run() {
		System.out.println("début du thread : " + Thread.currentThread().getName());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Scanner sc = new Scanner(System.in);
		long startTime = System.currentTimeMillis();

		System.out.println("Text du QRCode à générer ?");
		String stringAEncoder = sc.next();

		try {
			QRCodeGenerator.generateQRCodeImage(stringAEncoder, 350, 350, QR_CODE);

		} catch (WriterException e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
		}

		System.out.println("Le QR Code a été généré en " + (System.currentTimeMillis() - startTime) + " ms");

		InsertingImage.createPdfFromImageInAllPages(PDFCIBLE, QR_CODE, "./pfo_example_inserted.pdf");

		System.out.println("Le QR OCde a été inséré en " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();

		File pdf = new File(PDFQR);

		PDDocument document = PDDocument.load(pdf);
		PDFRenderer pdfRenderer = new PDFRenderer(document);

		int firstSixth = document.getNumberOfPages() / 6;
		int secondSixth = document.getNumberOfPages() / 3;
		int mid = document.getNumberOfPages() / 2;
		int fourthSixth = 2 * document.getNumberOfPages() / 3;
		int fifthSixth = 5 * document.getNumberOfPages() / 6;

		
		//Gestion des threads
		Thread thread1 = new Thread(() -> {
			try {
				QRCodeReader.lecture(pdfRenderer, 0, firstSixth);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread1");
		Thread thread2 = new Thread(() -> {
			try {
				QRCodeReader.lecture(pdfRenderer, firstSixth, secondSixth);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread2");

		Thread thread3 = new Thread(() -> {
			try {
				QRCodeReader.lecture(pdfRenderer, secondSixth, mid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread3");
		Thread thread4 = new Thread(() -> {
			try {
				QRCodeReader.lecture(pdfRenderer, mid, fourthSixth);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread3");
		Thread thread5 = new Thread(() -> {
			try {
				QRCodeReader.lecture(pdfRenderer, fourthSixth, fifthSixth);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread3");

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();

		QRCodeReader.lecture(pdfRenderer, fifthSixth, document.getNumberOfPages());

		// On attends d'être sur que tous les threads on fini avant de refermer le
		// document.
		while (activeCount() > 1) {
			sleep(10);
		}

		document.close();
		sc.close();

		System.out.println("Fin de la lecture des QRCodes");
	}

}
