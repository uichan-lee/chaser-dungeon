package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;

public class HUDTest {
    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 60;
    private static final int MENU_WIDTH = 56;
    private static final int MENU_HEIGHT = 70;
    private static final long seed = 56125848385792635L;

    public static void main(String[] args) {
        /**
         * 1. Open a small window for seed and window size options
         * 2. generate world
         */
        TERenderer renderer = new TERenderer();
        renderer.initialize(MENU_WIDTH, MENU_HEIGHT);

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.WHITE);

        // Draw box for menu items
        StdDraw.rectangle(
                (double) MENU_WIDTH / 2, // x: 28
                (double) MENU_HEIGHT / 2 - (MENU_HEIGHT * 0.05), // y: 32; Slightly below the center
                (double) MENU_WIDTH / 2 - (MENU_WIDTH * 0.1),    // halfWidth: 25; 6 empty on both sides
                (double) MENU_HEIGHT / 2 - (MENU_HEIGHT * 0.1)   // halfHeight: 28
        );

        // Title: "CS 61B: BYOW"
        Font titleFont = new Font("DialogInput", Font.BOLD, 70);
        StdDraw.setFont(titleFont);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.93, "CS 61B: BYOW");



        StdDraw.show();
    }
}
