package com.pony101.capture;

import com.github.sarxos.webcam.Webcam;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.*;

/**
 * D.Kruhlov
 *
 * @devPony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class CaptureTask implements Runnable {

    private final BufferedImage resized = new BufferedImage(128, 64, TYPE_BYTE_BINARY);
    private final Webcam webcam;
    private final SerialPort serialPort;
    private final BufferedImage image;

    private Boolean started;
    private int frame;

    public CaptureTask(Webcam webcam, SerialPort serialPort, int width, int height) {
        this.webcam = webcam;
        this.serialPort = serialPort;
        this.started = true;
        this.frame = 0;
        this.image = new BufferedImage(width, height, TYPE_3BYTE_BGR);
    }

    @Override
    public void run() {
        while (started) {
            synchronized (webcam) {
                if (!webcam.isOpen() || !started) {
                    return;
                }

                image.setData(webcam.getImage().getData());
                resizeFrame();
            }

            saveImageToFile("orig", resized);

            WritableRaster raster = resized.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            try {
                if (serialPort != null && serialPort.isOpened()) {
                    serialPort.writeBytes(data.getData());
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopCapture() {
        started = false;
    }

    private void saveImageToFile(String suffix, BufferedImage image) {
        File output = new File(String.format("images/image_%s_%s.jpg", suffix, ++frame));
        output.mkdirs();
        try {
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resizeFrame() {
        Image tmp = image.getScaledInstance(128, 64, Image.SCALE_FAST);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
    }

}
