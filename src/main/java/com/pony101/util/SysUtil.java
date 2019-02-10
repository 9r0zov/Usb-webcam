package com.pony101.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class SysUtil {

    private static final  Logger LOG = LoggerFactory.getLogger(SysUtil.class);

    private static final String FILE_PATH = "%1$s%4$simages%4$simage_%2$s_%3$s.png";

    public static void saveImageToFile(String suffix, BufferedImage image, int frame) {
        final String userDir = System.getProperty("user.dir");
        final String separator = File.separator;
        File output = new File(format(FILE_PATH, userDir, suffix, frame, separator));

        output.mkdirs();

        try {
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void resizeFrameAndFlipHorizontal(BufferedImage image, BufferedImage resized) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-resized.getWidth(), 0));

        Graphics2D g2d = resized.createGraphics();
        g2d.setTransform(at);
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
