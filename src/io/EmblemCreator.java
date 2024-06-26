package io;

import constants.EmblemConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class EmblemCreator {

    private static final int PIXELS = 32;
    private int backgroundColorIndex;
    private int[][] pixelColors = new int[PIXELS][PIXELS];

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
        backgroundColorIndex = transparentBackgroundColor.getSelectedIndex();

        Graphics2D g2d = newImage.createGraphics();

        if (!EmblemConstants.COLOR_NAMES[backgroundColorIndex].equals("No Background Color")) {
            g2d.setPaint(EmblemConstants.COLORS[backgroundColorIndex]);
            g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        }

        g2d.drawImage(image, 0, 0, null);

        return newImage;
    }

    private BufferedImage createEmblemImage(BufferedImage emblemImage, BufferedImage sourceImage, ArrayList<JCheckBox> colorSettingToggled) {
        Graphics2D g2d = emblemImage.createGraphics();
        sourceImage = resize(sourceImage);

        for (int i=0; i<PIXELS; i++) {
            for (int j=0; j<PIXELS; j++) {
                int rgb = sourceImage.getRGB(i,j);
                Color pixelColor = new Color(rgb, true);
                Color emblemPixelColor = getColorForEmblemPixel(pixelColor, colorSettingToggled, i, j);

                if (emblemPixelColor != null) {
                    g2d.setPaint(emblemPixelColor);
                    g2d.fillRect(i, j, 1, 1);
                }
                else {
                    pixelColors[i][j] = 15;
                }
            }
        }

        writePixelColorsToFile();
        return emblemImage;
    }

    private void writePixelColorsToFile() {
        PrintWriter outputStream = null;

        try {
            outputStream = new PrintWriter( new FileOutputStream("emblemPixelColors.txt"));
        }
        catch (FileNotFoundException f) {
            System.out.println("File does not exist");
            System.exit(0);
        }

        for (int i=0; i<EmblemConstants.COLOR_NAMES.length; i++) {
            outputStream.println(i + "=" + EmblemConstants.COLOR_NAMES[i]);
        }

        outputStream.println();

        for (int i=0; i<PIXELS; i++) {
            for (int j=0; j<PIXELS; j++) {
                int pixelColor = pixelColors[j][i];
                if (pixelColor > 9) {
                    outputStream.print(pixelColor + " ");
                }
                else {
                    outputStream.print(pixelColor + "  ");
                }
            }
            outputStream.println();
        }

        outputStream.close();
    }

    private static BufferedImage resize(BufferedImage img) {
        Image tmp = img.getScaledInstance(PIXELS, PIXELS, Image.SCALE_DEFAULT);
        BufferedImage resized = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    private Color getColorForEmblemPixel(Color pixelColor, ArrayList<JCheckBox> colorSettingToggled, int row, int column) {
        Color closestColor = null;
        double distanceOfClosestColor = Double.MAX_VALUE;

        if (pixelColor.getAlpha() == 0) {
            return null;
        }
        
        for (int i = 0; i<EmblemConstants.COLORS.length; i++) {
            double distance = distanceFormula(pixelColor, EmblemConstants.COLORS[i]);
            if (distance < distanceOfClosestColor && colorSettingToggled.get(i).isSelected()) {
                closestColor = EmblemConstants.COLORS[i];
                distanceOfClosestColor = distance;
                pixelColors[row][column] = i;
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
