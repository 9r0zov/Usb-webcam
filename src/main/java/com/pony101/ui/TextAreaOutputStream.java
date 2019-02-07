package com.pony101.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private static final int LOG_LINES_BUFFER = 50;

    private JTextArea textArea;

    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        textArea.append(String.valueOf((char) b));

        if (textArea.getLineCount() > LOG_LINES_BUFFER) {
            try {
                textArea.replaceRange("", 0, textArea.getLineEndOffset(0));
            } catch (BadLocationException e) {
                System.out.println("so bad");
            }
        }
    }

}
