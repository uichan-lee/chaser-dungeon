package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.*;

public class HUDTest {

    // Fonts
    public static final Font SMALL_WARNING_FONT = new Font("DialogInput", Font.BOLD, 20);
    public static final Font TITLE_FONT = new Font("DialogInput", Font.BOLD, 70);
    public static final Font MENU_FONT = new Font("DialogInput", Font.BOLD, 40);
    public static final Font SEED_FONT = new Font("DialogInput", Font.BOLD, 20);

    // World size constants
    private static final int SMALL_WIDTH = 60;
    private static final int SMALL_HEIGHT = 40;
    private static final int MEDIUM_WIDTH = 80;
    private static final int MEDIUM_HEIGHT = 50;
    private static final int BIG_WIDTH = 100;
    private static final int BIG_HEIGHT = 60;
    
    private static final int MENU_WIDTH = 56;
    private static final int MENU_HEIGHT = 50;
    private static final int SEED_MAXIMUM_LENGTH = 18;

    public static void main(String[] args) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(MENU_WIDTH, MENU_HEIGHT);

        drawTitle();
        drawMenu();
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
                    if (verifySeed(seed)) {
                        int[] worldSize = selectWorldSize();
                        if (worldSize != null) {
                            generateAndRenderWorld(seed, worldSize[0], worldSize[1]);
                        } else {
                            // Return to main menu if size selection was cancelled
                            StdDraw.clear(StdDraw.BLACK);
                            drawTitle();
                            drawMenu();
                            StdDraw.show();
                        }
                    } else {
                        // Return to main menu if verification failed
                        StdDraw.clear(StdDraw.BLACK);
                        drawTitle();
                        drawMenu();
                        StdDraw.show();
                    }
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
                    clearMessageArea();

                    // Print invalid message
                    addOrangeMessage("Invalid Input. Select from N/L/Q.");
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
        StdDraw.setFont(MENU_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.7, "Enter seed followed by S");
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
                if (sb.isEmpty()) {
                    // clear area
                    clearMessageArea();

                    // Print invalid message
                    addOrangeMessage("Seed cannot be empty.");
                    StdDraw.show();
                    continue;
                }

                String seed = sb.toString();
                return Long.parseLong(seed);
            } else if (sb.length() > SEED_MAXIMUM_LENGTH) { // seed maximum length exceeded
                clearMessageArea();

                // Print invalid message
                addOrangeMessage(String.format("Maximum length exceeded (%d).", SEED_MAXIMUM_LENGTH));
                StdDraw.show();
            } else if (!Character.isDigit(c)) { // If input is not a number (invalid input)
                // clear area
                clearMessageArea();

                // Print invalid message
                addOrangeMessage("Invalid Input. Type only numbers.");
                StdDraw.show();
            } else { // If c is a number (digit)


                sb.append(c);

                // clear area
                clearMessageArea();
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.4, (double) MENU_WIDTH * 0.35, 2);

                // Print input seed
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.setFont(SEED_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.4, sb.toString());
                StdDraw.show();
            }

        }
    }

    private static void clearMessageArea() {
        // clear area
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.1, (double) MENU_WIDTH * 0.35, 2);
    }

    private static void addOrangeMessage(String text) {
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.setFont(SMALL_WARNING_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.1, text);
    }

    private static void loadGame() {
        // TODO: Implement
    }

    /**
     * Verifies the seed input by displaying it and asking for confirmation.
     * 
     * @param seed the seed to verify
     * @return true if user confirms (Y), false if user cancels (N)
     */
    private static boolean verifySeed(long seed) {
        // Clear and redraw
        StdDraw.clear(StdDraw.BLACK);
        drawMenuBox();
        drawTitle();

        // Display seed verification message
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.setFont(MENU_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.7, "Seed: " + seed);
        
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.5, "Press Y to confirm");
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.3, "Press N to cancel");
        StdDraw.show();

        // Wait for Y or N input
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = StdDraw.nextKeyTyped();
            
            if (c == 'y' || c == 'Y') {
                return true;
            } else if (c == 'n' || c == 'N') {
                return false;
            } else {
                // Clear warning area
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.25, (double) MENU_WIDTH * 0.3, 2);
                
                // Print invalid message
                StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.setFont(SMALL_WARNING_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.25, "Invalid Input. Press Y or N.");
                StdDraw.show();
            }
        }
    }

    /**
     * Prompts the user to select a world size (Small, Medium, or Big).
     * 
     * @return an array [width, height] if a size is selected, null if cancelled
     */
    private static int[] selectWorldSize() {
        // Clear and redraw
        StdDraw.clear(StdDraw.BLACK);
        drawMenuBox();
        drawTitle();

        // Display world size selection menu
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(MENU_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.75, "Select World Size");
        
        StdDraw.setFont(SEED_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.6, "(S) Small: " + SMALL_WIDTH + "x" + SMALL_HEIGHT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.5, "(M) Medium: " + MEDIUM_WIDTH + "x" + MEDIUM_HEIGHT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.4, "(B) Big: " + BIG_WIDTH + "x" + BIG_HEIGHT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.25, "(N) Cancel");
        StdDraw.show();

        // Wait for S, M, B, or N input
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = StdDraw.nextKeyTyped();
            
            if (c == 's' || c == 'S') {
                return new int[]{SMALL_WIDTH, SMALL_HEIGHT};
            } else if (c == 'm' || c == 'M') {
                return new int[]{MEDIUM_WIDTH, MEDIUM_HEIGHT};
            } else if (c == 'b' || c == 'B') {
                return new int[]{BIG_WIDTH, BIG_HEIGHT};
            } else if (c == 'n' || c == 'N') {
                return null;
            } else {
                // Clear warning area
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.15, (double) MENU_WIDTH * 0.3, 2);
                
                // Print invalid message
                StdDraw.setPenColor(StdDraw.ORANGE);
                StdDraw.setFont(SMALL_WARNING_FONT);
                StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.15, "Invalid Input. Press S/M/B/N.");
                StdDraw.show();
            }
        }
    }

    /**
     * Generates a world using the given seed and renders it.
     * 
     * @param seed the seed for world generation
     * @param width the width of the world
     * @param height the height of the world
     */
    private static void generateAndRenderWorld(long seed, int width, int height) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(width, height);
        
        WorldGenerator gen = new WorldGenerator(width, height, seed);
        TETile[][] world = gen.generate();
        
        renderer.renderFrame(world);
    }

}
