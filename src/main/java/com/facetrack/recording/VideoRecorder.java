package com.facetrack.recording;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecorder {

    private VideoWriter writer;
    private boolean recording = false;
    private String outputDir = "videos";
    private String currentFilePath;

    public VideoRecorder() {
        File dir = new File(outputDir);
        if (!dir.exists())
            dir.mkdirs();
    }

    public boolean startRecording(int width, int height, double fps)
    {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        int fourcc = 0;

        if (recording)
            return false;
        currentFilePath = outputDir + "/recording_" + timestamp + ".avi";
        fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
        writer = new VideoWriter(currentFilePath, fourcc, fps, new Size(width, height));
        if (!writer.isOpened()) {
            writer.release();
            writer = null;
            return false;
        }
        recording = true;
        return true;
    }

    public void writeFrame(Mat frame)
    {
        if (recording && writer != null && writer.isOpened())
            writer.write(frame);
    }

    public void stopRecording()
    {
        if (!recording)
            return;
        recording = false;
        if (writer != null) {
            writer.release();
            writer = null;
        }
    }

    public boolean isRecording()
    {
        return recording;
    }

    public String getCurrentFilePath()
    {
        return currentFilePath;
    }
}
