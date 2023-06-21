package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;

    //list of rooms
    private ArrayList<Room> disconnectedRooms = new ArrayList<>();
    private ArrayList<Room> connectedRooms = new ArrayList<>();
    private ArrayList<Hallway> horiHallways = new ArrayList<>();
    private ArrayList<Hallway> vertiHallways = new ArrayList<>();
    public Random R;
    public long seed;
    public TETile[][] world;
    public TETile[][] savedWorld;
    public Waldo waldo;
    public String savedInput = "";
    public String savedPosition;
    public String fileName = "savedfile.txt";
    public TETile avatar = Tileset.AVATAR;
    public boolean inCave = false;
    public int savedX;
    public int savedY;
    public ArrayList<Door> savedDoors = new ArrayList<Door>();


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        // start menu (N, L, Q)
        gameStartMenu();

        // if press N, require player to enter seed
        pressSomething();

        // allows user to move around like they like it
        moveItmoveIt();
    }

    public void gameStartMenu() {
        StdDraw.setCanvasSize(WIDTH * 10, HEIGHT * 10);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, (HEIGHT / 4) * 3, "CS61B: THE GAME");
        Font fontwo = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontwo);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "Load Game (L)");
        // EDITTED!
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Appearance (A)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 9, "Quit (Q)");
        // END
        StdDraw.show();
    }

    public void pressSomething() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == 'n' || key == 'N') {
                    // ask to enter seed
                    StdDraw.clear(Color.BLACK);
                    Font font = new Font("Monaco", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter Seed");
                    savedInput += key;
                    StdDraw.show();
                    // enter that seed
                    gimmeTheSeed();
                    // generate world
                    generateRandomWorld();
                    // generate avatar aka waldo
                    generateWaldo();
                    //locked doors
                    createLockedDoors();
                    ter.renderFrame(world);
                    break;
                }
                if (key == 'l' || key == 'L') {
                    In in = new In(fileName);
                    String[] allInputs = in.readAll().split( ",");
                    String s = allInputs[0];
                    this.savedInput = "n" + inputToSeed(s) + "s";
                    String[] avatarInt = allInputs[2].split(" ");
                    getAvatar(Integer.parseInt(avatarInt[0]));
                    interactWithInputString(s);
                    String[] pos = allInputs[1].split( " ");
                    move(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
                    String[] doorPos = allInputs[3].split(" ")[0].split("~");
                    putDoorsBack(doorPos);
                    ter.renderFrame(world);
                    break;
                }
                if (key == 'a' || key == 'A') {
                    changeAvatarAppearance();
                    gameStartMenu();
                    pressSomething();
                    break;
                }
                if (key == 'q' || key == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    private void putDoorsBack(String[] doorPos) {
        for (String dp: doorPos) {
            String[] xys = dp.split("-");
            int x = Integer.parseInt(xys[0]);
            int y = Integer.parseInt(xys[1]);
            world[x][y] = Tileset.LOCKED_DOOR;
            Door temp = new Door(x, y);
            savedDoors.add(temp);
        }
    }

    private void getAvatar(int input) {
        if (input == 1) {
            this.avatar = Tileset.AVATAR;
        } else if (input == 2) {
            this.avatar = new TETile('w', Color.white, Color.black, "Waldo (you)", "waldo.jpg");
        } else if (input == 3) {
            this.avatar = new TETile('n', Color.white, Color.black, "Nyan cat (you)", "nyan.jpg");
        }
    }

    public void changeAvatarAppearance() {
        changeAvatarMenu(0);
        pressSomethingForAvatarAppearance();
    }

    private void changeAvatarMenu(int input) {
        StdDraw.setCanvasSize(WIDTH * 10, HEIGHT * 10);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH / 2, (HEIGHT / 4) * 3, "Choose your skin");
        Font fontwo = new Font("Monaco", Font.PLAIN, 18);
        StdDraw.setFont(fontwo);
        if (input == 0) {
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3.5 + 1, "Default (1)");
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3 + 1, "Waldo (2)");
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 2.5 + 1, "Nyan Cat (3)");
        }
        if (input == 1) {
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3 + 1, "Waldo (2)");
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 2.5 + 1, "Nyan Cat (3)");
            Font fontspec = new Font("Monaco", Font.BOLD, 24);
            StdDraw.setFont(fontspec);
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3.5 + 1, "Default (1)");
        }
        if (input == 2) {
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3.5 + 1, "Default (1)");
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 2.5 + 1, "Nyan Cat (3)");
            Font fontspec = new Font("Monaco", Font.BOLD, 24);
            StdDraw.setFont(fontspec);
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3 + 1, "Waldo (2)");
        }
        if (input == 3) {
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3.5 + 1, "Default (1)");
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 3 + 1, "Waldo (2)");
            Font fontspec = new Font("Monaco", Font.BOLD, 24);
            StdDraw.setFont(fontspec);
            StdDraw.text(WIDTH / 2, (HEIGHT / 6) * 2.5 + 1, "Nyan Cat (3)");
        }
        Font fonthree = new Font("Monaco", Font.PLAIN, 15);
        StdDraw.setFont(fonthree);
        StdDraw.text((WIDTH / 3) * 1 + 3, (HEIGHT / 3) - 2, "Back (B)");
        StdDraw.text((WIDTH / 3) * 2, (HEIGHT / 3) - 2, "Set (S)");
        StdDraw.show();
    }

    private void pressSomethingForAvatarAppearance() {
        TETile tempAvatar = avatar;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (key == '1') {
                    tempAvatar = Tileset.AVATAR;
                    changeAvatarMenu(1);
                }
                if (key == '2') {
                    tempAvatar = new TETile('w', Color.white, Color.black, "Waldo (you)", "waldo.jpg");
                    changeAvatarMenu(2);
                }
                if (key == '3') {
                    tempAvatar = new TETile('n', Color.white, Color.black, "Nyan cat (you)", "nyan.jpg");
                    changeAvatarMenu(3);
                }
                if (key == 'b' || key == 'B') {
                    break;
                }
                if (key == 's' || key == 'S') {
                    this.avatar = tempAvatar;
                    break;
                }
            }
        }
    }

    public void gimmeTheSeed() {
        String saved = "";
        drawFrame("");
        char thisChar = '.';
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                thisChar = StdDraw.nextKeyTyped();
                if (thisChar == 's' || thisChar == 'S') {
                    long entered = Long.parseLong(saved);
                    if (entered <= 9223372036854775807.0) {
                        this.seed = entered;
                        savedInput += String.valueOf(entered);
                        savedInput += "s";
                        break;
                    }
                } else {
                    saved = saved + thisChar;
                    StdDraw.clear(Color.BLACK);
                    Font font = new Font("Monaco", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Enter Seed");
                    drawFrame(saved);
                }
            }
        }
    }

    // @source lab12
    public void drawFrame(String s) {
//        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    public void generateRandomWorld() {
        ter.initialize(WIDTH,HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        this.world = world;
        Random RANDOM = new Random(this.seed);
        this.R = RANDOM;

        fillWithNothing(world);
        createRandomRooms(world, RANDOM);
        createHallways(world,RANDOM);
        roomStickers(world);
        hallwayStickers(world);
    }

    public void generateWaldo() {
        Room roomWithWaldo = connectedRooms.get(R.nextInt(connectedRooms.size()));
        world[roomWithWaldo.center[0]][roomWithWaldo.center[1]] = this.avatar;
        this.waldo = new Waldo(roomWithWaldo.center[0],roomWithWaldo.center[1]);
    }

    public void moveItmoveIt() {
        char nextKey;
        char prev = '[';
        while (true) {
            HUDisplay();
            if (StdDraw.hasNextKeyTyped()) {
                //edited
                if (!this.inCave) {
                    nextKey= StdDraw.nextKeyTyped();
                    if (nextKey == 'w' || nextKey == 'W') {
                        move(waldo.posX, waldo.posY + 1);
                        savedInput += nextKey;
                        ter.renderFrame(world);
                    }
                    if (nextKey == 'a' || nextKey == 'A') {
                        move(waldo.posX - 1, waldo.posY);
                        savedInput += nextKey;
                        ter.renderFrame(world);
                    }
                    if (nextKey == 's' || nextKey == 'S') {
                        move(waldo.posX, waldo.posY - 1);
                        savedInput += nextKey;
                        ter.renderFrame(world);
                    }
                    if (nextKey == 'd' || nextKey == 'D') {
                        move(waldo.posX + 1, waldo.posY);
                        savedInput += nextKey;
                        ter.renderFrame(world);
                    }
                    if (prev == ':' && (nextKey == 'q' || nextKey == 'Q')) {
                        Out out = new Out(fileName);
                        out.print(savedInput);
                        savedPosition = "," + this.waldo.posX + " " + this.waldo.posY + " ";
                        out.println(savedPosition);
                        String savedAvatar = "," + avatarValue() + " ";
                        out.println(savedAvatar);
                        String savedDoorPositions = getSavedDoors() + " ";
                        out.print(savedDoorPositions);
                        System.exit(0);
                    }
                    prev = nextKey;
                } else {
                    moveInCave();
                }
                //edited
            }
        }
    }

    private int avatarValue() {
        if (avatar.character() == '@') {
            return 1;
        } else if (avatar.character() == 'w') {
            return 2;
        } else if (avatar.character() == 'n') {
            return 3;
        } else {
            return 0;
        }
    }

    public void move(int moveToX, int moveToY) {
        if (walkable(moveToX, moveToY)) {
            if (world[moveToX][moveToY].character() == '█') {
                world[moveToX][moveToY] = Tileset.WALL;
                this.savedWorld = copyWorld(world);
                this.savedX = waldo.posX;
                this.savedY = waldo.posY;
                removeDoor(moveToX, moveToY);
                generateCave();
                this.inCave = true;
            } else {
                world[moveToX][moveToY] = avatar;
                world[waldo.posX][waldo.posY] = Tileset.FLOOR;
                this.waldo.posX = moveToX;
                this.waldo.posY = moveToY;
            }
        }
    }


    private boolean walkable(int x, int y) {
        if (world[x][y].character() == '#') {
            return false;
        }
        return true;
    }

    private void removeDoor(int x, int y) {
        for (int i = 0; i < savedDoors.size(); i++) {
            if (savedDoors.get(i).x == x && savedDoors.get(i).y == y) {
                savedDoors.remove(i);
            }
        }
    }

    private String getSavedDoors() {
        String output = ",";
        for (Door d: savedDoors) {
            output += d.x + "-" + d.y + "~";
        }
        return output.substring(0, output.length() - 1);
    }

    public void HUDisplay() {
        if (StdDraw.isMousePressed()) {
            for (int x = 8; x < 12; x++) {
                world[x][HEIGHT - 2] = Tileset.NOTHING;
                world[x][HEIGHT - 3] = Tileset.NOTHING;
                ter.renderFrame(world);
            }
            int mousePosX = (int) Math.round(StdDraw.mouseX()) - 1;
            int mousePosY = (int) Math.round(StdDraw.mouseY());
            Font font = new Font("Monaco", Font.ITALIC, 12);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            if (world[mousePosX][mousePosY].character() == ' ') {
                StdDraw.text(WIDTH / 8, HEIGHT - 2, "Nothing");
            } else if (world[mousePosX][mousePosY].character() == '#') {
                StdDraw.text(WIDTH / 8, HEIGHT - 2, "Wall");
            } else if (world[mousePosX][mousePosY].character() == '·') {
                StdDraw.text(WIDTH / 8, HEIGHT - 2, "Floor");
            } else if (world[mousePosX][mousePosY].character() == avatar.character()) {
                StdDraw.text(WIDTH / 8, HEIGHT - 2, avatar.description());
            } else if (world[mousePosX][mousePosY].character() == '█') {
            StdDraw.text(WIDTH / 8, HEIGHT - 2, "Door");
        }
            StdDraw.show();
        }
    }
    public void createLockedDoors() {
        for (int i = 0; i <= connectedRooms.size() / 2 ; i++) {
            int x = connectedRooms.get(i).startX + 1;
            int y = connectedRooms.get(i).startY;
            ArrayList<int[]> doors = connectedRooms.get(i).doors;
            boolean valid = true;
            for (int[] door: doors) {
                if (door[0] == x && door[1] == y) {
                    valid = false;
                }
            }
            if (valid && world[x][y].character() != '·') {
                world[x][y] = Tileset.LOCKED_DOOR;
                Door temp = new Door(x, y);
                savedDoors.add(temp);
            }
        }
    }

    public void generateCave() {
        fillWithNothing(world);
        ter.renderFrame(world);
        drawFrame("Entering a cave...");
        StdDraw.pause(2000);
        fillWithNothing(world);
        Font font = new Font("Monaco", Font.PLAIN, 14);
        StdDraw.setFont(font);
        for (int x = (WIDTH / 2 - 10); x <= (WIDTH / 2 + 10); x++) {
            for (int y = (HEIGHT / 2 - 8); y <= (HEIGHT / 2 + 8); y++) {
                if (x == (WIDTH / 2 - 10)|| x == (WIDTH / 2 + 10) || y == (HEIGHT / 2 - 8)|| y == (HEIGHT / 2 + 8)) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
        ter.renderFrame(world);
        generateCoins();
    }

    public void generateCoins() {
        Random rand = new Random();
        for (int x = (WIDTH / 2 - 10); x <= (WIDTH / 2 + 10); x++) {
            for (int y = (HEIGHT / 2 - 8); y <= (HEIGHT / 2 + 8); y++) {
                if (!(x == (WIDTH / 2 - 10)|| x == (WIDTH / 2 + 10) || y == (HEIGHT / 2 - 8)|| y == (HEIGHT / 2 + 8))) {
                    if (rand.nextInt(40) == 1)
                        world[x][y] = Tileset.UNLOCKED_DOOR;
                }
            }
        }
        //put avatar at center
        waldo.posX = WIDTH / 2;
        waldo.posY = HEIGHT / 2;
        world[WIDTH / 2][HEIGHT / 2] = avatar;
        ter.renderFrame(world);
    }

    public void moveInCave() {
        char prev = ' ';
        char nextKey;
        while (!checkCoins()) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT - 7, "Collect all coins to leave the cave!");
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                nextKey= StdDraw.nextKeyTyped();
                if (nextKey == 'w' || nextKey == 'W') {
                    move(waldo.posX, waldo.posY + 1);
                    ter.renderFrame(world);
                }
                if (nextKey == 'a' || nextKey == 'A') {
                    move(waldo.posX - 1, waldo.posY);
                    ter.renderFrame(world);
                }
                if (nextKey == 's' || nextKey == 'S') {
                    move(waldo.posX, waldo.posY - 1);
                    ter.renderFrame(world);
                }
                if (nextKey == 'd' || nextKey == 'D') {
                    move(waldo.posX + 1, waldo.posY);
                    ter.renderFrame(world);
                }
                if (prev == ':' && (nextKey == 'q' || nextKey == 'Q')) {
                    System.exit(0);
                }
                prev = nextKey;
            }
        }
        this.inCave = false;
        StdDraw.pause(500);
        fillWithNothing(world);
        ter.renderFrame(world);
        drawFrame("Going back to your world...");
        StdDraw.pause(1500);
        world = this.savedWorld;
        waldo.posX = this.savedX;
        waldo.posY = this.savedY;
        Font font = new Font("Monaco", Font.PLAIN, 14);
        StdDraw.setFont(font);
        ter.renderFrame(world);
    }

    public boolean checkCoins() {
        for (int x = (WIDTH / 2 - 10); x <= (WIDTH / 2 + 10); x++) {
            for (int y = (HEIGHT / 2 - 8); y <= (HEIGHT / 2 + 8); y++) {
                if (!(x == (WIDTH / 2 - 10)|| x == (WIDTH / 2 + 10) || y == (HEIGHT / 2 - 8)|| y == (HEIGHT / 2 + 8))) {
                    if (world[x][y] == Tileset.UNLOCKED_DOOR)
                        return false;
                }
            }
        }
        return true;
    }

    public TETile[][] copyWorld(TETile[][] tiles) {
        TETile[][] copy = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                copy[x][y] = tiles[x][y];
            }
        }
        return copy;
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        ter.initialize(WIDTH,HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        this.world = world;

        long seed = inputToSeed(input);
        this.seed = seed;
        Random RANDOM = new Random(seed);
        this.R = RANDOM;

        // fills canvas with nothing
        fillWithNothing(world);

        //create random rooms
        createRandomRooms(world, RANDOM);

        //create dem' hallways
        createHallways(world,RANDOM);

        //draw room over hallways
        roomStickers(world);

        //draw hallway floor
        hallwayStickers(world);

        // generate waldo
        generateWaldo();

        // moves waldo to previous saved position
        moveToSaved(input);

        // draws the world to the screen
        ter.renderFrame(world);

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }

    public long inputToSeed(String input) {
        int indexStart = 0;
        int indexEnd = 0;
        while (Character.toUpperCase(input.charAt(indexStart)) != 'N') {
            indexStart += 1;
            indexEnd += 1;
        }
        while (Character.toUpperCase(input.charAt(indexEnd)) != 'S') {
            indexEnd += 1;
        }
        String newInput = input.substring(indexStart + 1, indexEnd);
        long seed = Long.valueOf(newInput);
        return seed;
    }

    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    public void createRoom(TETile[][] world ,int width, int height, int startX, int startY) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (x == startX || x == startX + width - 1 || y == startY || y == startY + height - 1) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
        Room r = new Room(width,height,startX,startY);
        disconnectedRooms.add(r);

        // draw center
