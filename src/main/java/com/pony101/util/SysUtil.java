package com.pony101.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;

public class SysUtil {

    private final static Logger LOG = LoggerFactory.getLogger(SysUtil.class);

    public static void saveImageToFile(String suffix, BufferedImage image, int frame) {
        File output = new File(String.format("images/image_%s_%s.jpg", suffix, frame));

        output.mkdirs();

        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void resizeFrame(BufferedImage image, BufferedImage resized) {
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(image, 0, 0, 128, 64, null);
        g2d.dispose();
    }

    public static void safeSleep(TimeUnit timeUnit, int time) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
