package com.pony101.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
import com.pony101.ui.listener.FramesWebcamListener;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static javax.swing.SwingConstants.LEADING;

/**
 * D.Kruhlov
 *
 * @devPony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
final public class Window extends JFrame {

    private static final String TITLE = "USB-webcam 0.1";

    public static final int IMG_WIDTH = 640;
    public static final int IMG_HEIGHT = 480;

    private final String START = "Start";
    private final String STOP = "Stop";

    private Webcam webcam;
    private JCheckBox writeToFileCheckBox;
    private JComboBox<String> cbPorts;
    private JButton btnStartStop;
    private boolean started;

    private PortSearcher portSearcher;

    public Window() {
        super(TITLE);
        setSize(new Dimension(IMG_WIDTH, IMG_HEIGHT + 100));
        setResizable(false);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        initWindow();
    }

    public void addCameraListener(Consumer<BufferedImage> consumer) {
        webcam.addWebcamListener(new FramesWebcamListener(consumer));
    }

    public void setWriteToFileSwitchCallback(Consumer<Boolean> consumer) {
        writeToFileCheckBox.addItemListener(e -> consumer.accept(e.getStateChange() == ItemEvent.SELECTED));
    }

    public void setWindowEventListener(WindowListener windowListener) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                windowListener.windowClosing(e);
                portSearcher.setSearch(false);
                if (webcam != null) {
                    webcam.close();
                }
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
                    consumer.accept(started, cbPorts.getSelectedItem().toString());
                }
            });
        }
    }

    public Webcam getWebcam() {
        return webcam;
    }

    private void initWindow() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = dim.width / 2 - IMG_WIDTH / 2;
        int y = dim.height / 2 - IMG_HEIGHT / 2;
        setLocation(x, y);

        try {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));

            WebcamPanel webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSLimited(true);
            webcamPanel.setFPSLimit(50);
            webcamPanel.setSize(IMG_WIDTH, IMG_HEIGHT);
            webcamPanel.setMirrored(true);
            webcam.open();

            add(webcamPanel);
        } catch (WebcamException e) {
            final JLabel text = new JLabel();
            text.setText("No camera detected");
            text.setFont(new Font("Arial", 0, 26));
            add(text);
        }

        writeToFileCheckBox = new JCheckBox();
        writeToFileCheckBox.setSelected(false);
        writeToFileCheckBox.setText("Write to file:");
        writeToFileCheckBox.setHorizontalTextPosition(LEADING);
        add(writeToFileCheckBox);

        cbPorts = new JComboBox<>(SerialPortList.getPortNames());
        cbPorts.setMinimumSize(new Dimension(200, 20));
        cbPorts.setSize(new Dimension(200, 20));
        add(cbPorts);

        btnStartStop = new JButton(START);

        add(btnStartStop);

        portSearcher = new PortSearcher(cbPorts);
        new Thread(portSearcher).start();

        setVisible(true);
    }

    final class PortSearcher implements Runnable {

        private final DefaultComboBoxModel MODEL = new DefaultComboBoxModel<>();
        private final JComboBox CB_PORTS;

        private boolean search = true;

        PortSearcher(JComboBox cbPorts) {
            this.CB_PORTS = cbPorts;
        }

        @Override
        public void run() {
            while (search) {
                try {
                    Thread.sleep(500);

                    String[] portNames = SerialPortList.getPortNames();
                    MODEL.removeAllElements();

                    for (String portName : portNames) {
                        MODEL.addElement(portName);
                    }

                    synchronized (CB_PORTS) {
                        CB_PORTS.setModel(MODEL);
                        CB_PORTS.setEnabled(portNames.length > 0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void setSearch(boolean search) {
            this.search = search;
        }
    }

}
