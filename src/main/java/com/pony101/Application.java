package com.pony101;

import com.pony101.port.SerialPortHandler;
import com.pony101.ui.IWebcamProvider;

import static com.pony101.util.SerialPortUtil.connectPort;

public final class Application {

    private final SerialPortHandler serialPortHandler = new SerialPortHandler();
    private final IWebcamProvider webcamProvider;

    public Application(IWebcamProvider webcamProvider) {
        this.webcamProvider = webcamProvider;

        init();
    }

    private void init() {
        webcamProvider.setStartClickCallback((started, port) -> {
            if (started) {
                connectPort(port, webcamProvider)
                        .ifPresent(serialPortHandler::setSerialPort);
            } else {
                serialPortHandler.stop();
            }
        });
    }


}
