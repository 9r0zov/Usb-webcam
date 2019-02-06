package com.pony101.ui.listener;

import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class FramesWebcamListener implements WebcamListener {

    private Consumer<BufferedImage> consumer;

    public FramesWebcamListener(Consumer<BufferedImage> consumer) {
        this.consumer = consumer;
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
        consumer.accept(we.getImage());
    }
}