//        world[r.center[0]][r.center[1]] = Tileset.FLOWER;
    }

    public void createRandomRooms(TETile[][] world, Random RANDOM) {
        int numOfRooms = RANDOM.nextInt(10) + 5;
        int count = 0;
        while (count <= numOfRooms) {
            int randomWidth = RANDOM.nextInt(10) + 5;
            int randomHeight = RANDOM.nextInt(10) + 5;
            int randomStartX = RANDOM.nextInt(60) + 5;
            int randomStartY = RANDOM.nextInt(30) + 5;
            if (checkOverlap(world, randomWidth, randomHeight, randomStartX, randomStartY)) {
                createRoom(world, randomWidth, randomHeight, randomStartX, randomStartY);
                count += 1;
            }
        }
    }

    public boolean checkOverlap(TETile[][] world, int width, int height, int startX, int startY) {
        boolean output = true;
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (world[x][y].character() != ' ') {
                    output = false;
                }
            }
        }
        return output;
    }

    public void createHallways(TETile[][] world,  Random RANDOM) {
        while (disconnectedRooms.size() != 0) {
            if (connectedRooms.size() == 0) {
                // choose two rooms from disconnect rooms list
                int roomOneID = RANDOM.nextInt(disconnectedRooms.size());
                int roomTwoID = roomOneID;
                while (roomTwoID == roomOneID) {
                    roomTwoID = RANDOM.nextInt(disconnectedRooms.size());
                }
                    // connect first two rooms
                    connectingRooms(world, disconnectedRooms.get(roomOneID), disconnectedRooms.get(roomTwoID));
                    // remove from disconnect and move to connect
                    if (roomOneID < roomTwoID) {
                        connectedRooms.add(disconnectedRooms.remove(roomTwoID));
                        connectedRooms.add(disconnectedRooms.remove(roomOneID));
                    } else {
                        connectedRooms.add(disconnectedRooms.remove(roomOneID));
                        connectedRooms.add(disconnectedRooms.remove(roomTwoID));
                    }
            } else {
                int roomOneID = RANDOM.nextInt(connectedRooms.size());
                int roomTwoID = RANDOM.nextInt(disconnectedRooms.size());

                // connect two rooms
                connectingRooms(world, connectedRooms.get(roomOneID), disconnectedRooms.get(roomTwoID));
                // move room 2 from disconnect to connect
                connectedRooms.add(disconnectedRooms.remove(roomTwoID));
            }
        }
    }

    public void connectingRooms(TETile[][] world, Room roomOne, Room roomTwo) {
        int horiDistance;
        int vertiDistance;
        if (roomOne.center[0] < roomTwo.center[0]) {
            horiDistance = roomTwo.center[0] - roomOne.center[0];
            BobTheBuilder(world, roomOne.center[0], roomOne.center[1], horiDistance);
            if (roomOne.center[1] < roomTwo.center[1]) {
                vertiDistance = roomTwo.center[1] - roomOne.center[1];
                DoraTheExplorer(world, roomOne.center[0] + horiDistance, roomOne.center[1], vertiDistance);
            } else {
                vertiDistance = roomOne.center[1] - roomTwo.center[1];
                DoraTheExplorer(world, roomTwo.center[0], roomTwo.center[1], vertiDistance);
            }
        } else {
            horiDistance = roomOne.center[0] - roomTwo.center[0];
            BobTheBuilder(world, roomTwo.center[0], roomTwo.center[1], horiDistance);
            if (roomOne.center[1] > roomTwo.center[1]) {
                vertiDistance = roomOne.center[1] - roomTwo.center[1];
                DoraTheExplorer(world, roomTwo.center[0] + horiDistance, roomTwo.center[1], vertiDistance);
            } else {
                vertiDistance = roomTwo.center[1] - roomOne.center[1];
                DoraTheExplorer(world, roomOne.center[0], roomOne.center[1], vertiDistance);
            }
        }
    }

    private void BobTheBuilder(TETile[][] world, int startX, int startY, int distance) {
        //horizontal hallway builder
        for (int i = 0; i <= distance + 1; i++) {
            world[startX + i][startY + 1] = Tileset.WALL;
            world[startX + i][startY - 1] = Tileset.WALL;
        }
        for (int i = 0; i < distance + 1; i++) {
            world[startX + i][startY] = Tileset.FLOWER;
        }
        world[startX + distance + 1][startY] = Tileset.WALL;
        Hallway way = new Hallway(startX, startY, distance);
        horiHallways.add(way);
    }

    private void DoraTheExplorer(TETile[][] world, int startX, int startY, int distance) {
        //vertical hallway builder/explorer
        for (int i = 0; i < distance; i++) {
            world[startX + 1][startY + i] = Tileset.WALL;
//            world[startX][startY + i] = Tileset.FLOWER;
            world[startX - 1][startY + i] = Tileset.WALL;
        }
        Hallway way = new Hallway(startX, startY, distance);
        vertiHallways.add(way);
    }

    public void roomStickers(TETile[][] world) {
        for (Room r: connectedRooms) {
            createStickers(world, r.width, r.height, r.startX, r.startY);
        }
    }

    public void createStickers(TETile[][] world ,int width, int height, int startX, int startY) {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (x == startX || x == startX + width - 1 || y == startY || y == startY + height - 1) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    public void hallwayStickers(TETile[][] world) {
        for (Hallway hh: horiHallways) {
            for (int i = 0; i < hh.distance + 1; i++) {
                world[hh.startX + i][hh.startY] = Tileset.FLOOR;
            }
        }
        for (Hallway vh: vertiHallways) {
            for (int i = 0; i < vh.distance; i++) {
                world[vh.startX][vh.startY + i] = Tileset.FLOOR;
            }
        }
    }

    public void moveToSaved(String input) {
        int index = 0;
        while (Character.toUpperCase(input.charAt(index)) != 'S') {
            index += 1;
        }
        if (index + 1 != input.length()) {
            String movements = input.substring(index + 1, input.length() - 1);
            // move waldo
            for (char c: movements.toCharArray()) {
                if (Character.toUpperCase(c) == 'W') {
                    move(waldo.posX, waldo.posY + 1);
                }
                if (Character.toUpperCase(c) == 'A') {
                    move(waldo.posX - 1, waldo.posY);
                }
                if (Character.toUpperCase(c) == 'S') {
                    move(waldo.posX, waldo.posY - 1);
                }
                if (Character.toUpperCase(c) == 'D') {
                    move(waldo.posX + 1, waldo.posY);
                }
            }
        }
    }

}
