package com.pony101;

import jssc.SerialPortException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    private Capture capture;

    public void run() {
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

    private void stopCapture() {
        if (capture != null) {
            capture.stopCapture();
        }
    }

}
