package ui.gui;

import javax.swing.*;
import java.awt.*;

interface GuiComponent {
    int WIDTH = 1000;
    int HEIGHT = 700;
    Color WHITE = Color.white;

    // THEME 1
//    Color BACKGROUND = new Color(230, 235, 224);
//    Color MAIN_COLOUR = new Color(92, 164, 169);
//    Color MAIN_COLOUR_2 = new Color(155, 193, 188);
//    Color ACCENT_1 = new Color(244, 241, 187);
//    Color ACCENT_2 = new Color(237, 106, 90);

    // THEME 2
    Color BACKGROUND = new Color(235, 235, 235);
    Color MAIN_COLOUR = new Color(42, 157, 143);
    Color MAIN_COLOUR_2 = new Color(38, 70, 83);
    Color ACCENT_1 = new Color(233, 196, 106);
    Color ACCENT_2 = new Color(231, 111, 81);

    // THEME 3
//    Color BACKGROUND = new Color(17, 75, 95);
//    Color MAIN_COLOUR = new Color(2, 128, 144);
//    Color MAIN_COLOUR_2 = new Color(69, 105, 144);
//    Color ACCENT_1 = new Color(228, 253, 225);
//    Color ACCENT_2 = new Color(244, 91, 105);

    Color BUTTON_FG = WHITE;
    Color GREY = new Color(200,200,200);
    Font GENERAL_FONT = new Font("ARIAL", Font.PLAIN, 16);
    Font LARGE_BOLD_FONT = new Font("ARIAL", Font.BOLD, 24);
    Font LARGE_FONT = new Font("ARIAL", Font.PLAIN, 24);
    Font MEDIUM_FONT = new Font("ARIAL", Font.PLAIN, 20);

    Color LARGE_BTN_COLOUR = MAIN_COLOUR;
    Font LARGE_BTN_FONT = LARGE_BOLD_FONT;
    Color SMALL_BTN_COLOUR = MAIN_COLOUR_2;
    Font SMALL_BTN_FONT = MEDIUM_FONT;

    int MARGIN = 10;
}
