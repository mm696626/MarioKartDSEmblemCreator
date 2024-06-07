package ui;

import io.EmblemCreator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class MarioKartDSEmblemCreatorUI extends JFrame implements ActionListener {


    //where to get the image from
    private String imagePath = "";

    private JButton generateEmblem, cropAndGenerateEmblem;

    GridBagConstraints gridBagConstraints = null;

    public MarioKartDSEmblemCreatorUI()
    {
        setTitle("Mario Kart DS Emblem Creator");

        generateEmblem = new JButton("Select Image for Emblem");
        generateEmblem.addActionListener(this);

        cropAndGenerateEmblem = new JButton("Crop Image and Create Emblem");
        cropAndGenerateEmblem.addActionListener(this);


        setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        add(generateEmblem, gridBagConstraints);

        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=0;
        add(cropAndGenerateEmblem, gridBagConstraints);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == generateEmblem) {

            JFileChooser fileChooser = new JFileChooser();
            FileFilter imageFileFilter = new FileNameExtensionFilter("Image File","jpg", "jpeg", "png", "JPG", "JPEG", "PNG");
            fileChooser.setFileFilter(imageFileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                imagePath = fileChooser.getSelectedFile().getAbsolutePath();
            } else {
                return;
            }

            EmblemCreator emblemCreator = new EmblemCreator();
            BufferedImage emblem = emblemCreator.createEmblem(imagePath);

            File emblemFile = new File("emblem.png");

            try {
                ImageIO.write(emblem, "png", emblemFile);
            }
            catch (Exception ex) {
                return;
            }
        }

        if (e.getSource() == cropAndGenerateEmblem) {


        }
    }
}