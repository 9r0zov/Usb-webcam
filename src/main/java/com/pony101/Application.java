package com.pony101;

import com.pony101.ui.IWebcamProvider;
import jssc.SerialPort;

import static com.pony101.util.SerialPortUtil.connectPort;
import static com.pony101.util.SerialPortUtil.stopPort;

public final class Application {

    private final IWebcamProvider webcamProvider;

    private SerialPort serialPort;

    public Application(IWebcamProvider webcamProvider) {
        this.webcamProvider = webcamProvider;

        init();
    }

    private void init() {
        webcamProvider.setStartClickCallback((started, port) -> {
            if (started) {
                connectPort(port, webcamProvider)
                        .ifPresent(serialPort -> this.serialPort = serialPort);
            } else {
                if (serialPort != null) {
                    stopPort(serialPort);
                }
            }
        });
    }


}
