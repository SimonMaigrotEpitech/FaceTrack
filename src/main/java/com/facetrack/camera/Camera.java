package com.facetrack.camera;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import com.facetrack.detection.FaceDetector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends JFrame {

    private JLayeredPane layeredPane;
    private JLabel cameraScreen;
    private JLabel faceCountLabel;
    private JLabel fpsLabel;
    private JLabel resolutionLabel;
    private JLabel statusLabel;
    private JButton btnCapture;
    private VideoCapture capture;
    private Mat frame;
    private boolean clicked = false;
    private boolean detectionEnabled = true;
    private FaceDetector faceDetector;
    private long lastFrameTime = System.currentTimeMillis();

    public Camera() {
        faceDetector = new FaceDetector();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));
        setContentPane(layeredPane);

        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 1920, 1080);
        layeredPane.add(cameraScreen, JLayeredPane.DEFAULT_LAYER);

        faceCountLabel = new JLabel("nb visages: 0");
        faceCountLabel.setBounds(10, 10, 200, 30);
        faceCountLabel.setForeground(Color.GREEN);
        faceCountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        faceCountLabel.setOpaque(true);
        faceCountLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(faceCountLabel, JLayeredPane.PALETTE_LAYER);

        fpsLabel = new JLabel("FPS: 0");
        fpsLabel.setBounds(10, 45, 100, 30);
        fpsLabel.setForeground(Color.GREEN);
        fpsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        fpsLabel.setOpaque(true);
        fpsLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(fpsLabel, JLayeredPane.PALETTE_LAYER);

        resolutionLabel = new JLabel("Resolution: -");
        resolutionLabel.setBounds(10, 80, 200, 30);
        resolutionLabel.setForeground(Color.GREEN);
        resolutionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        resolutionLabel.setOpaque(true);
        resolutionLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(resolutionLabel, JLayeredPane.PALETTE_LAYER);

        statusLabel = new JLabel("Camera: stopped");
        statusLabel.setBounds(10, 115, 200, 30);
        statusLabel.setForeground(Color.ORANGE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(statusLabel, JLayeredPane.PALETTE_LAYER);

        btnCapture = new JButton("Capture");
        btnCapture.setBounds(10, 160, 100, 30);
        layeredPane.add(btnCapture, JLayeredPane.PALETTE_LAYER);

        btnCapture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });

        JButton btnQuit = new JButton("Quitter");
        btnQuit.setBounds(120, 160, 100, 30);
        layeredPane.add(btnQuit, JLayeredPane.PALETTE_LAYER);

        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                capture.release();
                frame.release();
                System.exit(0);
            }
        });

        JButton btnDetection = new JButton("Detection: ON");
        btnDetection.setBounds(10, 200, 130, 30);
        layeredPane.add(btnDetection, JLayeredPane.PALETTE_LAYER);

        btnDetection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detectionEnabled = !detectionEnabled;
                if (detectionEnabled) {
                    btnDetection.setText("Detection: ON");
                } else {
                    btnDetection.setText("Detection: OFF");
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                capture.release();
                frame.release();
                System.exit(0);
            }
        });

        setTitle("Camera");
        setLocationRelativeTo(null);
        setSize(new Dimension(1920, 1080));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startCamera() {
        capture = new VideoCapture(0);
        frame = new Mat();
        byte[] imageData;

        ImageIcon icon;
        while (true) {
            if (capture.isOpened()) {
                capture.read(frame);

                int faceCount = 0;
                if (detectionEnabled) {
                    faceCount = faceDetector.detectAndDraw(frame);
                }

                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - lastFrameTime;
                lastFrameTime = currentTime;
                int fps = 0;
                if (elapsed > 0)
                    fps = (int) (1000 / elapsed);

                int count = faceCount;
                int currentFps = fps;
                int width = frame.width();
                int height = frame.height();
                javax.swing.SwingUtilities.invokeLater(() -> {
                    faceCountLabel.setText("visages: " + count);
                    fpsLabel.setText("FPS: " + currentFps);
                    resolutionLabel.setText("Resolution: " + width + "x" + height);
                    statusLabel.setText("Camera: active");
                    statusLabel.setForeground(Color.GREEN);
                });

                MatOfByte buf = new MatOfByte();
                Imgcodecs.imencode(".jpg", frame, buf);

                imageData = buf.toArray();

                icon = new ImageIcon(imageData);

                cameraScreen.setIcon(icon);

                if (clicked) {
                    String name = JOptionPane.showInputDialog(this, "Enter image name:");
                    if (name == null) {
                        name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                    }
                    Imgcodecs.imwrite("images/" + name + ".jpg", frame);

                    clicked = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
