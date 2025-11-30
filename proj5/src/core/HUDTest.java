package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;

public class HUDTest {

    // Fonts
    public static final Font SMALL_WARNING_FONT = new Font("DialogInput", Font.BOLD, 20);
    public static final Font TITLE_FONT = new Font("DialogInput", Font.BOLD, 70);
    public static final Font MENU_FONT = new Font("DialogInput", Font.BOLD, 50);
    public static final Font SEED_FONT = new Font("DialogInput", Font.BOLD, 20);

    private static final int WORLD_WIDTH = 100;
    private static final int WORLD_HEIGHT = 60;
    private static final int MENU_WIDTH = 56;
    private static final int MENU_HEIGHT = 70;
    private static final int SEED_MAXIMUM_LENGTH = 30;

    private final long seed = 56125848385792635L;

    public static void main(String[] args) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(MENU_WIDTH, MENU_HEIGHT);   // Initialize world

        drawTitle();    // Draw title
        drawMenu();     // Draw Menu (square + options)
        StdDraw.show();

        mainMenu();

    }

    /**
     * mainMenu method is expected to end up with calling either
     * 1. getSeedInput(): Generate new world with seed input
     * 2. loadGame(): Load saved world
     * 3. exit (terminate program)
     */
    private static void mainMenu() {
        char c = '.';   // input character
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            c = StdDraw.nextKeyTyped(); // read next key
            System.out.println("Selected option: " + c);

            switch (c) {
                case 'n':
                case 'N':
                    long seed = getSeedInput();
                    generateAndRenderWorld(seed);
                    break;
                case 'l':
                case 'L':
                    loadGame();
                    break;
                case 'q':
                case 'Q':
                    System.exit(0);
                    break;
                default:
                    // clear area
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, (double) MENU_WIDTH / 2, 2);

                    // Print invalid message
                    StdDraw.setPenColor(StdDraw.ORANGE);
                    StdDraw.setFont(SMALL_WARNING_FONT);
                    StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, "Invalid Input. Select from N/L/Q.");
                    StdDraw.show();
            }
        }
    }

    private static void drawTitle() {
        // Title: "CS 61B: BYOW"
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.setFont(TITLE_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.93, "CS 61B: BYOW");
    }


    private static void drawMenu() {
        drawMenuBox();

        // Menu items
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(MENU_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.7, "(N) New Game");
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.45, "(L) Load Game");
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.2, "(Q) Quit Game");
    }

    private static void drawMenuBox() {
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.WHITE);

        // Draw box for menu items
        StdDraw.rectangle(
                (double) MENU_WIDTH / 2, // x: 28
                (double) MENU_HEIGHT / 2 - (MENU_HEIGHT * 0.05), // y: 32; Slightly below the center
                (double) MENU_WIDTH / 2 - (MENU_WIDTH * 0.1),    // halfWidth: 25; 6 empty on both sides
                (double) MENU_HEIGHT / 2 - (MENU_HEIGHT * 0.1)   // halfHeight: 28
        );
    }

    private static long getSeedInput() {
        // Clear and redraw the box, and display new texts
        StdDraw.clear(StdDraw.BLACK);
        drawMenuBox();
        drawTitle();

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.7, "Enter seed");
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.6, "followed by S");
        StdDraw.show();

        StdDraw.setPenColor(StdDraw.YELLOW);

        char c;
        StringBuilder sb = new StringBuilder();

        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            c = StdDraw.nextKeyTyped(); // read next key

            // Return seed if the input is s or S
            if (c == 's' || c == 'S') {
                String seed = sb.toString();
                return Long.parseLong(seed);
            } else if (sb.length() > SEED_MAXIMUM_LENGTH) { // seed maximum length exceeded
                // clear area
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, (double) MENU_WIDTH / 2, 2);

                // Print invalid message
                StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.setFont(SMALL_WARNING_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, "Maximum seed length reached (30).");
                StdDraw.show();
                continue;
            } else if (!Character.isDigit(c)) { // If input is not a number (invalid input)
                // clear area
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, (double) MENU_WIDTH / 2, 2);

                // Print invalid message
                StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.setFont(SMALL_WARNING_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, "Invalid Input. Type only numbers.");
                StdDraw.show();
                continue;
            } else { // If c is a number (digit)


                sb.append(c);

                // clear area
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.88, (double) MENU_WIDTH / 2, 2);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.4, (double) MENU_WIDTH * 0.35, 2);

                // Print input seed
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.setFont(SEED_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.4, sb.toString());
                StdDraw.show();
                continue;
            }

        }
    }

    private static void loadGame() {
        // TODO: Implement
    }

    /**
     * Generates a world using the given seed and renders it.
     * 
     * @param seed the seed for world generation
     */
    private static void generateAndRenderWorld(long seed) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(WORLD_WIDTH, WORLD_HEIGHT);
        
        WorldGenerator gen = new WorldGenerator(WORLD_WIDTH, WORLD_HEIGHT, seed);
        TETile[][] world = gen.generate();
        
        renderer.renderFrame(world);
    }

}
