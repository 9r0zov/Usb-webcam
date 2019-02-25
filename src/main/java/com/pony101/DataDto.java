package com.pony101;

import java.awt.image.BufferedImage;

public class DataDto {

    private byte[] data;
    private BufferedImage img;

    public DataDto(byte[] data, BufferedImage img) {
        this.data = data;
        this.img = img;
    }

    public BufferedImage getImg() {
        return img;
    }

    public byte[] getData() {
        return data;
    }
}
