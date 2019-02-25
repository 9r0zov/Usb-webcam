package com.pony101;

import com.pony101.port.SerialPortHandler;
import com.pony101.ui.IWebcamProvider;
import com.pony101.ui.listener.FramesWebcamListener;
import com.pony101.util.DataTransfer;

import static com.pony101.util.SerialPortUtil.connectPort;

public final class Application {

    private final SerialPortHandler serialPortHandler = new SerialPortHandler();
    private final IWebcamProvider webcamProvider;

    private final DataTransfer<DataDto> dataTransfer = new DataTransfer<>();


    public Application(IWebcamProvider webcamProvider) {
        this.webcamProvider = webcamProvider;

        init();
    }

    private void init() {
        webcamProvider.setStartClickCallback((started, port) -> {
            if (started) {
                connectPort(port, webcamProvider, dataTransfer)
                        .ifPresent(serialPortHandler::setSerialPort);
            } else {
                serialPortHandler.stop();
            }
        });

        webcamProvider.addCameraListener(new FramesWebcamListener(serialPortHandler, dataTransfer));
    }


}
