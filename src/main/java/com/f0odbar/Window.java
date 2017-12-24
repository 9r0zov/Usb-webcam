package com.f0odbar;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
import javafx.util.Pair;
import jssc.SerialNativeInterface;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * D.Kruhlov
 *
 * @f0odbar denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class Window {

    private static final String TITLE = "USB-webcam 0.1";

    private static final int IMG_WIDTH = 640;
    private static final int IMG_HEIGHT = 480;

    private final String START = "Start";
    private final String STOP = "Stop";

    private final JFrame window;

    private Webcam webcam;
    private JComboBox<String> cbPorts;
    private JButton btnStartStop;
    private boolean started;

    public Window() {
        window = new JFrame(TITLE);
        window.setSize(new Dimension(IMG_WIDTH, IMG_HEIGHT + 100));
        window.setResizable(false);
        window.setLayout(new FlowLayout());
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void initWindow() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = dim.width / 2 - IMG_WIDTH / 2;
        int y = dim.height / 2 - IMG_HEIGHT / 2;
        window.setLocation(x, y);

        try {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));

            WebcamPanel webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSLimit(30);
            webcamPanel.setSize(IMG_WIDTH, IMG_HEIGHT);
            webcam.open();

            window.add(webcamPanel);
        } catch (WebcamException e) {
            final JLabel text = new JLabel();
            text.setText("No camera detected");
            text.setFont(new Font("Arial", 0, 26));
            window.add(text);
        }

        cbPorts = new JComboBox<>(SerialPortList.getPortNames());
        cbPorts.setMinimumSize(new Dimension(200, 20));
        cbPorts.setSize(new Dimension(200, 20));
        window.add(cbPorts);

        btnStartStop = new JButton(START);

        window.add(btnStartStop);

        window.setVisible(true);
    }

    public void addCameraListener(Consumer<BufferedImage> consumer) {
        while (true) {
            if (!webcam.isOpen()) {
                break;
            }
            consumer.accept(webcam.getImage());
        }
    }

    public void setWindowEventListener(WindowListener windowListener) {
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                windowListener.windowClosing(e);
                webcam.close();
            }
        });
    }

    public void setBtnClickCallback(BiConsumer<Boolean, String> consumer) {
        if (btnStartStop != null) {
            btnStartStop.addActionListener(e -> {
                if (started) {
                    started = false;
                    btnStartStop.setText(START);
                } else {
                    started = true;
                    btnStartStop.setText(STOP);
                }
                cbPorts.setEnabled(!started);
                consumer.accept(started, cbPorts.getSelectedItem().toString());
            });
        }
    }

    public Webcam getWebcam() {
        return webcam;
    }

}
