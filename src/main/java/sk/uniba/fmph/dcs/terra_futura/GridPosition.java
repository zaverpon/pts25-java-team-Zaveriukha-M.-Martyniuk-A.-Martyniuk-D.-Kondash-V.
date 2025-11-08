package sk.uniba.fmph.dcs.terra_futura;

/**
 * Enumeration representing all possible coordinates of the player's grid.
 * Coordinates range from -2 to +2 on both X and Y axes.
 * Coordinate (0,0) indicates the starting card.
 */
public enum GridPosition {

    X_2_Y_2(-2, -2),
    X_2_Y_1(-2, -1),
    X_2_Y0(-2, 0),
    X_2_Y1(-2, 1),
    X_2_Y2(-2, 2),

    X_1_Y_2(-1, -2),
    X_1_Y_1(-1, -1),
    X_1_Y0(-1, 0),
    X_1_Y1(-1, 1),
    X_1_Y2(-1, 2),

    X0_Y_2(0, -2),
    X0_Y_1(0, -1),
    X0_Y0(0, 0),
    X0_Y1(0, 1),
    X0_Y2(0, 2),

    X1_Y_2(1, -2),
    X1_Y_1(1, -1),
    X1_Y0(1, 0),
    X1_Y1(1, 1),
    X1_Y2(1, 2),

    X2_Y_2(2, -2),
    X2_Y_1(2, -1),
    X2_Y0(2, 0),
    X2_Y1(2, 1),
    X2_Y2(2, 2);

    public final int x;
    public final int y;

    GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
