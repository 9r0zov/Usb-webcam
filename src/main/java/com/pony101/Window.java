package com.pony101;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
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
 * @devPony101 denis.kruglov.dev@gmail.com
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

    private PortSearcher portSearcher;

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
            webcamPanel.setFPSLimited(true);
            webcamPanel.setFPSLimit(30);
            webcamPanel.setSize(IMG_WIDTH, IMG_HEIGHT);
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setMirrored(true);
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

        portSearcher = new PortSearcher();
        portSearcher.start();

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
                if (cbPorts.getSelectedItem() != null) {
                    if (started) {
                        started = false;
                        btnStartStop.setText(START);
                    } else {
                        started = true;
                        btnStartStop.setText(STOP);
                    }
                    cbPorts.setEnabled(!started);
                    portSearcher.setSearch(false);
                    consumer.accept(started, cbPorts.getSelectedItem().toString());
                }
            });
        }
    }

    public Webcam getWebcam() {
        return webcam;
    }

    public class PortSearcher extends Thread {

        private boolean search = true;
        private final DefaultComboBoxModel MODEL = new DefaultComboBoxModel<>();

        public PortSearcher() {
            super("PORT_SEARCHER");
        }

        @Override
        public void run() {
            while (search) {
                try {
                    Thread.sleep(500);
                    MODEL.removeAllElements();
                    String[] portNames = SerialPortList.getPortNames();
                    for (int i = 0; i < portNames.length; i++) {
                        MODEL.addElement(portNames[i]);
                    }

                    Window.this.cbPorts.setModel(MODEL);
                    Window.this.cbPorts.setEnabled(portNames.length > 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setSearch(boolean search) {
            this.search = search;
        }
    }

}
