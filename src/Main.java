// Mario Kart DS Emblem Creator by Matt McCullough
// This is to create custom emblems in Mario Kart DS

import ui.MarioKartDSEmblemCreatorUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MarioKartDSEmblemCreatorUI marioKartDSEmblemCreatorUI = new MarioKartDSEmblemCreatorUI();
        marioKartDSEmblemCreatorUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        marioKartDSEmblemCreatorUI.pack();
        marioKartDSEmblemCreatorUI.setVisible(true);
    }
}