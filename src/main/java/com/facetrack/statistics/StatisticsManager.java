package com.facetrack.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticsManager {

    private int totalFacesDetected = 0;
    private long detectionTimeMs = 0;
    private long lastUpdateTime = 0;
    private boolean wasFaceVisible = false;
    private String outputDir = "stats";

    public StatisticsManager() {
        File dir = new File(outputDir);
        if (!dir.exists())
            dir.mkdirs();
    }

    public void update(int faceCount) {
        long now = System.currentTimeMillis();

        if (faceCount > 0) {
            totalFacesDetected += faceCount;
            if (wasFaceVisible && lastUpdateTime > 0)
                detectionTimeMs += now - lastUpdateTime;
            wasFaceVisible = true;
        } else {
            wasFaceVisible = false;
        }
        lastUpdateTime = now;
    }

    public int getTotalFacesDetected() {
        return totalFacesDetected;
    }

    public long getDetectionTimeSeconds() {
        return detectionTimeMs / 1000;
    }

    public String exportCSV() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String filePath = outputDir + "/stats_" + timestamp + ".csv";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("metrique,valeur\n");
            writer.write("total_visages_detectes," + totalFacesDetected + "\n");
            writer.write("temps_detection_secondes," + getDetectionTimeSeconds() + "\n");
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
