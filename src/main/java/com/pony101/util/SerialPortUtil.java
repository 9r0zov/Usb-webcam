package com.pony101.util;

import com.pony101.port.CustomSerialPortEventListener;
import com.pony101.ui.IWebcamProvider;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static jssc.SerialPort.*;

/**
 * D.Kruhlov
 *
 * @dope-pony101 denis.kruglov.dev@gmail.com
 * date: 09.11.2017
 */
public class SerialPortUtil {

    private final static Logger LOG = LoggerFactory.getLogger(SerialPortUtil.class);

    public static Optional<SerialPort> connectPort(String portName, IWebcamProvider webcamProvider) {
        SerialPort serialPort = new SerialPort(portName);

        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_115200, DATABITS_8, STOPBITS_1, PARITY_NONE);
            serialPort.addEventListener(new CustomSerialPortEventListener(webcamProvider, serialPort));

            return Optional.of(serialPort);
        } catch (SerialPortException e) {
            LOG.error(e.getMessage(), e);
        }

        return Optional.empty();
    }

    public static boolean stopPort(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                return serialPort.closePort();
            } catch (SerialPortException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return false;
    }



}
