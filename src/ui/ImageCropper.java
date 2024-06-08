package ui;

import io.EmblemCreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageCropper extends JFrame implements MouseListener {

    private JLabel imageToCrop;
    private Container container;
    private int clickCounter = 0;
    private int[] coordinates = new int[4];
    private String imagePath = "";
    private ArrayList<JCheckBox> colorSettingToggled;
    private JComboBox transparentBackgroundColor;

    public ImageCropper(String imagePath, ArrayList<JCheckBox> colorSettingToggled, JComboBox transparentBackgroundColor) {
        setTitle("Image Cropper");

        this.colorSettingToggled = colorSettingToggled;
        this.transparentBackgroundColor = transparentBackgroundColor;

        this.container = getContentPane();
        container.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon(imagePath);
        this.imagePath = imagePath;

        imageToCrop = new JLabel();
        imageToCrop.setIcon(imageIcon);
        imageToCrop.setText("");

        JPanel jPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1, 1);
        jPanel.setLayout(gridLayout);
        jPanel.add(imageToCrop);
        imageToCrop.addMouseListener(this);

        JScrollPane jScrollPane = new JScrollPane(jPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.add(jScrollPane);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (clickCounter == 0) {
            coordinates[0] = e.getX();
            coordinates[1] = e.getY();
            JOptionPane.showMessageDialog(this, "First click detected! Now click the bottom right of your image!");
            clickCounter++;
        }
        else {
            JOptionPane.showMessageDialog(this, "Second click detected!");
            coordinates[2] = e.getX();
            coordinates[3] = e.getY();
            if (isValidCoordinates()) {

                BufferedImage emblemImage;
                try {
                    emblemImage = ImageIO.read(new File(imagePath));
                }
                catch (Exception ex) {
                    return;
                }

                saveCroppedEmblem(emblemImage);
                setVisible(false);
            }
            else {
                JOptionPane.showMessageDialog(this, "Invalid Crop! Please try again!");
                coordinates = new int[4];
                clickCounter = 0;
            }
        }
    }

    private void saveCroppedEmblem(BufferedImage croppedImage) {
        int croppedImageWidth = coordinates[2] - coordinates[0];
        int croppedImageHeight = coordinates[3] - coordinates[1];

        BufferedImage copyOfCroppedImage = copyCroppedImage(croppedImage, croppedImageWidth, croppedImageHeight);

        EmblemCreator emblemCreator = new EmblemCreator();
        BufferedImage emblemImage = emblemCreator.createEmblem(copyOfCroppedImage, colorSettingToggled, transparentBackgroundColor);

        if (emblemImage != null) {
            imagePath = imagePath.substring(0, imagePath.lastIndexOf("."));
            File imageFile = new File(imagePath + "Emblem.png");
            try {
                ImageIO.write(emblemImage, "png", imageFile); //this is where the image is saved to
                JOptionPane.showMessageDialog(this, "Your Emblem has been successfully created");
            }
            catch (Exception ex) {
                return;
            }
        }
    }

    private BufferedImage copyCroppedImage(BufferedImage croppedImage, int croppedImageWidth, int croppedImageHeight) {
        //copy cropped image, so the original isn't modified
        croppedImage = croppedImage.getSubimage(coordinates[0], coordinates[1], croppedImageWidth, croppedImageHeight);
        BufferedImage copyOfImage = new BufferedImage(croppedImage.getWidth(), croppedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(croppedImage, 0, 0, null);
        return copyOfImage;
    }

    private boolean isValidCoordinates() {
        //valid coordinates if the second click's position is farther right and down than the first click
        return coordinates[2] > coordinates[0] && coordinates[3] > coordinates[1];
    }

    //ignore these methods
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}