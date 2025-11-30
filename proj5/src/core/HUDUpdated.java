package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;

public class HUD {

    public static void drawHUD(TETile[][] world) {
        // Mouse position
        double mx = StdDraw.mouseX();
        double my = StdDraw.mouseY();

        int x = (int) mx;
        int y = (int) my;

        String info = "";

        if (x >= 0 && x < world.length && y >= 0 && y < world[0].length) {
            info = world[x][y].description();
        } else {
            info = "";
        }

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 16));
        StdDraw.textLeft(1, world[0].length - 1, info);
        StdDraw.show();
    }
}

