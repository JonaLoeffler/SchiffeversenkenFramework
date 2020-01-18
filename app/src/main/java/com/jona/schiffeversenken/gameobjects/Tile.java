package com.jona.schiffeversenken.gameobjects;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.jona.schiffeversenken.R;
import com.jona.schiffeversenken.utility.MyColorFilter;

import framework.core.GameBoard;
import framework.core.GameObject;

public class Tile extends GameObject {

    @SuppressWarnings("unused")
    private static final String TAG = "Tile";

    public int tag;

    public Tile(Context context) {
        super(context);
        init();
    }

    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Tile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    public void setSize() {
        Rect dimensions = new Rect();
        dimensions.top = GameBoard.unitCount / 20;
        dimensions.left = GameBoard.unitCount / 20;
        dimensions.right = GameBoard.unitCount / 20;
        dimensions.bottom = GameBoard.unitCount / 20;
        setDimensionsFromCenter(dimensions);
    }

    public int getTileTag() {
        return tag;
    }

    public void setTileTag(int tag) {
        this.tag = tag;
        setPosition(BattleShipsGameBoard.P[tag]);
    }

    @Override
    protected void setImage() {
        MyColorFilter.filter(getContext(), this, R.color.accentColor, R.drawable.tile_frame);
//        setImageResource(R.drawable.tile_frame);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected)
            setBackgroundResource(R.drawable.tile_frame_selected);
        else
            setBackgroundResource(R.drawable.tile_frame);
    }
}
