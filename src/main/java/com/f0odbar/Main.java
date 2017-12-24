package com.f0odbar;

import jssc.SerialPortException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * D.Kruhlov
 *
 * @f0odbar denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class Main {

    private static Capture capture;

    public static void main(String[] args) {
        Window window = new Window();
        window.initWindow();

        final PortWriter portWriter = new PortWriter();

        window.setBtnClickCallback((started, port) -> {
            if (started) {
                if (portWriter.connectPort(port)) {
                    capture = new Capture(window.getWebcam(), portWriter.getSerialPort());
                    capture.start();
                }
            } else {
                stopCapture();
                try {
                    portWriter.stop();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });

        window.setWindowEventListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                stopCapture();
                try {
                    portWriter.stop();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void stopCapture() {
        if (capture != null) {
            capture.stopCapture();
        }
    }
}
