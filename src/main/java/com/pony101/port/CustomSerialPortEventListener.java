package com.pony101.port;

import com.github.sarxos.webcam.Webcam;
import com.pony101.ui.IWebcamProvider;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import static com.pony101.util.SysUtil.*;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CustomSerialPortEventListener implements SerialPortEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSerialPortEventListener.class);

    private static final String MESSAGE_REQUEST = "req";

    private final BufferedImage resized = new BufferedImage(128, 64, TYPE_BYTE_BINARY);

    private final Webcam webcam;
    private final SerialPort serialPort;

    private int frame;
    private boolean writeToFile;

    public CustomSerialPortEventListener(IWebcamProvider webcamProvider, SerialPort serialPort) {
        this.webcam = webcamProvider.getWebcam();
        this.serialPort = serialPort;

        webcamProvider.setWriteToFileSwitchCallback(this::setWriteToFile);
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        handleSerialPortEvent();
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    private void handleSerialPortEvent() {
        try {
            byte[] bytes = serialPort.readBytes();
            if (bytes != null) {
                String message = new String(bytes);
                LOG.info(message);

                if (message.equals(MESSAGE_REQUEST)) {
                    sendFrameMessage();
                }
            }
        } catch (SerialPortException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void sendFrameMessage() throws SerialPortException {
        BufferedImage image;
        synchronized (webcam) {
            if (!webcam.isOpen()) {
                return;
            }

            image = webcam.getImage();
        }

        resizeFrameAndFlipHorizontal(image, resized);

        if (serialPort.isOpened()) {
            WritableRaster raster = resized.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            byte[] bytes = transformBuffer(data.getData());

            // todo: that crutch may be fixed in future
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // for Mac's build
                for (int i = 0; i < 3; i++) {
                    int to = Math.min(350 * (i + 1), bytes.length);
                    int from = i * 350;

                    serialPort.writeBytes(Arrays.copyOfRange(bytes, from, to));

                    safeSleep(MILLISECONDS, 2);
                }
            } else {
                // for Windows' build
                serialPort.writeBytes(bytes);
            }

            if (writeToFile) {
                saveImageToFile("test", resized, frame++);
            }
        }

    }

}
