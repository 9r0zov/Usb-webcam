package com.pony101.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
import com.pony101.log.LogMessageReceiverAppender;
import com.pony101.log.TextAreaOutputStream;
import com.pony101.ui.listener.FramesWebcamListener;
import jssc.SerialPortList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static javax.swing.SwingConstants.LEADING;

/**
 * D.Kruhlov
 *
 * @dope-pony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public final class Window extends JFrame {

    private final static Logger LOG = LoggerFactory.getLogger(Window.class);

    private static final String TITLE = "USB-webcam 0.1";

    public static final int IMG_WIDTH = 640;
    public static final int IMG_HEIGHT = 480;
    private static final int WINDOW_HEIGHT = IMG_HEIGHT + 250;
    private static final int LOG_LINES_BUFFER = 50;

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
        setSize(new Dimension(IMG_WIDTH, WINDOW_HEIGHT));
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

    public void setWindowClosingListener(Runnable consumer) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                consumer.run();
                portSearcher.setSearch(false);
                if (webcam != null) {
                    webcam.close();
                }
            }
        });
    }

    public void setStartClickCallback(BiConsumer<Boolean, String> consumer) {
        if (btnStartStop != null) {
            btnStartStop.addActionListener(e -> {

                if (started) {
                    started = false;
                    btnStartStop.setText(START);
                } else {
                    if (cbPorts.getSelectedItem() != null) {
                        started = true;
                        btnStartStop.setText(STOP);
                    }
                }

                cbPorts.setEnabled(!started && cbPorts.getItemCount() > 0);
                consumer.accept(started, cbPorts.getSelectedItem().toString());

            });
        }
    }

    public Webcam getWebcam() {
        return webcam;
    }

    private void initWindow() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = dim.width / 2 - IMG_WIDTH / 2;
        int y = dim.height / 2 - WINDOW_HEIGHT / 2;
        setLocation(x, y);

        try {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(IMG_WIDTH, IMG_HEIGHT));

            WebcamPanel webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSLimited(false);
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

        JTextArea logArea = new JTextArea();
        logArea.setEnabled(false);
        logArea.setColumns(50);
        logArea.setRows(10);
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setWheelScrollingEnabled(true);

        add(scrollPane);

        LogMessageReceiverAppender.setStaticOutputStream(new TextAreaOutputStream(logArea));

        portSearcher = new PortSearcher(cbPorts, btnStartStop);
        new Thread(portSearcher).start();

        setVisible(true);
    }

    final class PortSearcher implements Runnable {

        private final DefaultComboBoxModel MODEL = new DefaultComboBoxModel<>();
        private final JComboBox cbPorts;
        private final JButton startBtn;

        private boolean search = true;

        private String prevPort;

        PortSearcher(JComboBox cbPorts, JButton startBtn) {
            this.cbPorts = cbPorts;
            this.startBtn = startBtn;
        }

        @Override
        public void run() {
            while (search) {
                try {
                    Thread.sleep(500);

                    prevPort = cbPorts.getSelectedItem() != null ? cbPorts.getSelectedItem().toString() : "";

                    String[] portNames = SerialPortList.getPortNames();

                    if (Stream.of(portNames).noneMatch(n -> n.equals(prevPort))
                            && startBtn.getText().equals(STOP)) {
                        startBtn.doClick();
                    }

                    MODEL.removeAllElements();

                    for (String portName : portNames) {
                        MODEL.addElement(portName);
                    }

                    cbPorts.setModel(MODEL);
                    cbPorts.setEnabled(portNames.length > 0 && startBtn.getText().equals(START));
                    startBtn.setEnabled(cbPorts.isEnabled() || startBtn.getText().equals(STOP));

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
