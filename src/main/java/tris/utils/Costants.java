package tris.utils;

import java.awt.*;

public final class Costants {
    public static final int GRID_DIMX=3;
    public static final int GRID_DIMY=3;
    public static final int WIN_COUNT=3;
    public static final float WIN_REWARD=1;
    public static final float LOSE_REWARD=-1;
    public static final float DRAW_REWARD=0;

    public static final int MUL_WIDTH_FRAME =160;
    public static final int MUL_HEIGHT_FRAME =130;
    public static final int RECTANGLE_DIMENSION =120;
    public static final int INITIAL_HEIGHT =80;
    public static final int INITIAL_TITLE_WIDTH =53;
    public static final int INITIAL_TITLE_HEIGHT =11;
    public static final int FINAL_RESTART_HEIGHT =10;
    public static final int FINAL_LBLINFO_HEIGHT =30;
    public static final int TITLE_HEIGHT =42;
    public static final int TITLE_WIDTH =464;
    public static final int FONT_TITLE_DIMENSION =40;
    public static final int FONT_RESTART_DIMENSION =20;
    public static final int FONT_INFO_DIMENSION =20;

    public static final int FONT_GRID_FIELDS_DIMENSION =100;
    public static final float ALPHA=0.1f; //learning rate
    public static final float DISCOUNT_FACTOR=0.88f;
    public static final float DELTA=0.03f;
    public static final float EPSILON =0.025f;
    public  static final String BOT_SYMBOL = "X";
    public static final String PLAYER_SYMBOL = "O";
    public static final String NAME_FILE_BOT="C:\\Users\\Mariangela\\tesiWorkspace\\tris\\qtableObjectX";

    public static final Color BOT_COLOR= new Color(0,0,255);;
    public static final Color PLAYER_COLOR= new Color(255,0,0);


}
