package com.facetrack.app;

import nu.pattern.OpenCV;
import org.opencv.core.Core;

public class App {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        System.out.println("OpenCV version: " + Core.VERSION);
    }
}
