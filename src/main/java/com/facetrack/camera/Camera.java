package com.facetrack.camera;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.facetrack.detection.FaceDetector;
import com.facetrack.detection.MultiDetector;
import com.facetrack.recording.VideoRecorder;

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
    private JLabel eyeCountLabel;
    private JLabel smileCountLabel;
    private JLabel profileCountLabel;
    private JLabel fpsLabel;
    private JLabel resolutionLabel;
    private JLabel statusLabel;
    private JButton btnCapture;
    private VideoCapture capture;
    private Mat frame;
    private boolean clicked = false;
    private boolean detectionEnabled = true;
    private FaceDetector faceDetector;
    private MultiDetector multiDetector;
    private VideoRecorder videoRecorder;
    private JLabel recordIndicator;
    private JComboBox<String> resolutionCombo;
    private int selectedWidth = 640;
    private int selectedHeight = 480;
    private int selectedCamera = 0;
    private JTextArea logArea;
    private long lastFrameTime = System.currentTimeMillis();

    public Camera() {
        faceDetector = new FaceDetector();
        multiDetector = new MultiDetector();
        videoRecorder = new VideoRecorder();

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

        eyeCountLabel = new JLabel("yeux: 0");
        eyeCountLabel.setBounds(220, 10, 150, 25);
        eyeCountLabel.setForeground(new Color(100, 100, 255));
        eyeCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        eyeCountLabel.setOpaque(true);
        eyeCountLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(eyeCountLabel, JLayeredPane.PALETTE_LAYER);

        smileCountLabel = new JLabel("sourires: 0");
        smileCountLabel.setBounds(220, 38, 150, 25);
        smileCountLabel.setForeground(Color.YELLOW);
        smileCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        smileCountLabel.setOpaque(true);
        smileCountLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(smileCountLabel, JLayeredPane.PALETTE_LAYER);

        profileCountLabel = new JLabel("profils: 0");
        profileCountLabel.setBounds(220, 66, 150, 25);
        profileCountLabel.setForeground(Color.ORANGE);
        profileCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileCountLabel.setOpaque(true);
        profileCountLabel.setBackground(new Color(0, 0, 0, 150));
        layeredPane.add(profileCountLabel, JLayeredPane.PALETTE_LAYER);

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
                videoRecorder.stopRecording();
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
                    addLog("ca detecte");
                } else {
                    btnDetection.setText("Detection: OFF");
                    addLog("ca detecte plus");
                }
            }
        });

        JButton btnRecord = new JButton("Enregistrer");
        btnRecord.setBounds(150, 200, 120, 30);
        layeredPane.add(btnRecord, JLayeredPane.PALETTE_LAYER);

        recordIndicator = new JLabel("REC");
        recordIndicator.setBounds(280, 200, 50, 30);
        recordIndicator.setForeground(Color.RED);
        recordIndicator.setFont(new Font("Arial", Font.BOLD, 16));
        recordIndicator.setOpaque(true);
        recordIndicator.setBackground(new Color(0, 0, 0, 150));
        recordIndicator.setVisible(false);
        layeredPane.add(recordIndicator, JLayeredPane.PALETTE_LAYER);

        btnRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (videoRecorder.isRecording()) {
                    videoRecorder.stopRecording();
                    btnRecord.setText("Enregistrer");
                    recordIndicator.setVisible(false);
                    addLog("enregistrement arrete: " + videoRecorder.getCurrentFilePath());
                } else {
                    int w = (frame != null) ? frame.width() : selectedWidth;
                    int h = (frame != null) ? frame.height() : selectedHeight;
                    boolean started = videoRecorder.startRecording(w, h, 20);
                    if (started) {
                        btnRecord.setText("Stop");
                        recordIndicator.setVisible(true);
                        addLog("enregistrement demarre zebi: " + videoRecorder.getCurrentFilePath());
                    } else
                        addLog("erreur: impossible de start l'enregistrement");
                }
            }
        });

        String[] resolutions = {"640x480", "1920x1080"};
        resolutionCombo = new JComboBox<>(resolutions);
        resolutionCombo.setBounds(10, 240, 120, 30);
        layeredPane.add(resolutionCombo, JLayeredPane.PALETTE_LAYER);

        resolutionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) resolutionCombo.getSelectedItem();
                String[] parts = selected.split("x");
                selectedWidth = Integer.parseInt(parts[0]);
                selectedHeight = Integer.parseInt(parts[1]);
                if (capture != null && capture.isOpened()) {
                    capture.release();
                    capture.open(0);
                    capture.set(Videoio.CAP_PROP_FRAME_WIDTH, selectedWidth);
                    capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, selectedHeight);
                }
            }
        });

        JLabel sensitivityLabel = new JLabel("Sensibilite: 3");
        sensitivityLabel.setBounds(10, 280, 120, 20);
        sensitivityLabel.setForeground(Color.WHITE);
        sensitivityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        layeredPane.add(sensitivityLabel, JLayeredPane.PALETTE_LAYER);

        JSlider sensitivitySlider = new JSlider(1, 10, 3);
        sensitivitySlider.setBounds(10, 300, 150, 30);
        layeredPane.add(sensitivitySlider, JLayeredPane.PALETTE_LAYER);

        sensitivitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                int value = sensitivitySlider.getValue();
                sensitivityLabel.setText("Sensibilite: " + value);
                faceDetector.setMinNeighbors(value);
            }
        });

        JLabel cameraLabel = new JLabel("Camera:");
        cameraLabel.setBounds(10, 340, 80, 20);
        cameraLabel.setForeground(Color.WHITE);
        cameraLabel.setFont(new Font("Arial", Font.BOLD, 14));
        layeredPane.add(cameraLabel, JLayeredPane.PALETTE_LAYER);

        String[] cameras = {"Camera 0", "Camera 1", "Camera 2"};
        JComboBox<String> cameraCombo = new JComboBox<>(cameras);
        cameraCombo.setBounds(10, 360, 100, 30);
        layeredPane.add(cameraCombo, JLayeredPane.PALETTE_LAYER);

        cameraCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedCamera = cameraCombo.getSelectedIndex();
                if (capture != null && capture.isOpened()) {
                    capture.release();
                    capture.open(selectedCamera);
                    addLog("Camera changee: " + selectedCamera);
                }
            }
        });

        JCheckBox eyeCheckBox = new JCheckBox("Yeux");
        eyeCheckBox.setBounds(10, 400, 80, 25);
        eyeCheckBox.setForeground(Color.WHITE);
        eyeCheckBox.setOpaque(false);
        layeredPane.add(eyeCheckBox, JLayeredPane.PALETTE_LAYER);

        eyeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiDetector.setEyeEnabled(eyeCheckBox.isSelected());
                addLog("detection yeux: " + (eyeCheckBox.isSelected() ? "ON" : "OFF"));
            }
        });

        JCheckBox smileCheckBox = new JCheckBox("Sourire");
        smileCheckBox.setBounds(90, 400, 90, 25);
        smileCheckBox.setForeground(Color.WHITE);
        smileCheckBox.setOpaque(false);
        layeredPane.add(smileCheckBox, JLayeredPane.PALETTE_LAYER);

        smileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiDetector.setSmileEnabled(smileCheckBox.isSelected());
                addLog("detection sourire: " + (smileCheckBox.isSelected() ? "ON" : "OFF"));
            }
        });

        JCheckBox profileCheckBox = new JCheckBox("Profil");
        profileCheckBox.setBounds(180, 400, 80, 25);
        profileCheckBox.setForeground(Color.WHITE);
        profileCheckBox.setOpaque(false);
        layeredPane.add(profileCheckBox, JLayeredPane.PALETTE_LAYER);

        profileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                multiDetector.setProfileEnabled(profileCheckBox.isSelected());
                addLog("detection profil: " + (profileCheckBox.isSelected() ? "ON" : "OFF"));
            }
        });

        JLabel logLabel = new JLabel("Logs:");
        logLabel.setBounds(10, 435, 80, 20);
        logLabel.setForeground(Color.WHITE);
        logLabel.setFont(new Font("Arial", Font.BOLD, 14));
        layeredPane.add(logLabel, JLayeredPane.PALETTE_LAYER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(0, 0, 0, 200));
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBounds(10, 455, 250, 150);
        layeredPane.add(scrollPane, JLayeredPane.PALETTE_LAYER);

        addLog("application demarree");
        if (faceDetector.isLoaded())
            addLog("OpenCV charge");
        else
            addLog("erreur: OpenCV pas charge");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                videoRecorder.stopRecording();
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
        capture = new VideoCapture(selectedCamera);
        frame = new Mat();
        byte[] imageData;
        addLog("demarrage de la caméra " + selectedCamera);

        ImageIcon icon;
        while (true) {
            if (capture.isOpened()) {
                capture.read(frame);
                if (frame.empty())
                    continue;

                int faceCount = 0;
                if (detectionEnabled) {
                    faceCount = faceDetector.detectAndDraw(frame);
                    multiDetector.detectEyes(frame, faceDetector.getLastDetectedFaces());
                    multiDetector.detectSmiles(frame, faceDetector.getLastDetectedFaces());
                    multiDetector.detectProfiles(frame);
                }

                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - lastFrameTime;
                lastFrameTime = currentTime;
                int fps = 0;
                if (elapsed > 0)
                    fps = (int) (1000 / elapsed);

                if (videoRecorder.isRecording())
                    videoRecorder.writeFrame(frame);

                int count = faceCount;
                int currentFps = fps;
                int width = frame.width();
                int height = frame.height();
                int eyes = multiDetector.getEyeCount();
                int smiles = multiDetector.getSmileCount();
                int profiles = multiDetector.getProfileCount();
                boolean recBlink = videoRecorder.isRecording() && (System.currentTimeMillis() / 500) % 2 == 0;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    faceCountLabel.setText("visages: " + count);
                    eyeCountLabel.setText("yeux: " + eyes);
                    smileCountLabel.setText("sourires: " + smiles);
                    profileCountLabel.setText("profils: " + profiles);
                    fpsLabel.setText("FPS: " + currentFps);
                    resolutionLabel.setText("Resolution: " + width + "x" + height);
                    statusLabel.setText("Camera: active");
                    statusLabel.setForeground(Color.GREEN);
                    if (videoRecorder.isRecording())
                        recordIndicator.setVisible(recBlink);
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

    private void addLog(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String log = "[" + timestamp + "] " + message + "\n";
        System.out.println(log);
        if (logArea != null) {
            logArea.append(log);
            logArea.setCaretPosition(logArea.getDocument().getLength());
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
