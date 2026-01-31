package com.facetrack.detection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceDetector {

    private CascadeClassifier faceClassifier;
    private boolean loaded = false;

    private Scalar RECT_COLOR = new Scalar(0, 255, 0);
    private int RECT_THICKNESS = 2;
    private String CASCADE_FILE = "haarcascade_frontalface_default.xml";
    private int minNeighbors = 3;

    public FaceDetector()
    {
        loadClassifier();
    }

    private void loadClassifier()
    {
        boolean resResources = tryLoadFromResources();

        if (resResources)
            return;
        printErrorMessage();
    }

    private boolean loadFromPath(String path, String source)
    {
        boolean res = false;

        this.faceClassifier = new CascadeClassifier(path);
        loaded = !faceClassifier.empty();
        res = loaded;
        return res;
    }

    private boolean tryLoadFromResources()
    {
        boolean res = false;
        InputStream is = null;
        File tempFile = null;
        FileOutputStream fos = null;
        byte[] buffer = null;
        int bytesRead = 0;

        is = getClass().getResourceAsStream("/" + CASCADE_FILE);
        if (is == null)
            is = getClass().getClassLoader().getResourceAsStream(CASCADE_FILE);
        if (is == null)
            return res;
        try {
            tempFile = File.createTempFile("haarcascade_frontalface", ".xml");
            tempFile.deleteOnExit();
            fos = new FileOutputStream(tempFile);
            buffer = new byte[4096];
            bytesRead = is.read(buffer);
            while (bytesRead != -1) {
                fos.write(buffer, 0, bytesRead);
                bytesRead = is.read(buffer);
            }
            fos.close();
            is.close();
            res = loadFromPath(tempFile.getAbsolutePath(), "ressources");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private void printErrorMessage()
    {
        System.err.println("Téléchargez le file de Haar (abe) et mettez le dans src/main/resources/");
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setMinNeighbors(int minNeighborss)
    {
        this.minNeighbors = minNeighborss;
    }

    public int detectAndDraw(Mat frame)
    {
        if (!loaded || faceClassifier == null)
            return 0;

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        faceClassifier.detectMultiScale(grayFrame, faces, 1.1, minNeighbors);

        Rect[] facesArray = faces.toArray();
        for (Rect face : facesArray) {
            Imgproc.rectangle(frame, face.tl(), face.br(), RECT_COLOR, RECT_THICKNESS);
        }

        return facesArray.length;
    }
}
