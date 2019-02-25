package com.pony101.ui.listener;

import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.pony101.DataDto;
import com.pony101.port.CustomSerialPortEventListener;
import com.pony101.port.SerialPortHandler;
import com.pony101.util.DataTransfer;
import com.pony101.util.SerialPortUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.concurrent.LinkedBlockingQueue;

import static com.pony101.util.SysUtil.resizeFrameAndFlipHorizontal;
import static com.pony101.util.SysUtil.transformBuffer;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;

public class FramesWebcamListener implements WebcamListener {

    private final static Logger LOG = LoggerFactory.getLogger(FramesWebcamListener.class);

    private final BufferedImage resized = new BufferedImage(128, 64, TYPE_BYTE_BINARY);

    private final SerialPortHandler serialPortHandler;
    private final DataTransfer<DataDto> dataTransfer;

    public FramesWebcamListener(SerialPortHandler serialPortHandler,
                                DataTransfer<DataDto> dataTransfer) {
        this.serialPortHandler = serialPortHandler;
        this.dataTransfer = dataTransfer;
    }

    @Override
    public void webcamOpen(WebcamEvent we) {
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
        BufferedImage image = we.getImage();

        resizeFrameAndFlipHorizontal(image, resized);
        //scale(image, resized);

        if (serialPortHandler.isOpened()) {
            WritableRaster raster = resized.getRaster();
            DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

            byte[] bytes = transformBuffer(data.getData());

            try {
                dataTransfer.setData(new DataDto(bytes, resized));
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
