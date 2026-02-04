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

        if (!eyeEnabled)
            return;
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        for (Rect face : faces) {
            faceROI = grayFrame.submat(face);
            eyes = new MatOfRect();
            eyeClassifier.detectMultiScale(faceROI, eyes, 1.1, 5);
            eyesArray = eyes.toArray();
            eyeCount += eyesArray.length;
            for (Rect eye : eyesArray) {
                eyeRect = new Rect(face.x + eye.x, face.y + eye.y, eye.width, eye.height);
                Imgproc.rectangle(frame, eyeRect.tl(), eyeRect.br(), blueColor, 2);
            }
        }
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

        if (!smileEnabled)
            return;
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        for (Rect face : faces) {
            faceROI = grayFrame.submat(face);
            smiles = new MatOfRect();
            smileClassifier.detectMultiScale(faceROI, smiles, 1.8, 20);
            smilesArray = smiles.toArray();
            smileCount += smilesArray.length;
            for (Rect smile : smilesArray) {
                smileRect = new Rect(face.x + smile.x, face.y + smile.y, smile.width, smile.height);
                Imgproc.rectangle(frame, smileRect.tl(), smileRect.br(), yellowColor, 2);
            }
        }
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
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);
        profileClassifier.detectMultiScale(grayFrame, profiles, 1.1, 3);
        profilesArray = profiles.toArray();
        profileCount = profilesArray.length;
        for (Rect profile : profilesArray)
            Imgproc.rectangle(frame, profile.tl(), profile.br(), orangeColor, 2);
        return profileCount;
    }
}
