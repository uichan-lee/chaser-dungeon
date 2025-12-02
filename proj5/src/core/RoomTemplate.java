package core;

import tileengine.TETile;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Immutable blueprint for a room layout.
 */
public final class RoomTemplate {
    // Width of the template in tiles (local coordinates)
    public final int width;

    // Height of the template in tiles (local coordinates)
    public final int height;

    // local tile layout of the room.
    public final TETile[][] layout;

    // Local positions of all doors, measured from the room origin
    public final List<Point> doorPositions;

    // Set of directions of doors
    public final Set<Direction> doorDirections;

    // Metadata describing the category of this template
    public final Set<RoomType> roomTypes;

    public RoomTemplate(int width,
                        int height,
                        TETile[][] layout,
                        List<Point> doorPositions,
                        Set<Direction> doorDirections,
                        Set<RoomType> roomTypes) {

        this.width = width;
        this.height = height;
        this.layout = layout;
        this.doorPositions = Collections.unmodifiableList(doorPositions);
        this.doorDirections = Collections.unmodifiableSet(doorDirections);
        this.roomTypes = roomTypes;
    }

    public TETile tileAt(int x, int y) {
        return layout[x][y];
    }

}
