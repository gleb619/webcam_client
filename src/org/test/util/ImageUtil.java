package org.test.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class ImageUtil {

	public static BufferedImage convertToGray(BufferedImage image) {
		BufferedImage gray = null;
		gray = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		ColorConvertOp op = new ColorConvertOp(image.getColorModel()
				.getColorSpace(), gray.getColorModel().getColorSpace(), null);
		op.filter(image, gray);
		
		return gray;
	}
	
	public static void compress(BufferedImage image, String path) throws FileNotFoundException, IOException {
		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
//		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("png").next();
		
		ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();

		if (jpgWriteParam.canWriteCompressed()) {
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality(0.5f);
		}
		
		if (jpgWriteParam.canWriteProgressive()) {
			jpgWriteParam.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_COPY_FROM_METADATA);
		}
		

		FileImageOutputStream outputStream = new FileImageOutputStream(new File(path));
		jpgWriter.setOutput(outputStream);
		IIOImage outputImage = new IIOImage(image, null, null);
		jpgWriter.write(null, outputImage, jpgWriteParam);
		jpgWriter.dispose();
	}
	
}
