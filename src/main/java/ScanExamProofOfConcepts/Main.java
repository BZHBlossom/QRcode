package ScanExamProofOfConcepts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.google.zxing.WriterException;

public class Main extends Thread {
	private static final String QR_CODE = "./MyQRCode.png";
	private static final String PDFCIBLE = "./pfo_example.pdf";

	public static void lecture(PDFRenderer pdfRenderer, int debpages, int nbPagesTotal) throws IOException {
		long startThread = System.currentTimeMillis();

		for (int page = debpages; page < nbPagesTotal; ++page) {
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 80, ImageType.GRAY);
			BufferedImage dest = bim.getSubimage(540, 50, 120, 110);
			System.out.println(QRCodeReader.decodeQRCodeBuffered(dest));
		}

		System.out.println("temps décodage du thread : " + (System.currentTimeMillis() - startThread) + " ms");
	}

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

		AddImageToPDF.createPDFFromImageInAllPages(PDFCIBLE, QR_CODE, "./pfo_example_inserted.pdf");

		System.out.println("Le QR OCde a été inséré en " + (System.currentTimeMillis() - startTime) + " ms");
		startTime = System.currentTimeMillis();
		
		File pdf = new File("./pfo_example_inserted.pdf");

		PDDocument document = PDDocument.load(pdf);
		PDFRenderer pdfRenderer = new PDFRenderer(document);

		int premierQuart = document.getNumberOfPages() / 4;
		int milieu = document.getNumberOfPages() / 2;
		int troisiemeQuart = 3 * document.getNumberOfPages() / 4;

		Thread thread1 = new Thread(() -> {
			try {
				lecture(pdfRenderer, 0, premierQuart);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread1");
		Thread thread2 = new Thread(() -> {
			try {
				lecture(pdfRenderer, premierQuart, milieu);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread2");

		Thread thread3 = new Thread(() -> {
			try {
				lecture(pdfRenderer, milieu, troisiemeQuart);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "thread3");

		thread1.start();
		thread2.start();
		thread3.start();
		lecture(pdfRenderer, troisiemeQuart, document.getNumberOfPages());

		// On attends d'être sur que tous les threads on fini avant de refermer le
		// document.
		while (activeCount() > 1) {
			System.out.println(activeCount());
			sleep(50);
		}

		document.close();
		sc.close();

		System.out.println("Fin de la lecture des QRCodes");
		System.out.println("temps lecture d'execution : " + (System.currentTimeMillis() - startTime) + " ms");
	}

}
