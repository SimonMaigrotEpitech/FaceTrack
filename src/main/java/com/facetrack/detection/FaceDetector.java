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

    public FaceDetector()
    {
        loadClassifier();
    }

    private void loadClassifier()
    {
        boolean resResources;
        boolean resEnvPath;
        boolean resSystemPaths;

        if (resResources || resEnvPath ||  resSystemPaths)
            return;
        printErrorMessage();
    }

    private void printErrorMessage()
    {
        System.err.println("Téléchargez le file de Haar (abe) et mettez le dans src/main/resources/");
    }

    public boolean isLoaded()
    {
        return loaded;
    }
}
