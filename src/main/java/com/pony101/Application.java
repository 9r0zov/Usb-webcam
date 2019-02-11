package com.pony101;

import com.pony101.port.PortDataSenderTask;
import com.pony101.ui.Window;
import jssc.SerialPort;

import java.util.Optional;

import static com.pony101.port.SerialPortConnector.connectPort;

public final class Application {

    private final Window window;
    private final PortDataSenderTask portDataSenderTask;

    public Application() {
        this.window = new Window();
        this.portDataSenderTask = new PortDataSenderTask(window.getWebcam(), Window.IMG_WIDTH, Window.IMG_HEIGHT);

        init();
    }

    private void init() {
        new Thread(portDataSenderTask).start();

        window.setStartClickCallback((started, port) -> {
            if (started) {
                Optional<SerialPort> serialPort = connectPort(port, window.getWebcam());
                serialPort.filter(SerialPort::isOpened)
                        .ifPresent(portDataSenderTask::setSerialPort);
            } else {
                portDataSenderTask.stopCapture();
            }
        });

        window.setWindowClosingListener(portDataSenderTask::stop);
        window.setWriteToFileSwitchCallback(portDataSenderTask::setWriteToFile);
    }


}
