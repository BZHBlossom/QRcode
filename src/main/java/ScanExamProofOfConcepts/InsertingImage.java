package ScanExamProofOfConcepts;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class InsertingImage {


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

}