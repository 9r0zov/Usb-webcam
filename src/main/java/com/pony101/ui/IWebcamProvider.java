package com.pony101.ui;

import com.github.sarxos.webcam.Webcam;

import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IWebcamProvider {

    void addCameraListener(Consumer<BufferedImage> consumer);

    void setWriteToFileSwitchCallback(Consumer<Boolean> consumer);

    void setWindowClosingListener(Runnable consumer);

    void setStartClickCallback(BiConsumer<Boolean, String> consumer);

    Webcam getWebcam();

}
