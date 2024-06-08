package ui;

import constants.EmblemConstants;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MarioKartDSEmblemCreatorUI extends JFrame implements ActionListener {


    //where to get the image from
    private String imagePath = "";
    private ArrayList<JLabel> colorSettingsLabels;
    private ArrayList<JCheckBox> colorSettingToggled;
    private JButton generateEmblem, cropAndGenerateEmblem, disableAllColors, enableAllColors;
    private JComboBox transparentBackgroundColor;

    public MarioKartDSEmblemCreatorUI()
    {
        setTitle("Mario Kart DS Emblem Creator");
        generateUI();

        File colorSettings = new File("colorSettings.txt");
        if (colorSettings.exists()) {
            loadSettingsOnStartUp();
        }
    }

    private void generateUI() {

        colorSettingsLabels = new ArrayList<>();
        colorSettingToggled = new ArrayList<>();

        JPanel mainMenuPanel = new JPanel();
        GridLayout mainMenuGridLayout = new GridLayout(2, 1);
        mainMenuPanel.setLayout(mainMenuGridLayout);

        JPanel colorSettingsPanel = new JPanel();
        GridLayout colorSettingsGridLayout = new GridLayout(EmblemConstants.COLORS.length + 2,2);
        colorSettingsPanel.setLayout(colorSettingsGridLayout);

        generateEmblem = new JButton("Select Image for Emblem");
        generateEmblem.addActionListener(this);
        mainMenuPanel.add(generateEmblem);

        cropAndGenerateEmblem = new JButton("Crop Image and Create Emblem");
        cropAndGenerateEmblem.addActionListener(this);
        mainMenuPanel.add(cropAndGenerateEmblem);


        for (int i=0; i<EmblemConstants.COLORS.length; i++) {
            JLabel jLabel = new JLabel(EmblemConstants.COLOR_NAMES[i]);
            colorSettingsLabels.add(jLabel);
            colorSettingsPanel.add(colorSettingsLabels.get(i));
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setSelected(true);
            colorSettingToggled.add(jCheckBox);
            colorSettingToggled.get(i).addActionListener(e -> updateColorSettings());
            colorSettingsPanel.add(colorSettingToggled.get(i));
        }

        JLabel transparentBackgroundColorLabel = new JLabel("Transparent Background Color");
        colorSettingsPanel.add(transparentBackgroundColorLabel);

        transparentBackgroundColor = new JComboBox<>(EmblemConstants.COLOR_NAMES);
        transparentBackgroundColor.setSelectedIndex(15);
        transparentBackgroundColor.addActionListener(e -> updateColorSettings());
        colorSettingsPanel.add(transparentBackgroundColor);

        enableAllColors = new JButton("Enable All Colors");
        enableAllColors.addActionListener(this);
        colorSettingsPanel.add(enableAllColors);

        disableAllColors = new JButton("Disable All Colors");
        disableAllColors.addActionListener(this);
        colorSettingsPanel.add(disableAllColors);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Create Emblem", mainMenuPanel);
        tabbedPane.add("Change Color Settings", colorSettingsPanel);
        add(tabbedPane);
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
            BufferedImage emblem = emblemCreator.createEmblem(imagePath, colorSettingToggled, transparentBackgroundColor);

            imagePath = imagePath.substring(0, imagePath.lastIndexOf("."));
            File emblemFile = new File(imagePath + "Emblem.png");

            try {
                ImageIO.write(emblem, "png", emblemFile);
                JOptionPane.showMessageDialog(this, "Your Emblem has been successfully created");
            }
            catch (Exception ex) {
                return;
            }
        }

        if (e.getSource() == cropAndGenerateEmblem) {
            JFileChooser fileChooser = new JFileChooser();
            FileFilter imageFileFilter = new FileNameExtensionFilter("Image File","jpg", "jpeg", "png", "JPG", "JPEG", "PNG");
            fileChooser.setFileFilter(imageFileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Please click the top left of your image");

                imagePath = fileChooser.getSelectedFile().getAbsolutePath();
                ImageCropper imageCropper = new ImageCropper(imagePath, colorSettingToggled, transparentBackgroundColor);
                imageCropper.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                imageCropper.pack();
                imageCropper.setVisible(true);
            } else {
                return;
            }

        }

        if (e.getSource() == disableAllColors) {
            for (int i=0; i<colorSettingToggled.size(); i++) {
                colorSettingToggled.get(i).setSelected(false);
                updateColorSettings();
            }
        }

        if (e.getSource() == enableAllColors) {
            for (int i=0; i<colorSettingToggled.size(); i++) {
                colorSettingToggled.get(i).setSelected(true);
                updateColorSettings();
            }
        }
    }

    private void updateColorSettings() {
        PrintWriter outputStream = null;

        try {
            outputStream = new PrintWriter( new FileOutputStream("colorSettings.txt"));
        }
        catch (FileNotFoundException f) {
            System.out.println("File does not exist");
            System.exit(0);
        }

        for (int i=0; i<EmblemConstants.COLORS.length; i++) {
            outputStream.println(EmblemConstants.COLOR_NAMES[i] + "=" + colorSettingToggled.get(i).isSelected());
        }

        outputStream.println("Transparent Background Color" + "=" + EmblemConstants.COLOR_NAMES[transparentBackgroundColor.getSelectedIndex()]);
        outputStream.close();
    }

    private void loadSettingsOnStartUp() {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream("colorSettings.txt"));
        } catch (FileNotFoundException e) {
            return;
        }

        int index = 0;

        while (inputStream.hasNextLine()) {
            String line = inputStream.nextLine();

            if (index < EmblemConstants.COLORS.length && line.contains(EmblemConstants.COLOR_NAMES[index])) {
                String settingValue = line.split("=")[1];
                colorSettingToggled.get(index).setSelected(Boolean.parseBoolean(settingValue));
                index++;
            }

            else if (line.contains("Transparent Background Color")) {
                String settingValue = line.split("=")[1];
                for (int i=0; i<EmblemConstants.COLOR_NAMES.length; i++) {
                    if (settingValue.equals(EmblemConstants.COLOR_NAMES[i])) {
                        transparentBackgroundColor.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        inputStream.close();
    }
}