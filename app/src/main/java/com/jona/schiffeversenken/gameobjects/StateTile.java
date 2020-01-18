package com.jona.schiffeversenken.gameobjects;

import android.content.Context;
import android.util.AttributeSet;

import com.jona.schiffeversenken.R;

public class StateTile extends Tile {

    public static final int TILESTATE_NONE = 0;
    public static final int TILESTATE_MISS = 1;
    public static final int TILESTATE_HIT = 2;
    private static final String TAG = "StateTile";
    private int state = TILESTATE_NONE;

    public StateTile(Context context) {
        super(context);
        init();
    }

    public StateTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setSize();
    }

//    public void setSize() {
//        Rect dimensions = new Rect();
//        dimensions.top = GameBoard.unitCount / 20;
//        dimensions.left = GameBoard.unitCount / 20;
//        dimensions.right = GameBoard.unitCount / 20;
//        dimensions.bottom = GameBoard.unitCount / 20;
//        setDimensionsFromCenter(dimensions);
//    }

    @Override
    public void setImage() {
//        Log.d(TAG, "setImage: TileState = " + state);
        switch (state) {
            case TILESTATE_HIT:
                setImageResource(R.drawable.tile_hit);
                break;
            case TILESTATE_MISS:
                setImageResource(R.drawable.tile_miss);
                break;
            case TILESTATE_NONE:
                setImageResource(R.color.transparent);
                break;
            default:
                break;
        }
    }

    public void setState(int aState) {
        this.state = aState;
        setImage();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
