package com.pony101.ui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamListener;
import com.pony101.ui.listener.FramesWebcamListener;

import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IWebcamProvider {

    void addCameraListener(FramesWebcamListener listener);

    void setWriteToFileSwitchCallback(Consumer<Boolean> consumer);

    void setWindowClosingListener(Runnable consumer);

    void setStartClickCallback(BiConsumer<Boolean, String> consumer);

    Webcam getWebcam();

}
