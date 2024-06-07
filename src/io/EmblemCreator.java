package io;

import constants.EmblemColors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EmblemCreator {

    private static final int PIXELS = 32;

    public BufferedImage createEmblem(String imagePath) {
        BufferedImage sourceImage = null; //QR code image
        BufferedImage emblemImage = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB); //new image


        try {
            sourceImage = ImageIO.read(new File(imagePath));
        }
        catch(Exception e)
        {
            //already handled it below
        }

        if (sourceImage == null) {
            //this is if the image couldn't be read
            return null;
        }

        sourceImage = removeTransparentBackground(sourceImage);
        return createEmblemImage(emblemImage, sourceImage);
    }

    public BufferedImage createEmblem(BufferedImage bufferedImage) {
        BufferedImage emblemImage = new BufferedImage(PIXELS, PIXELS, BufferedImage.TYPE_INT_ARGB); //new image

        if (bufferedImage == null) {
            //this is if the image couldn't be read
            return null;
        }

        bufferedImage = removeTransparentBackground(bufferedImage);
        return createEmblemImage(emblemImage, bufferedImage);
    }

    private BufferedImage removeTransparentBackground(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_ARGB); //new image
        Graphics2D g2d = newImage.createGraphics();
        g2d.setPaint(EmblemColors.WHITE);
        g2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        g2d.drawImage(image, 0, 0, null);
        return newImage;
    }

    private BufferedImage createEmblemImage(BufferedImage emblemImage, BufferedImage sourceImage) {
        Graphics2D g2d = emblemImage.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, PIXELS, PIXELS);
        sourceImage = resize(sourceImage);

        for (int i=0; i<PIXELS; i++) {
            for (int j=0; j<PIXELS; j++) {
                int rgb = sourceImage.getRGB(i,j);
                Color pixelColor = new Color(rgb);
                g2d.setPaint(getColorForEmblemPixel(pixelColor));
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

    private Color getColorForEmblemPixel(Color pixelColor) {
        Color closestColor = null;
        double distanceOfClosestColor = Double.MAX_VALUE;
        
        for (int i=0; i<EmblemColors.COLORS.length; i++) {
            double distance = distanceFormula(pixelColor, EmblemColors.COLORS[i]);
            if (distance < distanceOfClosestColor) {
                closestColor = EmblemColors.COLORS[i];
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
