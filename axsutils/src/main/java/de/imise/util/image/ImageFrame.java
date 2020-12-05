package de.imise.util.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageFrame extends JFrame {

    JLabel imageLabel = new JLabel();

    public ImageFrame() {
        this(null, false);
    }

    public ImageFrame(BufferedImage image) {
        this(image, false);
    }

    public ImageFrame(BufferedImage image, boolean undecorated) {
        this(image, null, undecorated);
    }

    public ImageFrame(BufferedImage image, String title, boolean undecorated) {
        super();
        setUndecorated(undecorated);
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            getContentPane().setLayout(new BorderLayout());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            getContentPane().setBackground(Color.red);
            imageLabel.setIcon(icon);
        }
        getContentPane().add(imageLabel, BorderLayout.CENTER);
        setTitle(title);
        pack();
        setVisible(true);

    }

    public void setImage(BufferedImage image) {
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        } else
            imageLabel.setIcon(null);

        pack();
        setVisible(true);
    }

}
