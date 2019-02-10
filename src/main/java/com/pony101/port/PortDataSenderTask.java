package com.pony101.port;

import com.github.sarxos.webcam.Webcam;
import jssc.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import static com.pony101.port.SerialPortConnector.stopPort;
import static com.pony101.util.SysUtil.resizeFrameAndFlipHorizontal;
import static com.pony101.util.SysUtil.saveImageToFile;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * D.Kruhlov
 *
 * @dope-pony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class PortDataSenderTask implements Runnable {

    private final static Logger LOG = LoggerFactory.getLogger(PortDataSenderTask.class);

    private final BufferedImage resized;
    private final BufferedImage image;
    private final Webcam webcam;

    private SerialPort serialPort;
    private boolean started;
    private boolean writeToFile;
    private int frame;


    public PortDataSenderTask(Webcam webcam, int width, int height) {
        this.webcam = webcam;
        this.started = true;
        this.frame = 0;
        this.resized = new BufferedImage(128, 64, TYPE_BYTE_BINARY);
        this.image = new BufferedImage(width, height, TYPE_INT_RGB);
    }

    @Override
    public void run() {
        while (started) {
            synchronized (webcam) {
                if (!webcam.isOpen()) {
                    return;
                }

                image.setData(webcam.getImage().getData());
            }

            resizeFrameAndFlipHorizontal(image, resized);

            if (writeToFile) {
                saveImageToFile("resized", resized, ++frame);
            }
        }
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void stopCapture() {
        stopPort(serialPort);
        serialPort = null;
    }

    public void stop() {
        stopPort(serialPort);
        started = false;
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

}
