package com.pony101;

import com.pony101.ui.IWebcamProvider;
import com.pony101.ui.Window;

import static com.pony101.port.SerialPortConnector.connectPort;

public final class Application {

    private final IWebcamProvider webcamProvider;

    public Application(IWebcamProvider webcamProvider) {
        this.webcamProvider = webcamProvider;

        init();
    }

    private void init() {
        webcamProvider.setStartClickCallback((started, port) -> {
            if (started) {
                connectPort(port, webcamProvider);
            }
        });
    }


}
