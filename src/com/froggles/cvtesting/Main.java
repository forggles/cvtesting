package com.froggles.cvtesting;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import org.omg.CORBA.portable.UnknownException;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Main {
	
	public static JPanel jpanel = new JPanel();
	public static JFrame jframe = new JFrame();
	public static JLabel jlabel = new JLabel();

	public static void main(String[] args) {
		jframe.setContentPane(jpanel);
		jframe.setSize(640, 480);
		jframe.setVisible(true);
		jpanel.add(jlabel);
		jlabel.setBounds(0, 0, 640, 480);
		
		int cameraid = 1;
		String definitionsfile = "lbpcascade_frontalface.xml";

		System.out.println("Hello, OpenCV");
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture(cameraid);
		//camera.open(cameraid); // Useless
		if (!camera.isOpened()) {
			System.out.println("Camera Error");
		} else {
			System.out.println("Camera OK?");
		}

		Mat frame = new Mat();
		Mat circletemplate = Highgui.imread(Main.class.getResource("/resources/circle.png").getPath().substring(1));
		//loop start
		while(true){
		camera.read(frame);

		// Then it is OK to load the file.
		CascadeClassifier Detector = new CascadeClassifier(Main.class.getResource("/resources/"+definitionsfile).getPath().substring(1));
		Mat blurredImage = new Mat();
		Mat hsvImage = new Mat();
		Mat mask = new Mat();
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Mat circleinput = new Mat();
		int result_cols = frame.cols() - circletemplate.cols() + 1;
        int result_rows = frame.rows() - circletemplate.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
		//
		Imgproc.blur(frame, blurredImage, new Size(3, 3));
		Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
		Core.inRange(hsvImage, new Scalar(0,0,230), new Scalar(150,25,255), mask);
		// find contours
		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
		{
		        // for each contour, display it in blue
		        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
		        {
		                Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
		        }
		}
		Imgproc.matchTemplate(frame, circletemplate, result, Imgproc.TM_CCOEFF);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        MinMaxLocResult mmr = Core.minMaxLoc(result);

        // / Show me what you got
        Core.rectangle(frame, mmr.minLoc, new Point(mmr.minLoc.x + circletemplate.cols(),mmr.minLoc.y + circletemplate.rows()), new Scalar(0, 255, 0));
		//frame = tempmatch;
		/*
	    // Detect thing in the image.
	    // MatOfRect is a special container class for Rect.
	    MatOfRect Detections = new MatOfRect();
	    Detector.detectMultiScale(frame, Detections);

	    System.out.println(String.format("Detected %s objects", Detections.toArray().length));
		for (Rect rect : Detections.toArray()) {
	        Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 0));
	        //Core.circle(frame, new Point(rect.x+rect.width/2,rect.y+rect.height/2), 30, new Scalar(0,0,0));
	    }
	    */
		
		jlabel.setIcon(new ImageIcon(Utils.matToBufferedImage(frame)));
		jlabel.repaint();
		}
	}
	
}