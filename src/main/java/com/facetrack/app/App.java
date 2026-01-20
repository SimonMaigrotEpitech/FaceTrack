package com.facetrack.app;

import nu.pattern.OpenCV;
import com.facetrack.camera.Camera;
import java.awt.EventQueue;

public class App {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Camera camera = new Camera();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        camera.startCamera();
                    }
                }).start();
            }
        });
    }
}
