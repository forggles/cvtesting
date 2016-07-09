package com.froggles.cvtesting;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

public class Utils {
	public static BufferedImage matToBufferedImage(Mat mat) {
		int type = mat.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
		byte[] buffer = new byte[mat.channels() * mat.cols() * mat.rows()];
		mat.get(0, 0, buffer);
		BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
		System.arraycopy(buffer, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData(), 0, buffer.length);
		return image;
	}

}
