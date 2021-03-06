package com.jona.schiffeversenken.gameobjects.ships;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jona.schiffeversenken.R;
import com.jona.schiffeversenken.gameobjects.Ship;

import framework.core.GameBoard;

public class BattleShip extends Ship {
    public BattleShip(Context context) {
        super(context);
    }

    public BattleShip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BattleShip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getShipLength() {
        return 4;
    }

    public void setSize() {
        Rect dimensions = new Rect();
        dimensions.top = GameBoard.unitCount / 20;
        dimensions.right = ((GameBoard.unitCount / 10) * getShipLength()) - (GameBoard.unitCount / 20);
        dimensions.bottom = GameBoard.unitCount / 20;
        dimensions.left = GameBoard.unitCount / 20;

        setDimensionsFromCenter(dimensions);
    }

    public void setImage() {
        setImageResource(R.drawable.ship_battleship);
    }
}
