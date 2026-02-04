package com.facetrack.detection;

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
}
