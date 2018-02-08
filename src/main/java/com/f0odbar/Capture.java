package com.f0odbar;

import com.github.sarxos.webcam.Webcam;
import jssc.SerialPort;
import jssc.SerialPortException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * D.Kruhlov
 *
 * @f0odbar denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class Capture extends Thread {

    private final Webcam webcam;
    private final SerialPort serialPort;

    private AtomicBoolean started;

    private AtomicInteger frame;

    public Capture(Webcam webcam, SerialPort serialPort) {
        this.webcam = webcam;
        this.serialPort = serialPort;
        started = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        frame = new AtomicInteger(0);
        while (started.get()) {
            if (!webcam.isOpen() || !started.get()) {
                break;
            }

            BufferedImage image = webcam.getImage();
            //saveImageToFile("orig", image);

            BufferedImage grayImage = getGrayImage(image);
            //saveImageToFile("gray", grayImage);

            WritableRaster raster = grayImage.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
            try {
                if (serialPort != null && serialPort.isOpened()) {
                    //serialPort.writeByte((byte) 0XAB);
                    serialPort.writeBytes(data.getData());
                    Thread.sleep(300);
                }
            } catch (SerialPortException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToFile(String suffix, BufferedImage image) {
        File output = new File(String.format("images/image_%s_%s.jpg", suffix, frame.addAndGet(1)));
        output.mkdirs();
        try {
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopCapture() {
        started.set(false);
    }

    private BufferedImage getGrayImage(BufferedImage image) {
        BufferedImage gray = new BufferedImage(128, 64, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(image, 0, 0, 128, 64, null);
        g.dispose();

        return gray;
    }
}
