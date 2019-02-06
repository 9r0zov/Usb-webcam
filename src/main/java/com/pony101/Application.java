package com.pony101;

import com.pony101.capture.CaptureTask;
import com.pony101.port.SerialPortConnector;
import com.pony101.ui.Window;
import jssc.SerialPortException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    private CaptureTask captureTask;

    public void go() {
        final Window window = new Window();
        final SerialPortConnector serialPortConnector = new SerialPortConnector();

        window.setBtnClickCallback((started, port) -> {
            if (started) {
                if (serialPortConnector.connectPort(port)) {
                    captureTask = new CaptureTask(window.getWebcam(), serialPortConnector.getSerialPort(),
                            Window.IMG_WIDTH, Window.IMG_HEIGHT);
                    new Thread(captureTask).start();
                }
            } else {
                stopReadingFrames(serialPortConnector);
            }
        });

        window.setWindowEventListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                stopReadingFrames(serialPortConnector);
            }
        });

        window.setWriteToFileSwitchCallback(write -> captureTask.setWriteToFile(write));
    }

    private void stopReadingFrames(SerialPortConnector serialPortConnector) {
        if (captureTask != null) {
            captureTask.stopCapture();
        }
        try {
            serialPortConnector.stop();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

}
