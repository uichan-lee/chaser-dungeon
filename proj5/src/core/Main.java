package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class Main {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;

    public static void main(String[] args) {
        StdDraw.setCanvasSize(WIDTH * 10, HEIGHT * 10);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        while (true) {
            drawMenu();
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (c == 'N') startNewGame();
                if (c == 'L') loadOldGame();
                if (c == 'Q') System.exit(0);
            }
            StdDraw.show();
        }
    }

    private static void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 40));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "CS61B: BYOW");

        StdDraw.setFont(new Font("Monaco", Font.PLAIN, 28));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 5, "(N) New Game");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 12, "(L) Load Game");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 19, "(Q) Quit");
    }

    private static void startNewGame() {
        long seed = getSeedInput();
        WorldGenerator wg = new WorldGenerator(WIDTH, HEIGHT, seed);
        TETile[][] world = wg.generate();

        Point start = findAvatar(world);
        Player p = new Player(start.x, start.y);

        SaveLoad.save(world, p, null); // No chaser in Main.java

        new GameLoop().run(world, p);
    }

    private static void loadOldGame() {
        SaveLoad.SaveState state = SaveLoad.load();
        if (state == null) return;
        new GameLoop().run(state.world, state.player);
    }

    private static long getSeedInput() {
        StringBuilder sb = new StringBuilder();

        while (true) {
            drawSeedScreen(sb.toString());
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) sb.append(c);
                if (c == 'S' || c == 's') break;
            }
            StdDraw.show();
        }
        return Long.parseLong(sb.toString());
    }

    private static void drawSeedScreen(String sofar) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 32));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 + 5, "Enter Seed:");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 5, sofar);
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 15, "Press S to Start");
    }

    private static Point findAvatar(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.AVATAR) {
                    return new Point(x, y);
                }
            }
        }
        throw new RuntimeException("Avatar not found");
    }
}