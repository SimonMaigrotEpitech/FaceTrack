package com.facetrack.monitoring;

public class PresenceMonitor {

    private int thresholdSeconds = 5;
    private long lastFaceSeenTime;
    private boolean alertActive = false;

    public PresenceMonitor()
    {
        lastFaceSeenTime = System.currentTimeMillis();
    }

    public void update(int faceCount)
    {
        long elapsed = System.currentTimeMillis() - lastFaceSeenTime;

        if (faceCount > 0) {
            lastFaceSeenTime = System.currentTimeMillis();
            alertActive = false;
        } else
            alertActive = elapsed >= thresholdSeconds * 1000L;
    }

    public boolean isAlertActive()
    {
        return alertActive;
    }

    public void setThresholdSeconds(int seconds)
    {
        this.thresholdSeconds = seconds;
    }

    public int getThresholdSeconds()
    {
        return thresholdSeconds;
    }
}
