package com.pony101.port;

import jssc.SerialPort;

import static com.pony101.util.SerialPortUtil.stopPort;

public class SerialPortHandler {

    private SerialPort serialPort;

    public void stop() {
        if (serialPort != null) {
            stopPort(serialPort);
        }
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public boolean isOpened() {
        return serialPort.isOpened();
    }
}
