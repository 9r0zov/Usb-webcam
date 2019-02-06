package com.pony101.port;

import jssc.SerialPort;
import jssc.SerialPortException;

import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.PARITY_NONE;
import static jssc.SerialPort.STOPBITS_1;

/**
 * D.Kruhlov
 *
 * @devPony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class PortWriter {

    private SerialPort serialPort;

    public boolean connectPort(String portName) {
        serialPort = new SerialPort(portName);

        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200, DATABITS_8, STOPBITS_1, PARITY_NONE);
            serialPort.addEventListener(serialPortEvent -> {
                try {
                    System.out.println(serialPort.readBytes());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            });

            return serialPort.isOpened();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void stop() throws SerialPortException {
        if (serialPort != null && serialPort.isOpened()) {
            serialPort.closePort();
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }
}
