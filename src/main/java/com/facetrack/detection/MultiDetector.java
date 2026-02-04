package com.facetrack.detection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class MultiDetector {

    private CascadeClassifier eyeClassifier;
    private CascadeClassifier smileClassifier;
    private CascadeClassifier profileClassifier;

    private boolean eyeEnabled = false;
    private boolean smileEnabled = false;
    private boolean profileEnabled = false;

    private int eyeCount = 0;
    private int smileCount = 0;
    private int profileCount = 0;

    public MultiDetector()
    {
        eyeClassifier = new CascadeClassifier("src/main/resources/haarcascade_eye.xml");
        smileClassifier = new CascadeClassifier("src/main/resources/haarcascade_smile.xml");
        profileClassifier = new CascadeClassifier("src/main/resources/haarcascade_profileface.xml");
    }

    public void setEyeEnabled(boolean enabled)
    {
        this.eyeEnabled = enabled;
    }

    public void setSmileEnabled(boolean enabled)
    {
        this.smileEnabled = enabled;
    }

    public void setProfileEnabled(boolean enabled)
    {
        this.profileEnabled = enabled;
    }

    public int getEyeCount()
    {
        return eyeCount;
    }

    public int getSmileCount()
    {
        return smileCount;
    }

    public int getProfileCount()
    {
        return profileCount;
    }

    public void detectEyes(Mat frame, Rect[] faces)
    {
        eyeCount = 0;
        Mat grayFrame = new Mat();
        Mat faceROI;
        MatOfRect eyes = new MatOfRect();
        Rect[] eyesArray;
        Scalar blueColor = new Scalar(255, 0, 0);
        Rect eyeRect;
    }

    public void detectSmiles(Mat frame, Rect[] faces)
    {
        smileCount = 0;
        Mat grayFrame = new Mat();
        Mat faceROI;
        MatOfRect smiles = new MatOfRect();
        Rect[] smilesArray;
        Scalar yellowColor = new Scalar(0, 255, 255);
        Rect smileRect;
    }

    public int detectProfiles(Mat frame)
    {
        profileCount = 0;
        Mat grayFrame = new Mat();
        MatOfRect profiles = new MatOfRect();
        Rect[] profilesArray;
        Scalar orangeColor = new Scalar(0, 165, 255);

        if (!profileEnabled)
            return 0;
        return profileCount;
    }
}
