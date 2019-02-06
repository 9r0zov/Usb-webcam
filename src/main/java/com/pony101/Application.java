package com.pony101;

import jssc.SerialPortException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    private CaptureTask captureTask;

    public void go() {
        final Window window = new Window();
        final PortWriter portWriter = new PortWriter();

        window.setBtnClickCallback((started, port) -> {
            if (started) {
                if (portWriter.connectPort(port)) {
                    captureTask = new CaptureTask(window.getWebcam(), portWriter.getSerialPort(),
                            Window.IMG_WIDTH, Window.IMG_HEIGHT);
                    new Thread(captureTask).start();
                }
            } else {
                stopReadingFrames(portWriter);
            }
        });

        window.setWindowEventListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                stopReadingFrames(portWriter);
            }
        });
    }

    private void stopReadingFrames(PortWriter portWriter) {
        if (captureTask != null) {
            captureTask.stopCapture();
        }
        try {
            portWriter.stop();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

}
