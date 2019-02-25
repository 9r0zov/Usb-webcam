package com.pony101.port;

import com.pony101.DataDto;
import com.pony101.ui.IWebcamProvider;
import com.pony101.util.DataTransfer;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static com.pony101.util.SysUtil.safeSleep;
import static com.pony101.util.SysUtil.saveImageToFile;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CustomSerialPortEventListener implements SerialPortEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSerialPortEventListener.class);

    private static final String MESSAGE_REQUEST = "req";

    private final DataTransfer<DataDto> imageTransfer;
    private final SerialPort serialPort;

    private int frame;
    private boolean writeToFile;

    public CustomSerialPortEventListener(IWebcamProvider webcamProvider,
                                         SerialPort serialPort,
                                         DataTransfer<DataDto> imageTransfer) {
        this.serialPort = serialPort;
        this.imageTransfer = imageTransfer;

        webcamProvider.setWriteToFileSwitchCallback(this::setWriteToFile);
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        handleSerialPortEvent();
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    private void handleSerialPortEvent() {
        try {
            byte[] bytes = serialPort.readBytes();
            if (bytes != null) {
                String message = new String(bytes);
                LOG.info(message);

                if (message.equals(MESSAGE_REQUEST)) {
                    sendFrameMessage();
                }
            }
        } catch (SerialPortException | InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void sendFrameMessage() throws SerialPortException, InterruptedException {
        DataDto image = imageTransfer.getData();

        if (serialPort.isOpened()) {
            byte[] bytes = image.getData();

            // TODO: 2/25/19 remove that crutch in future
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // for Mac's build
                for (int i = 0; i < 3; i++) {
                    int to = Math.min(350 * (i + 1), bytes.length);
                    int from = i * 350;

                    serialPort.writeBytes(Arrays.copyOfRange(bytes, from, to));

                    safeSleep(MILLISECONDS, 2);
                }
            } else {
                // for Windows' build
                serialPort.writeBytes(bytes);
            }

            if (writeToFile) {
                saveImageToFile("test", image.getImg(), frame++);
            }
        }
    }

}
