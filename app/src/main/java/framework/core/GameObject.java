package framework.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * This should be the parent for every class that is going to be laid out in GameBoard.
 * The Attributes can either be set in code or by providing XML Resources.
 * Subclassed children of GameObject should only be placed in a subclass of GameBoard, as their layout attributes will not work with other Layout classes.
 */
public abstract class GameObject extends ImageView {

    private static final String TAG = "GameObject";

    /**
     * One of the GameObject Types, which should be defined in a subclass of GameBoard
     */
    private int gameObjectType;

    /**
     * Boolean value whether the View is selected.
     */
    private boolean selected = false;

    /**
     * x and y coordinates of the center of the view, origin: top left corner
     */
    private Point position = new Point(0, 0);

    /**
     * top, right, bottom, left, relative to center
     */
    private Rect dimensionsFromCenter = new Rect();

    public GameObject(Context context) {
        super(context);
        init();
    }

    public GameObject(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameObject(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Returns the GameObjectType of the GameObject.
     *
     * @return gameObjectType
     */
    public int getGameObjectType() {
        return gameObjectType;
    }

    /**
     * Sets the GameObjectType of the GameObject. Image and size of the GameObject View may also be defined by their GameObjectType. This allows various images to be set for one GameObject Subclass (e.g. for simple animations).
     *
     * @param gameObjectType The possible GameObjectTypes should be defined in a subclass of GameBoard.
     */
    public void setGameObjectType(int gameObjectType) {
        this.gameObjectType = gameObjectType;
        setImage();
        setSize();
        refreshLayoutParams();
    }

    private void init() {
        GameBoard.LayoutParams lp = new GameBoard.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        setImage();
    }

    /**
     * Set the image of the View based on the GameObject Type.
     * To set an image manually use setImageResource() provided by ImageView
     */
    protected abstract void setImage();

    /**
     * Returns the Position of the GameObject in its parent GameBoard.
     *
     * @return position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the Position of the GameObject relative to its parent GameBoard with the top left corner being (0|0).
     *
     * @param position The Point's x and y values need to be between 0 and the unitCount of the GameBoard.
     */
    public void setPosition(Point position) throws IllegalArgumentException {
        if ((position.x > 0 && position.y > 0) || (position.x < GameBoard.unitCount && position.y < GameBoard.unitCount)) {
            this.position = position;
        } else {
            IllegalArgumentException e = new IllegalArgumentException("Illegal Arguments");
            throw e;
        }
        refreshLayoutParams();
    }

    /**
     * Return the selection value of the GameObject.
     *
     * @return selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * May be overridden to change the appearance of the view upon selection.
     *
     * @param selected Boolean value whether the View is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Returns the dimensionsFromCenter attribute.
     *
     * @return dimensionsFromCenter
     */
    public Rect getDimensionsFromCenter() {
        return dimensionsFromCenter;
    }

    /**
     * Sets the DimensionsFromCenter attribute of the GameObjectType. <p></p>
     * The dimensionsFromCenter array defines the space between the GameObjects position point and the sides.
     *
     * @param dimensionsFromCenter top, left, bottom, right being the space between position point and sides, not the actual length of each side.
     */
    public void setDimensionsFromCenter(Rect dimensionsFromCenter) throws IllegalArgumentException {

        this.dimensionsFromCenter = dimensionsFromCenter;
        refreshLayoutParams();
    }

    /**
     * Abstract method which must be overriden and has to set the DimensionsFromCenter attribute when called.
     */
    public abstract void setSize();

    /**
     * Makes possible changes to the GameObject attributes visible by refreshing the LayoutParams. No need for overriding.
     */
    private void refreshLayoutParams() {
        GameBoard.LayoutParams lp = (GameBoard.LayoutParams) this.getLayoutParams();
        lp.setDimensionsFromCenter(this.dimensionsFromCenter);
        lp.setPosition(this.position);
        lp.setGameObjectType(this.gameObjectType);
        setLayoutParams(lp);
        invalidate();
        requestLayout();
    }
}
