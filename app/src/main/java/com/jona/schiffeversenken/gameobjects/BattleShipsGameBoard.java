package com.jona.schiffeversenken.gameobjects;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;

import framework.core.GameBoard;

public class BattleShipsGameBoard extends GameBoard {

    public static final Point[] P = {new Point(5, 5), new Point(5, 15), new Point(5, 25), new Point(5, 35), new Point(5, 45),
            new Point(5, 55), new Point(5, 65), new Point(5, 75), new Point(5, 85), new Point(5, 95),
            new Point(15, 5), new Point(15, 15), new Point(15, 25), new Point(15, 35), new Point(15, 45), new Point(15, 55),
            new Point(15, 65), new Point(15, 75), new Point(15, 85), new Point(15, 95),
            new Point(25, 5), new Point(25, 15), new Point(25, 25), new Point(25, 35), new Point(25, 45), new Point(25, 55),
            new Point(25, 65), new Point(25, 75), new Point(25, 85), new Point(25, 95),
            new Point(35, 5), new Point(35, 15), new Point(35, 25), new Point(35, 35), new Point(35, 45), new Point(35, 55),
            new Point(35, 65), new Point(35, 75), new Point(35, 85), new Point(35, 95),
            new Point(45, 5), new Point(45, 15), new Point(45, 25), new Point(45, 35), new Point(45, 45), new Point(45, 55),
            new Point(45, 65), new Point(45, 75), new Point(45, 85), new Point(45, 95),
            new Point(55, 5), new Point(55, 15), new Point(55, 25), new Point(55, 35), new Point(55, 45), new Point(55, 55),
            new Point(55, 65), new Point(55, 75), new Point(55, 85), new Point(55, 95),
            new Point(65, 5), new Point(65, 15), new Point(65, 25), new Point(65, 35), new Point(65, 45), new Point(65, 55),
            new Point(65, 65), new Point(65, 75), new Point(65, 85), new Point(65, 95),
            new Point(75, 5), new Point(75, 15), new Point(75, 25), new Point(75, 35), new Point(75, 45), new Point(75, 55),
            new Point(75, 65), new Point(75, 75), new Point(75, 85), new Point(75, 95),
            new Point(85, 5), new Point(85, 15), new Point(85, 25), new Point(85, 35), new Point(85, 45), new Point(85, 55),
            new Point(85, 65), new Point(85, 75), new Point(85, 85), new Point(85, 95),
            new Point(95, 5), new Point(95, 15), new Point(95, 25), new Point(95, 35), new Point(95, 45), new Point(95, 55),
            new Point(95, 65), new Point(95, 75), new Point(95, 85), new Point(95, 95)};

//    public static final Point[] P = {new Point(50, 50), new Point(50, 150), new Point(50, 250), new Point(50, 350), new Point(50, 450),
//            new Point(50, 550), new Point(50, 650), new Point(50, 750), new Point(50, 850), new Point(50, 950),
//            new Point(150, 50), new Point(150, 150), new Point(150, 250), new Point(150, 350), new Point(150, 450), new Point(150, 550),
//            new Point(150, 650), new Point(150, 750), new Point(150, 850), new Point(150, 950),
//            new Point(250, 50), new Point(250, 150), new Point(250, 250), new Point(250, 350), new Point(250, 450), new Point(250, 550),
//            new Point(250, 650), new Point(250, 750), new Point(250, 850), new Point(250, 950),
//            new Point(350, 50), new Point(350, 150), new Point(350, 250), new Point(350, 350), new Point(350, 450), new Point(350, 550),
//            new Point(350, 650), new Point(350, 750), new Point(350, 850), new Point(350, 950),
//            new Point(450, 50), new Point(450, 150), new Point(450, 250), new Point(450, 350), new Point(450, 450), new Point(450, 550),
//            new Point(450, 650), new Point(450, 750), new Point(450, 850), new Point(450, 950),
//            new Point(550, 50), new Point(550, 150), new Point(550, 250), new Point(550, 350), new Point(550, 450), new Point(550, 550),
//            new Point(550, 650), new Point(550, 750), new Point(550, 850), new Point(550, 950),
//            new Point(650, 50), new Point(650, 150), new Point(650, 250), new Point(650, 350), new Point(650, 450), new Point(650, 550),
//            new Point(650, 650), new Point(650, 750), new Point(650, 850), new Point(650, 950),
//            new Point(750, 50), new Point(750, 150), new Point(750, 250), new Point(750, 350), new Point(750, 450), new Point(750, 550),
//            new Point(750, 650), new Point(750, 750), new Point(750, 850), new Point(750, 950),
//            new Point(850, 50), new Point(850, 150), new Point(850, 250), new Point(850, 350), new Point(850, 450), new Point(850, 550),
//            new Point(850, 650), new Point(850, 750), new Point(850, 850), new Point(850, 950),
//            new Point(950, 50), new Point(950, 150), new Point(950, 250), new Point(950, 350), new Point(950, 450), new Point(950, 550),
//            new Point(950, 650), new Point(950, 750), new Point(950, 850), new Point(950, 950)};

    private static final String TAG = "BattleShipsGameBoard";

    public BattleShipsGameBoard(Context context) {
        super(context);
        init();
    }

    public BattleShipsGameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BattleShipsGameBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public static class LayoutParams extends GameBoard.LayoutParams {

        /**
         *
         */
        public static final int ORIENTATION_HORIZONTAL = 0;

        /**
         *
         */
        public static final int ORIENTATION_VERTICAL = 1;

        public static final int LAYOUT_TYPE_STATETILE = 1;
        public static final int LAYOUT_TYPE_TILE = 0;
        public static final int LAYOUT_TYPE_AIRCRAFTCARRIER = -1;
        public static final int LAYOUT_TYPE_BATTLESHIP = -2;
        public static final int LAYOUT_TYPE_CRUISER_1 = -3;
        public static final int LAYOUT_TYPE_CRUISER_2 = -4;
        public static final int LAYOUT_TYPE_DESTROYER = -5;
        public static final int POSITION_LEGAL = 3;
        public static final int POSITION_ILLEGAL = 4;

        public LayoutParams(Point position, int gameObjectType, Rect dimensionsFromCenter) {
            super(position, gameObjectType, dimensionsFromCenter);
        }
    }
}
