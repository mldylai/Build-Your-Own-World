package byow.Core;

import java.util.ArrayList;
import java.util.Random;

public class Room {

    public int width;
    public int height;
    public int startX;
    public int startY;
    public int[] center;
    public ArrayList<int[]> doors = new ArrayList<>();

    public Room(int width, int height, int startX, int startY) {
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.center = findCenter(width, height, startX, startY);
    }

    public int[] findCenter(int width, int height, int startX, int startY) {
        return new int[]{(startX * 2 + width) / 2, (startY * 2 + height) / 2};
    }
}
