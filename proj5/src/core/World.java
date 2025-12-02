package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class World {
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

    private static int SIZE; // 0 = Small; 1 = Medium; 2 = Big

    private static final int MENU_WIDTH = 56;
    private static final int MENU_HEIGHT = 50;
    private static final int SEED_MAXIMUM_LENGTH = 18;

    // world renderer
    private static final TERenderer renderer = new TERenderer();
    
    // Flag to track if we're waiting for Q after pressing :
    private static boolean waitingForQuit = false;

    /**
     * Main entry point for the game.
     * Can be called directly or through Main.main().
     */
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
        char c = '.'; // input character
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            c = StdDraw.nextKeyTyped(); // read next key

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
                (double) MENU_WIDTH / 2 - (MENU_WIDTH * 0.1), // halfWidth: 25; 6 empty on both sides
                (double) MENU_HEIGHT / 2 - (MENU_HEIGHT * 0.1) // halfHeight: 28
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

    /**
     * Loads a saved game from savefile.txt and starts the game loop.
     * Returns to main menu if no save file exists.
     */
    private static void loadGame() {
        SaveLoad.SaveState state = SaveLoad.load();
        if (state == null) {
            return; // No save file exists, return to menu
        }

        TETile[][] world = state.world;
        Player player = state.player;
        Chaser chaser = state.chaser;

        // Determine world size from loaded world dimensions
        int width = world.length;
        int height = world[0].length;
        
        // Set SIZE based on loaded world dimensions
        if (width == SMALL_WIDTH && height == SMALL_HEIGHT) {
            SIZE = 0;
        } else if (width == MEDIUM_WIDTH && height == MEDIUM_HEIGHT) {
            SIZE = 1;
        } else if (width == BIG_WIDTH && height == BIG_HEIGHT) {
            SIZE = 2;
        } else {
            // Unknown size, default to medium
            SIZE = 1;
        }

        // Initialize renderer with loaded world dimensions
        renderer.initialize(width, height);

        // Render the loaded world
        renderer.renderFrame(world);

        // Start game loop with loaded world, player, and chaser
        runGameLoop(world, player, chaser);
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
                return new int[] { SMALL_WIDTH, SMALL_HEIGHT };
            } else if (c == 'm' || c == 'M') {
                SIZE = 1;
                return new int[] { MEDIUM_WIDTH, MEDIUM_HEIGHT };
            } else if (c == 'b' || c == 'B') {
                SIZE = 2;
                return new int[] { BIG_WIDTH, BIG_HEIGHT };
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
     * @param seed   the seed for world generation
     * @param width  the width of the world
     * @param height the height of the world
     */
    private static void generateAndRenderWorld(long seed, int width, int height) {
        renderer.initialize(width, height);

        WorldGenerator gen = new WorldGenerator(width, height, seed);
        TETile[][] world = gen.generate();

        // Find avatar position and create Player
        Player player = findPlayer(world);
        
        // Create chaser at position from WorldGenerator
        Chaser chaser = null;
        java.awt.Point chaserPos = gen.getChaserPosition();
        if (chaserPos != null) {
            chaser = new Chaser(chaserPos.x, chaserPos.y);
            // Initialize tileUnderChaser from WorldGenerator
            TETile tileUnder = gen.getChaserTileUnder();
            if (tileUnder != null) {
                chaser.tileUnderChaser = tileUnder;
            } else {
                chaser.tileUnderChaser = Tileset.FLOOR; // Fallback
            }
        }

        renderer.renderFrame(world);

        runGameLoop(world, player, chaser);
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
     * Main game loop: handles player movement and updates HUD based on mouse
     * position
     *
     * @param world  the world tile map
     * @param player the player object
     * @param chaser the chaser entity (can be null)
     */
    private static void runGameLoop(TETile[][] world, Player player, Chaser chaser) {
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

        // Reset quit waiting flag
        waitingForQuit = false;
        
        // Initial HUD background
        clearHUDArea(worldWidth, worldHeight);
        StdDraw.show();

        // Main loop: handle input and update HUD based on mouse position
        while (true) {
            boolean playerMoved = false;
            
            // If waiting for Q after pressing :, check for Q key first
            if (waitingForQuit) {
                if (StdDraw.hasNextKeyTyped()) {
                    char q = Character.toUpperCase(StdDraw.nextKeyTyped());
                    if (q == 'Q') {
                        SaveLoad.save(world, player, chaser);
                        System.exit(0);
                    } else {
                        // If not Q, cancel quit mode and process the key normally
                        waitingForQuit = false;
                        // Process the key that was pressed
                        playerMoved = processKeyAfterQuitCancel(world, player, chaser, q);
                    }
                }
            } else {
                // Handle keyboard input for player movement
                playerMoved = handleInput(world, player, chaser);
            }
            
            // Update chaser path for display (always use current player position)
            if (chaser != null) {
                // Calculate path from chaser to current player position for display
                chaser.path = Pathfinder.findPath(chaser.pos, player.pos, world);
            }
            
            // Check for collision - handle cases where player and chaser swap positions
            if (chaser != null && playerMoved) {
                // Check if player moved to chaser's current position
                if (player.pos.equals(chaser.pos)) {
                    showGameOver(worldWidth, worldHeight);
                    System.exit(0);
                }
                
                // Move chaser (this will update chaser.previousPos)
                moveChaser(chaser, player, world);
                
                // Check if chaser caught the player after moving
                if (chaser.pos.equals(player.pos)) {
                    showGameOver(worldWidth, worldHeight);
                    System.exit(0);
                }
                
                // Check if they swapped positions (passed through each other)
                // Player's previous position = Chaser's current position AND
                // Chaser's previous position = Player's current position
                if (player.previousPos.equals(chaser.pos) && 
                    chaser.previousPos.equals(player.pos)) {
                    showGameOver(worldWidth, worldHeight);
                    System.exit(0);
                }
            }
            
            // Also check if they're on the same tile (in case chaser moves without player moving)
            if (chaser != null && chaser.pos.equals(player.pos)) {
                showGameOver(worldWidth, worldHeight);
                System.exit(0);
            }

            // Update HUD based on mouse position
            updateHUDWithMouse(world, worldWidth, worldHeight, player, chaser);
            
            // Render chaser and path
            if (chaser != null) {
                renderChaserAndPath(chaser, world);
            }

            StdDraw.pause(30); // ~30 FPS, avoids busy-waiting
        }
    }

    /**
     * Processes a key press after quitting was cancelled.
     * This allows normal key processing even after : was pressed.
     * 
     * @return true if player actually moved, false otherwise
     */
    private static boolean processKeyAfterQuitCancel(TETile[][] world, Player player, Chaser chaser, char c) {
        switch (c) {
            case 'W':
                return movePlayer(player, Direction.UP, world);
            case 'A':
                return movePlayer(player, Direction.LEFT, world);
            case 'S':
                return movePlayer(player, Direction.DOWN, world);
            case 'D':
                return movePlayer(player, Direction.RIGHT, world);
            case 'P':
                // Toggle path display
                if (chaser != null) {
                    chaser.showPath = !chaser.showPath;
                }
                return false;
            case 'I':
                // Interact with tiles (open treasure, unlock doors, etc.)
                interact(player, world);
                return false;
            case ' ':
                // Push ability (밀쳐내기) - push chaser away
                if (chaser != null) {
                    pushChaser(chaser, player, world);
                }
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Handles keyboard input for player movement (W/A/S/D), save/quit (:Q), 
     * path toggle (P), interact (I), and push ability (Space).
     * 
     * @return true if player actually moved, false otherwise
     */
    private static boolean handleInput(TETile[][] world, Player player, Chaser chaser) {
        if (!StdDraw.hasNextKeyTyped()) {
            return false;
        }

        char c = Character.toUpperCase(StdDraw.nextKeyTyped());
        switch (c) {
            case 'W':
                return movePlayer(player, Direction.UP, world);
            case 'A':
                return movePlayer(player, Direction.LEFT, world);
            case 'S':
                return movePlayer(player, Direction.DOWN, world);
            case 'D':
                return movePlayer(player, Direction.RIGHT, world);
            case ':':
                // Set flag to show quit message and wait for Q
                waitingForQuit = true;
                return false;
            case 'Q':
                // In world mode, Q alone does nothing
                return false;
            case 'P':
                // Toggle path display
                if (chaser != null) {
                    chaser.showPath = !chaser.showPath;
                }
                return false;
            case 'I':
                // Interact with tiles (open treasure, unlock doors, etc.)
                interact(player, world);
                return false;
            case ' ':
                // Push ability (밀쳐내기) - push chaser away
                if (chaser != null) {
                    pushChaser(chaser, player, world);
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Moves the player in the specified direction if the target tile is walkable.
     * Only redraws the affected tiles without re-rendering the entire world.
     * 
     * @param player the player to move
     * @param dir the direction to move
     * @param world the world tile map
     * @return true if player actually moved, false otherwise
     */
    private static boolean movePlayer(Player player, Direction dir, TETile[][] world) {
        player.facing = dir;

        int nx = player.pos.x + dir.dx;
        int ny = player.pos.y + dir.dy;

        // Check bounds
        if (nx < 0 || nx >= world.length || ny < 0 || ny >= world[0].length) {
            return false;
        }

        // Check if target tile is walkable (matches WorldGenerator.isWalkableTile
        // logic)
        if (isWalkableTile(world[nx][ny])) {
            // Reset font to tile rendering font before redrawing tiles
            // (HUD may have changed the font)
            renderer.resetFont();
            
            // Save previous position before moving (for chaser to track)
            player.previousPos = new java.awt.Point(player.pos.x, player.pos.y);
            
            // Restore old location with the tile that was under the player
            world[player.pos.x][player.pos.y] = player.tileUnderPlayer;
            world[player.pos.x][player.pos.y].draw(player.pos.x, player.pos.y);

            // Save the new tile that will be under the player
            player.tileUnderPlayer = world[nx][ny];

            // Move player
            player.pos = new java.awt.Point(nx, ny);

            // Place avatar tile and redraw
            world[nx][ny] = Tileset.AVATAR;
            world[nx][ny].draw(nx, ny);

            // Show the updated tiles
            StdDraw.show();
            return true; // Player actually moved
        }
        return false; // Player did not move
    }

    /**
     * Moves the chaser one step towards the player using BFS pathfinding.
     * The chaser tracks the player's previous position (before last move) to avoid
     * immediately catching up to the player's new position.
     * Note: The path for display is calculated separately in runGameLoop using current position.
     * 
     * @param chaser the chaser to move
     * @param player the target player
     * @param world the world tile map
     */
    private static void moveChaser(Chaser chaser, Player player, TETile[][] world) {
        // Find path from chaser to player's previous position (before last move)
        // This prevents chaser from immediately catching up to player's new position
        java.util.List<java.awt.Point> path = Pathfinder.findPath(chaser.pos, player.previousPos, world);
        
        // If path exists and has at least one step, move chaser
        if (!path.isEmpty()) {
            java.awt.Point nextPos = path.get(0);
            
            // Check bounds
            if (nextPos.x >= 0 && nextPos.x < world.length && 
                nextPos.y >= 0 && nextPos.y < world[0].length) {
                
                // Check if target is walkable or passable
                // Pathfinder allows CHASER and AVATAR to be traversed, so we allow them too
                TETile nextTile = world[nextPos.x][nextPos.y];
                if (isWalkableTile(nextTile) || 
                    nextTile.equals(Tileset.AVATAR) ||
                    nextTile.equals(Tileset.CHASER)) {
                    
                    // Reset font before drawing
                    renderer.resetFont();
                    
                    // Save previous position before moving (for collision detection)
                    chaser.previousPos = new java.awt.Point(chaser.pos.x, chaser.pos.y);
                    
                    // Restore old location with the tile that was under the chaser
                    world[chaser.pos.x][chaser.pos.y] = chaser.tileUnderChaser;
                    world[chaser.pos.x][chaser.pos.y].draw(chaser.pos.x, chaser.pos.y);
                    
                    // Save the new tile that will be under the chaser
                    chaser.tileUnderChaser = world[nextPos.x][nextPos.y];
                    
                    // Move chaser
                    chaser.pos = nextPos;
                    
                    // Place chaser tile (only if not on player)
                    if (!chaser.pos.equals(player.pos)) {
                        world[chaser.pos.x][chaser.pos.y] = Tileset.CHASER;
                        world[chaser.pos.x][chaser.pos.y].draw(chaser.pos.x, chaser.pos.y);
                    }
                    
                    // Show the updated tiles
                    StdDraw.show();
                    
                    // Update path after moving (for next display)
                    chaser.path = Pathfinder.findPath(chaser.pos, player.pos, world);
                }
            }
        }
    }
    
    /**
     * Renders the chaser's path as small yellow dots on top of tiles (if path display is enabled).
     * Clears previous path by redrawing tiles before drawing new path.
     */
    private static void renderChaserAndPath(Chaser chaser, TETile[][] world) {
        // Clear previous path by redrawing tiles
        if (chaser.previousPath != null && !chaser.previousPath.isEmpty()) {
            renderer.resetFont();
            for (java.awt.Point p : chaser.previousPath) {
                // Redraw the tile to clear the path dot
                if (p.x >= 0 && p.x < world.length && 
                    p.y >= 0 && p.y < world[0].length &&
                    !p.equals(chaser.pos)) {
                    world[p.x][p.y].draw(p.x, p.y);
                }
            }
        }
        
        // Draw new path if path display is enabled
        if (chaser.showPath && chaser.path != null && !chaser.path.isEmpty()) {
            StdDraw.setPenColor(new java.awt.Color(255, 0, 0, 200)); // Red color
            
            for (java.awt.Point p : chaser.path) {
                // Don't draw path on chaser's current position or player position
                if (!p.equals(chaser.pos) && 
                    p.x >= 0 && p.x < world.length &&
                    p.y >= 0 && p.y < world[0].length &&
                    (world[p.x][p.y] == null || !world[p.x][p.y].equals(Tileset.AVATAR))) {
                    double centerX = p.x + 0.5;
                    double centerY = p.y + 0.5;
                    double dotSize = 0.15;
                    StdDraw.filledCircle(centerX, centerY, dotSize);
                }
            }
        }
        
        // Update previous path for next frame
        if (chaser.path != null) {
            chaser.previousPath = new java.util.ArrayList<>(chaser.path);
        } else {
            chaser.previousPath = null;
        }
        
        StdDraw.show();
    }
    
    /**
     * Checks if a tile is walkable.
     * Walkable tiles are all tiles except unwalkable obstacles.
     * Must match WorldGenerator.isWalkableTile and Pathfinder.isWalkableTile for consistency.
     */
    private static boolean isWalkableTile(TETile tile) {
        if (tile == null) {
            return false;
        }
        return !tile.equals(Tileset.AVATAR)
                && !tile.equals(Tileset.WALL)
                && !tile.equals(Tileset.NOTHING)
                && !tile.equals(Tileset.WATER)
                && !tile.equals(Tileset.LOCKED_DOOR)
                && !tile.equals(Tileset.MOUNTAIN)
                && !tile.equals(Tileset.BUSH)
                && !tile.equals(Tileset.TREE)
                && !tile.equals(Tileset.PORTAL)
                && !tile.equals(Tileset.TREASURE)
                && !tile.equals(Tileset.OPENED_CHEST)
                && !tile.equals(Tileset.CRATE)
                && !tile.equals(Tileset.BOOKSHELF)
                && !tile.equals(Tileset.SNOWMAN)
                && !tile.equals(Tileset.STATUE);
    }
    
    /**
     * Interacts with tiles in front of the player.
     * Opens treasure chests (increases push ability count) and unlocks doors.
     */
    private static void interact(Player player, TETile[][] world) {
        java.awt.Point front = player.frontTile();
        
        // Check bounds
        if (front.x < 0 || front.x >= world.length || 
            front.y < 0 || front.y >= world[0].length) {
            return;
        }
        
        TETile frontTile = world[front.x][front.y];
        
        // Open treasure chest
        if (frontTile.equals(Tileset.TREASURE)) {
            player.pushAbilityCount++;
            // Replace treasure with opened chest
            world[front.x][front.y] = Tileset.OPENED_CHEST;
            renderer.resetFont();
            world[front.x][front.y].draw(front.x, front.y);
            StdDraw.show();
        }
        
        // Unlock door
        if (frontTile.equals(Tileset.LOCKED_DOOR)) {
            world[front.x][front.y] = Tileset.UNLOCKED_DOOR;
            renderer.resetFont();
            world[front.x][front.y].draw(front.x, front.y);
            StdDraw.show();
        }
        
        // Interact with portal (clear game)
        if (frontTile.equals(Tileset.PORTAL)) {
            showClearScreen(world.length, world[0].length);
        }
    }
    
    /**
     * Pushes the chaser away from the player (밀쳐내기).
     * Only works if chaser is within 2 tiles of the player (24 tiles total).
     * Pushes chaser up to 3 tiles away in the direction from player to chaser.
     * Stops early if blocked by a wall or unwalkable tile.
     */
    private static void pushChaser(Chaser chaser, Player player, TETile[][] world) {
        // Check if player has push ability
        if (player.pushAbilityCount <= 0) {
            return;
        }
        
        // Calculate relative position
        int dx = chaser.pos.x - player.pos.x;
        int dy = chaser.pos.y - player.pos.y;
        
        // Check if chaser is within 2 tiles (Chebyshev distance <= 2)
        // This includes all 24 tiles around the player in a 5x5 square (excluding center)
        if (Math.max(Math.abs(dx), Math.abs(dy)) > 2 || (dx == 0 && dy == 0)) {
            return;
        }
        
        // Calculate push direction (from player to chaser)
        Direction pushDir = null;
        if (dx > 0 && dy == 0) {
            pushDir = Direction.RIGHT;
        } else if (dx < 0 && dy == 0) {
            pushDir = Direction.LEFT;
        } else if (dx == 0 && dy > 0) {
            pushDir = Direction.UP;
        } else if (dx == 0 && dy < 0) {
            pushDir = Direction.DOWN;
        } else {
            // Diagonal: use the direction with larger absolute value, or prefer horizontal
            if (Math.abs(dx) >= Math.abs(dy)) {
                pushDir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                pushDir = dy > 0 ? Direction.UP : Direction.DOWN;
            }
        }
        
        // Try to push up to 3 tiles, stopping if blocked
        int pushDistance = 0;
        
        for (int i = 1; i <= 3; i++) {
            int nextX = chaser.pos.x + pushDir.dx * i;
            int nextY = chaser.pos.y + pushDir.dy * i;
            
            // Check bounds
            if (nextX < 0 || nextX >= world.length || 
                nextY < 0 || nextY >= world[0].length) {
                break; // Hit boundary, stop pushing
            }
            
            // Check if target position is walkable
            TETile nextTile = world[nextX][nextY];
            if (!isWalkableTile(nextTile) && 
                !nextTile.equals(Tileset.AVATAR) &&
                !nextTile.equals(Tileset.CHASER)) {
                break; // Hit wall or obstacle, stop pushing
            }
            
            pushDistance = i;
        }
        
        // If we couldn't push at least 1 tile, don't use the ability
        if (pushDistance == 0) {
            return;
        }
        
        // Calculate final target position
        int targetX = chaser.pos.x + pushDir.dx * pushDistance;
        int targetY = chaser.pos.y + pushDir.dy * pushDistance;
        
        // Use push ability
        player.pushAbilityCount--;
        
        // Reset font before drawing
        renderer.resetFont();
        
        // Restore old location
        world[chaser.pos.x][chaser.pos.y] = chaser.tileUnderChaser;
        world[chaser.pos.x][chaser.pos.y].draw(chaser.pos.x, chaser.pos.y);
        
        // Save new tile under chaser
        chaser.tileUnderChaser = world[targetX][targetY];
        
        // Save previous position for collision detection
        chaser.previousPos = new java.awt.Point(chaser.pos.x, chaser.pos.y);
        
        // Move chaser
        chaser.pos = new java.awt.Point(targetX, targetY);
        
        // Place chaser tile
        if (!chaser.pos.equals(player.pos)) {
            world[chaser.pos.x][chaser.pos.y] = Tileset.CHASER;
            world[chaser.pos.x][chaser.pos.y].draw(chaser.pos.x, chaser.pos.y);
        }
        
        // Clear chaser's path (will be recalculated on next move)
        chaser.path = null;
        
        StdDraw.show();
    }

    /**
     * Clears and redraws the HUD area, then shows information about
     * the tile currently under the mouse cursor (if any).
     * Also displays push ability icon on the right side.
     */
    private static void updateHUDWithMouse(TETile[][] world, int worldWidth, int worldHeight, Player player, Chaser chaser) {
        // Redraw HUD background
        clearHUDArea(worldWidth, worldHeight);

        // If waiting for quit confirmation, show quit message
        if (waitingForQuit) {
            StdDraw.setPenColor(StdDraw.YELLOW);
            StdDraw.setFont(HUD_FONT);
            StdDraw.text(worldWidth / 2.0, worldHeight - 0.9, "Press Q to quit");
            StdDraw.show();
            return;
        }

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
        
        // Check if player can interact with treasure chest or portal (front tile is TREASURE or PORTAL)
        java.awt.Point frontTile = player.frontTile();
        boolean showInteractMessage = false;
        if (frontTile.x >= 0 && frontTile.x < worldWidth && 
            frontTile.y >= 0 && frontTile.y < worldHeight - 2) {
            TETile frontTileType = world[frontTile.x][frontTile.y];
            if (frontTileType != null && 
                (frontTileType.equals(Tileset.TREASURE) || frontTileType.equals(Tileset.PORTAL))) {
                // Display "Press I to Interact" on the left side of HUD
                StdDraw.setPenColor(StdDraw.CYAN);
                StdDraw.setFont(HUD_FONT);
                StdDraw.textLeft(2.0, worldHeight - 0.9, "Press I to Interact");
                showInteractMessage = true;
            }
        }
        
        // Check if player can push chaser (chaser within 2 tiles and has push ability)
        if (!showInteractMessage && chaser != null && player.pushAbilityCount > 0) {
            int dx = chaser.pos.x - player.pos.x;
            int dy = chaser.pos.y - player.pos.y;
            
            // Check if chaser is within 2 tiles (Chebyshev distance <= 2)
            if (Math.max(Math.abs(dx), Math.abs(dy)) <= 2 && (dx != 0 || dy != 0)) {
                // Display "Press Space to Push" on the left side of HUD
                StdDraw.setPenColor(StdDraw.CYAN);
                StdDraw.setFont(HUD_FONT);
                StdDraw.textLeft(2.0, worldHeight - 0.9, "Press Space to Push");
            }
        }
        
        // Draw push ability icon on the right side of HUD
        drawPushAbilityIcon(worldWidth, worldHeight, player);

        StdDraw.show();
    }
    
    /**
     * Displays Clear/Game Complete message and waits before exiting.
     */
    private static void showClearScreen(int worldWidth, int worldHeight) {
        // Clear screen
        StdDraw.clear(StdDraw.BLACK);
        
        // Draw "CLEAR" or "GAME COMPLETE" message
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setFont(new java.awt.Font("Monaco", java.awt.Font.BOLD, 60));
        StdDraw.text(worldWidth / 2.0, worldHeight / 2.0 + 1, "CLEAR!");
        
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new java.awt.Font("Monaco", java.awt.Font.PLAIN, 24));
        StdDraw.text(worldWidth / 2.0, worldHeight / 2.0 - 1, "Game Complete");
        
        StdDraw.show();
        
        // Wait a bit before exiting
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        System.exit(0);
    }
    
    /**
     * Displays Game Over message and waits before exiting.
     */
    private static void showGameOver(int worldWidth, int worldHeight) {
        // Clear screen
        StdDraw.clear(StdDraw.BLACK);
        
        // Draw Game Over text
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setFont(TITLE_FONT);
        StdDraw.text(worldWidth / 2.0, worldHeight / 2.0 + 5, "GAME OVER");
        
        // Draw subtitle
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(MENU_FONT);
        StdDraw.text(worldWidth / 2.0, worldHeight / 2.0 - 5, "Chaser caught you!");
        
        StdDraw.show();
        
        // Wait a bit before exiting
        StdDraw.pause(2000);
    }
    
    /**
     * Draws the push ability icon and count on the right side of the HUD.
     */
    private static void drawPushAbilityIcon(int worldWidth, int worldHeight, Player player) {
        // Draw icon on the right side of HUD
        double iconX = worldWidth - 2.0;
        double iconY = worldHeight - 0.9;
        
        // Draw push ability icon
        StdDraw.setPenColor(StdDraw.CYAN);
        StdDraw.setFont(HUD_FONT);
        StdDraw.text(iconX, iconY, "⚡"); // Lightning bolt icon for push ability
        
        // Draw count below the icon
        StdDraw.setFont(HUD_TAG_FONT);
        StdDraw.text(iconX, iconY - 0.6, String.valueOf(player.pushAbilityCount));
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
        } else if (tile.equals(Tileset.OPENED_CHEST)) {
            return "Opened Chest";
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
     * Returns a short category tag for the given tile, e.g. "dangerous",
     * "interactable",
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
                || tile.equals(Tileset.TREE)
                || tile.equals(Tileset.OPENED_CHEST)
                || tile.equals(Tileset.BUSH)
                || tile.equals(Tileset.STATUE)
                || tile.equals(Tileset.CRATE)
                || tile.equals(Tileset.BOOKSHELF)
                || tile.equals(Tileset.SNOWMAN)
                || tile.equals(Tileset.PORTAL)
                || tile.equals(Tileset.TREASURE)) {
            return "unwalkable";
        }

        return "";
    }

}
