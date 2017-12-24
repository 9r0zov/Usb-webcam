package com.f0odbar;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.OutputStream;

import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.PARITY_NONE;
import static jssc.SerialPort.STOPBITS_1;

/**
 * D.Kruhlov
 *
 * @f0odbar denis.kruglov.dev@gmail.com
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

    public synchronized void stop() throws SerialPortException {
        if (serialPort != null && serialPort.isOpened()) {
            serialPort.closePort();
        }
    }

    public synchronized SerialPort getSerialPort() {
        return serialPort;
    }
}
