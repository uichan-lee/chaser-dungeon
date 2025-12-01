package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class HUDTest {
    // Fonts
    public static final Font SMALL_WARNING_FONT = new Font("DialogInput", Font.BOLD, 20);
    public static final Font TITLE_FONT = new Font("DialogInput", Font.BOLD, 70);
    public static final Font MENU_FONT = new Font("DialogInput", Font.BOLD, 40);
    public static final Font SEED_FONT = new Font("DialogInput", Font.PLAIN, 20);
    public static final Font MINI_FONT = new Font("DialogInput", Font.ITALIC, 12);
    public static final Font HUD_FONT = new Font("DialogInput", Font.PLAIN, 14);
    public static final Font HUD_TAG_FONT = new Font("DialogInput", Font.PLAIN, 10);

    // World size constants
    private static final int SMALL_WIDTH = 50;
    private static final int SMALL_HEIGHT = 30;

    private static final int MEDIUM_WIDTH = 80;
    private static final int MEDIUM_HEIGHT = 50;

    private static final int BIG_WIDTH = 110;
    private static final int BIG_HEIGHT = 70;

    private static int SIZE; //0 = Small; 1 = Medium; 2 = Big

    
    private static final int MENU_WIDTH = 56;
    private static final int MENU_HEIGHT = 50;
    private static final int SEED_MAXIMUM_LENGTH = 18;

//    // Tile under Player. Used to render after player move.
//    private static TETile playerPositionTile = Tileset.FLOOR;

    // world renderer
    private static final TERenderer renderer = new TERenderer();



    public static void main(String[] args) {
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
        StdDraw.filledRectangle((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.8, (double) MENU_WIDTH * 0.35, 2);
    }

    private static void addOrangeMessage(String text) {
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.setFont(SMALL_WARNING_FONT);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.8, text);
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
        
        StdDraw.setFont(MINI_FONT);
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.47, "Recommended for most devices");
        StdDraw.text((double) MENU_WIDTH / 2, MENU_HEIGHT * 0.37, "Recommended for large monitors");
        
        StdDraw.show();

        // Wait for S, M, B, or N input
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char c = StdDraw.nextKeyTyped();
            
            if (c == 's' || c == 'S') {
                SIZE = 0;
                return new int[]{SMALL_WIDTH, SMALL_HEIGHT};
            } else if (c == 'm' || c == 'M') {
                SIZE = 1;
                return new int[]{MEDIUM_WIDTH, MEDIUM_HEIGHT};
            } else if (c == 'b' || c == 'B') {
                SIZE = 2;
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
     * After rendering,
     * 
     * @param seed the seed for world generation
     * @param width the width of the world
     * @param height the height of the world
     */
    private static void generateAndRenderWorld(long seed, int width, int height) {
        renderer.initialize(width, height);
        
        WorldGenerator gen = new WorldGenerator(width, height, seed);
        TETile[][] world = gen.generate();
        
        // Find avatar position and create Player
        Player player = findPlayer(world);
        
        renderer.renderFrame(world);

        runGameLoop(world, player);
    }
    
    /**
     * Finds the avatar in the world and creates a Player object at that position.
     */
    private static Player findPlayer(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    return new Player(x, y);
                }
            }
        }
        throw new RuntimeException("Avatar not found in world");
    }


    /**
     * Main game loop: handles player movement and updates HUD based on mouse position
     *
     * @param world the world tile map
     * @param player the player object
     */
    private static void runGameLoop(TETile[][] world, Player player) {
        int worldWidth;
        int worldHeight;

        if (SIZE == 0) {
            worldWidth = SMALL_WIDTH;
            worldHeight = SMALL_HEIGHT;
        } else if (SIZE == 1) {
            worldWidth = MEDIUM_WIDTH;
            worldHeight = MEDIUM_HEIGHT;
        } else {
            worldWidth = BIG_WIDTH;
            worldHeight = BIG_HEIGHT;
        }

        // Initial HUD background
        clearHUDArea(worldWidth, worldHeight);
        StdDraw.show();

        // Main loop: handle input, update world, render, and update HUD
        while (true) {
            // Handle keyboard input for player movement
            handleInput(world, player);
            
            // Clear screen and render everything in one batch
            StdDraw.clear(StdDraw.BLACK);
            
            // Render the world tiles (without clearing/showing)
            renderer.drawTiles(world);
            
            // Update HUD based on mouse position (without showing)
            updateHUDWithMouse(world, worldWidth, worldHeight, false);
            
            // Show everything at once
            StdDraw.show();
            
            StdDraw.pause(30); // ~30 FPS, avoids busy-waiting
        }
    }
    
    /**
     * Handles keyboard input for player movement (W/A/S/D).
     * Based on GameLoop.handleInput logic.
     */
    private static void handleInput(TETile[][] world, Player player) {
        if (!StdDraw.hasNextKeyTyped()) {
            return;
        }
        
        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        switch (c) {
            case 'W':
                movePlayer(player, Direction.UP, world);
                break;
            case 'A':
                movePlayer(player, Direction.LEFT, world);
                break;
            case 'S':
                movePlayer(player, Direction.DOWN, world);
                break;
            case 'D':
                movePlayer(player, Direction.RIGHT, world);
                break;
        }
    }
    
    /**
     * Moves the player in the specified direction if the target tile is walkable.
     * Based on GameLoop.move logic.
     */
    private static void movePlayer(Player player, Direction dir, TETile[][] world) {
        player.facing = dir;
        
        int nx = player.pos.x + dir.dx;
        int ny = player.pos.y + dir.dy;
        
        // Check bounds
        if (nx < 0 || nx >= world.length || ny < 0 || ny >= world[0].length) {
            return;
        }
        
        // Allowed floor types (same as GameLoop.move)
        if (world[nx][ny].equals(Tileset.FLOOR) || world[nx][ny].equals(Tileset.UNLOCKED_DOOR)) {
            // Clear old location
            world[player.pos.x][player.pos.y] = Tileset.FLOOR;
            
            // Move player
            player.pos = new java.awt.Point(nx, ny);
            
            // Place avatar tile
            world[nx][ny] = Tileset.AVATAR;
        }
    }

    /**
     * Draws the HUD area and shows information about the tile currently under the mouse cursor (if any).
     * 
     * @param world the world tile map
     * @param worldWidth width of the world
     * @param worldHeight height of the world
     * @param showNow if true, calls StdDraw.show() at the end; if false, just draws without showing
     */
    private static void updateHUDWithMouse(TETile[][] world, int worldWidth, int worldHeight, boolean showNow) {
        // Redraw HUD background
        clearHUDArea(worldWidth, worldHeight);

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        int tileX = (int) Math.floor(mouseX);
        int tileY = (int) Math.floor(mouseY);

        // Only show info if mouse is within world bounds and not in HUD area
        if (tileX >= 0 && tileX < worldWidth && tileY >= 0 && tileY < worldHeight - 2) {
            TETile tile = world[tileX][tileY];
            if (tile != null) {
                String name = getFriendlyTileName(tile);
                String tag = getTileTag(tile);

                StdDraw.setPenColor(StdDraw.WHITE);

                // Main tile name, slightly above center of HUD bar
                StdDraw.setFont(HUD_FONT);
                StdDraw.text(worldWidth / 2.0, worldHeight - 0.9, name);

                // Smaller tag text just below the name
                if (!tag.isEmpty()) {
                    StdDraw.setFont(HUD_TAG_FONT);
                    StdDraw.text(worldWidth / 2.0, worldHeight - 1.5, "(" + tag + ")");
                }
            }
        }

        if (showNow) {
            StdDraw.show();
        }
    }

    private static void clearHUDArea(int worldWidth, int worldHeight) {
        // HUD bar exactly matches the non-playable HUD area (height = 2 tiles)
        StdDraw.setPenColor(52, 61, 82);
        StdDraw.filledRectangle(worldWidth / 2.0, worldHeight - 1,
                worldWidth / 2.0, 1);
    }

    /**
     * Converts a TETile into a short, user-friendly name for the HUD.
     */
    private static String getFriendlyTileName(TETile tile) {
        if (tile == null) {
            return "";
        }

        // Prefer matching against known tiles so we can control capitalization.
        if (tile.equals(Tileset.FLOOR)) {
            return "Floor";
        } else if (tile.equals(Tileset.GRASS)) {
            return "Grass";
        } else if (tile.equals(Tileset.FLOWER)) {
            return "Flower";
        } else if (tile.equals(Tileset.SAND)) {
            return "Sand";
        } else if (tile.equals(Tileset.SNOW)) {
            return "Snow";
        } else if (tile.equals(Tileset.WALL)) {
            return "Wall";
        } else if (tile.equals(Tileset.NOTHING)) {
            return "Nothing";
        } else if (tile.equals(Tileset.WATER)) {
            return "Water";
        } else if (tile.equals(Tileset.MOUNTAIN)) {
            return "Mountain";
        } else if (tile.equals(Tileset.TREE)) {
            return "Tree";
        } else if (tile.equals(Tileset.LOCKED_DOOR)) {
            return "Locked Door";
        } else if (tile.equals(Tileset.UNLOCKED_DOOR)) {
            return "Unlocked Door";
        } else if (tile.equals(Tileset.CELL)) {
            return "Cell";
        } else if (tile.equals(Tileset.BUSH)) {
            return "Bush";
        } else if (tile.equals(Tileset.STATUE)) {
            return "Statue";
        } else if (tile.equals(Tileset.CRATE)) {
            return "Crate";
        } else if (tile.equals(Tileset.BOOKSHELF)) {
            return "Bookshelf";
        } else if (tile.equals(Tileset.SNOWMAN)) {
            return "Snowman";
        } else if (tile.equals(Tileset.TREASURE)) {
            return "Treasure";
        } else if (tile.equals(Tileset.PORTAL)) {
            return "Portal";
        } else if (tile.equals(Tileset.LAVA)) {
            return "Lava";
        } else if (tile.equals(Tileset.SPIKE)) {
            return "Spike";
        } else if (tile.equals(Tileset.AVATAR)) {
            return "Player";
        }

        // Fallback: use description() with first letter capitalized
        String desc = tile.description();
        if (desc == null || desc.isEmpty()) {
            return "";
        }
        return desc.substring(0, 1).toUpperCase() + desc.substring(1);
    }

    /**
     * Returns a short category tag for the given tile, e.g. "dangerous", "interactable",
     * "unwalkable", etc. Used for the tiny HUD text under the tile name.
     * 
     * Categories follow WorldGenerator's logic for isWalkableTile/isBlockingTile,
     * tagging as "dangerous", "interactable", or "unwalkable".
     */
    private static String getTileTag(TETile tile) {
        if (tile == null) {
            return "";
        }

        // Dangerous tiles (highest priority)
        if (tile.equals(Tileset.LAVA) || tile.equals(Tileset.SPIKE)) {
            return "dangerous";
        }

        // Interactable tiles
        if (tile.equals(Tileset.TREASURE)
                || tile.equals(Tileset.PORTAL)
                || tile.equals(Tileset.LOCKED_DOOR)
                || tile.equals(Tileset.UNLOCKED_DOOR)) {
            return "interactable";
        }

        // Unwalkable tiles (matches WorldGenerator.isBlockingTile)
        if (tile.equals(Tileset.AVATAR)
                || tile.equals(Tileset.WALL)
                || tile.equals(Tileset.WATER)
                || tile.equals(Tileset.LOCKED_DOOR)
                || tile.equals(Tileset.UNLOCKED_DOOR)
                || tile.equals(Tileset.MOUNTAIN)
                || tile.equals(Tileset.TREE)) {
            return "unwalkable";
        }

        return "";
    }

}
