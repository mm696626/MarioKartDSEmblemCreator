package io;

import constants.EmblemConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class EmblemCreator {

    private static final int PIXELS = 32;

    public BufferedImage createEmblem(String imagePath, ArrayList<JCheckBox> colorSettingToggled, JComboBox transparentBackgroundColor) {
        BufferedImage sourceImage = null; //source image
        BufferedImage emblemImage = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB); //new image

        try {
            sourceImage = ImageIO.read(new File(imagePath));
        }
        catch (Exception e) {
            //already handled it below
        }

        if (sourceImage == null) {
            //this is if the image couldn't be read
            return null;
        }

        sourceImage = removeTransparentBackground(sourceImage, transparentBackgroundColor);
        return createEmblemImage(emblemImage, sourceImage, colorSettingToggled);
    }

    public BufferedImage createEmblem(BufferedImage bufferedImage, ArrayList<JCheckBox> colorSettingToggled, JComboBox transparentBackgroundColor) {
        BufferedImage emblemImage = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB); //new image

        if (bufferedImage == null) {
            //this is if the image couldn't be read
            return null;
        }

        bufferedImage = removeTransparentBackground(bufferedImage, transparentBackgroundColor);
        return createEmblemImage(emblemImage, bufferedImage, colorSettingToggled);
    }

    private BufferedImage removeTransparentBackground(BufferedImage image, JComboBox transparentBackgroundColor) {
        BufferedImage newImage = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_ARGB); //new image
        Graphics2D g2d = newImage.createGraphics();
        g2d.setPaint(EmblemConstants.COLORS[transparentBackgroundColor.getSelectedIndex()]);
        g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        g2d.drawImage(image, 0, 0, null);
        return newImage;
    }

    private BufferedImage createEmblemImage(BufferedImage emblemImage, BufferedImage sourceImage, ArrayList<JCheckBox> colorSettingToggled) {
        Graphics2D g2d = emblemImage.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, PIXELS, PIXELS);
        sourceImage = resize(sourceImage);

        for (int i=0; i<PIXELS; i++) {
            for (int j=0; j<PIXELS; j++) {
                int rgb = sourceImage.getRGB(i,j);
                Color pixelColor = new Color(rgb);
                g2d.setPaint(getColorForEmblemPixel(pixelColor, colorSettingToggled));
                g2d.fillRect(i, j,1,1);
            }
        }

        return emblemImage;
    }

    private static BufferedImage resize(BufferedImage img) {
        Image tmp = img.getScaledInstance(PIXELS, PIXELS, Image.SCALE_DEFAULT);
        BufferedImage resized = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    private Color getColorForEmblemPixel(Color pixelColor, ArrayList<JCheckBox> colorSettingToggled) {
        Color closestColor = null;
        double distanceOfClosestColor = Double.MAX_VALUE;
        
        for (int i = 0; i<EmblemConstants.COLORS.length; i++) {
            double distance = distanceFormula(pixelColor, EmblemConstants.COLORS[i]);
            if (distance < distanceOfClosestColor && colorSettingToggled.get(i).isSelected()) {
                closestColor = EmblemConstants.COLORS[i];
                distanceOfClosestColor = distance;
            }
        }

        return closestColor;
    }
    
    private double distanceFormula(Color pixelColor, Color emblemColor) {
        int redDistance = Math.abs(pixelColor.getRed() - emblemColor.getRed());
        int greenDistance = Math.abs(pixelColor.getGreen() - emblemColor.getGreen());
        int blueDistance = Math.abs(pixelColor.getBlue() - emblemColor.getBlue());

        double totalDistance = (redDistance*redDistance) + (greenDistance*greenDistance) + (blueDistance*blueDistance);
        return Math.sqrt(totalDistance);
    }
}
