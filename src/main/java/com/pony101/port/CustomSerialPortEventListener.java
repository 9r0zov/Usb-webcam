package com.pony101.port;

import com.github.sarxos.webcam.Webcam;
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

import static com.pony101.util.SysUtil.resizeFrame;
import static com.pony101.util.SysUtil.safeSleep;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CustomSerialPortEventListener implements SerialPortEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSerialPortEventListener.class);

    private static final String MESSAGE_REQUEST = "req";

    private final BufferedImage resized = new BufferedImage(128, 64, TYPE_BYTE_BINARY);

    private final Webcam webcam;
    private final SerialPort serialPort;

    public CustomSerialPortEventListener(Webcam webcam, SerialPort serialPort) {
        this.webcam = webcam;
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        handleSerialPortEvent(webcam, serialPort);
    }

    private void handleSerialPortEvent(Webcam webcam, SerialPort serialPort) {
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

    private void sendFrameMessage() {
        BufferedImage image;
        synchronized (webcam) {
            if (!webcam.isOpen()) {
                return;
            }

            image = webcam.getImage();
        }

        resizeFrame(image, resized);

        if (serialPort != null && serialPort.isOpened()) {
            WritableRaster raster = resized.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            // todo: remove crutch
            byte[] bytes = byteArrayToBinary(data.getData());

            try {
                for (int i = 0; i < 3; i++) {
                    serialPort.writeBytes(Arrays.copyOfRange(bytes, i * 350, 350 * (i + 1)));
                    safeSleep(MILLISECONDS, 50);
                }
            } catch (SerialPortException e) {
                LOG.error(e.getMessage(), e);
            }
        }

    }

    private byte[] byteArrayToBinary(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] > -2) {
                bytes[i] = 1;
            } else {
                bytes[i] = 0;
            }
        }

        return bytes;
    }

}
