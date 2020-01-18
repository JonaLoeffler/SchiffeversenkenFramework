package framework.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public abstract class GameBoard extends ViewGroup {

    /**
     * Count of units the screen estate is divided by. The relative sizes of the GameObject subclasses are calculated based on this value. Default is 100
     */
    public static final int unitCount = 100;
    @SuppressWarnings("unused")
    private static final String TAG = "Gameboard";
    /**
     * Rectangle in which the size of a child is temporarily stored
     */
    private final Rect mTmpChildRect = new Rect();
    /**
     * The actual size of the GameBoard in pixels is divided by its unitCount
     */
    private float unitWidth;
    /**
     * The actual size of the GameBoard in pixels is divided by its unitCount
     */
    private float unitHeight;

    /**
     * constructors
     */
    public GameBoard(Context context) {
        super(context);
    }

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new GameBoard.LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * Lays out children based on their position and size.
     * Also sets the pivotPoint of each GameObject.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();

//        Log.d(TAG, "onLayout: Laying out " + count + " children");
//        Log.d(TAG, "onLayout: " + count + " children, width " + unitWidth + " height " + unitHeight + ", parWidth " + parentWidth + " parHeight " + parentHeight);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                Point pos = lp.getPosition();
                Rect dimensions = lp.getDimensionsFromCenter();

                child.setPivotX(dimensions.left * unitWidth);
                child.setPivotY(dimensions.top * unitHeight);

                mTmpChildRect.top = (int) ((pos.y - dimensions.top) * unitHeight);
                mTmpChildRect.right = (int) ((pos.x + dimensions.right) * unitWidth);
                mTmpChildRect.bottom = (int) ((pos.y + dimensions.bottom) * unitHeight);
                mTmpChildRect.left = (int) ((pos.x - dimensions.left) * unitWidth);

//                Log.d(TAG, "onLayout: left: " + mTmpChildRect.left + " " + mTmpChildRect.top + " " + mTmpChildRect.right + " " + mTmpChildRect.bottom);

                child.layout(mTmpChildRect.left, mTmpChildRect.top, mTmpChildRect.right, mTmpChildRect.bottom);
            }
        }
    }

    /**
     * Returns true if the two instances of GameObject are touching or overlapping each other in the GameBoard.
     */
    public boolean checkIfTouching(GameObject child1, GameObject child2) throws IllegalArgumentException {
//        for (int i = 0; i < getChildCount(); i++) {
//            if (child1 == child2) {
//                IllegalArgumentException e = new IllegalArgumentException("Illegal Arguments");
//                throw e;
//            }
//            if (getChildAt(i).equals(child1) || getChildAt(i).equals(child2)) {
//                IllegalArgumentException e = new IllegalArgumentException("GameBoard is not parent of given views");
//                throw e;
//            }
//        }

//        Rect dim1 = child1.getDimensionsFromCenter();
//        Rect dim2 = child1.getDimensionsFromCenter();
//        Point pos1 = child1.getPosition();
//        Point pos2 = child2.getPosition();

//        if ((pos1.x + dim1[1]) < (pos2.x - dim2[3])) {
//            if ((pos1.y + dim1[0]) < (pos2.y - dim2[2])) {
//                return true;
//            }
//            if ((pos1.y - dim1[2]) > (pos2.y - dim2[0])) {
//                return true;
//            }
//        }
//
//        if ((pos1.x - dim1[3]) > (pos2.x - dim2[1])) {
//            if ((pos1.y + dim1[0]) < (pos2.y - dim2[2])) {
//                return true;
//            }
//            if ((pos1.y - dim1[2]) > (pos2.y - dim2[0])) {
//                return true;
//            }
//        }
        return false;
    }

    /**
     * Determines width and height of a single unit and ensures the GameBoard is a square.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        float parentWidth = MeasureSpec.getSize(widthMeasureSpec);

        unitHeight = parentHeight / unitCount;
        unitWidth = parentWidth / unitCount;

        int size = (int) (parentWidth > parentHeight ? parentHeight : parentWidth);
//        Log.d(TAG, "onMeasure: Set dimensions to " + size);
        setMeasuredDimension(size, size);
    }

    /**
     * Prevent Layout from scrolling
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    public void addView(View view) {
        if (GameObject.class.isAssignableFrom(view.getClass())) {
            super.addView(view);
        }
    }

    /**
     * LayoutParams for subclasses of GameObject
     */
    public static class LayoutParams extends MarginLayoutParams {

        /**
         * x and y coordinates of the center of the view, origin: top left
         * corner
         */
        private Point position = new Point(0, 0);

        /**
         * Game object type of the child
         */
        private int gameObjectType;

        /**
         * top, right, bottom, left, relative to center
         */
        private Rect dimensionsFromCenter = new Rect();

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * A custom constructor with the custom attributes.
         */
        public LayoutParams(Point position, int gameObjectType, Rect dimensionsFromCenter) {
            super(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            setGameObjectType(gameObjectType);
            setPosition(position);
            setDimensionsFromCenter(dimensionsFromCenter);
        }

        /**
         * Returns the GameObjectType of the GameObject child.
         *
         * @return gameObjectType.
         */
        public int getGameObjectType() {
            return this.gameObjectType;
        }

        /**
         * Sets the GameObjectType of the GameObject child.
         * This Method is equal to GameObject.setGameObjectType() and should therefore not be called.
         *
         * @param gameObjectType The possible GameObjectTypes should be defined in a subclass of GameBoard.
         */
        public void setGameObjectType(int gameObjectType) {
            this.gameObjectType = gameObjectType;
        }

        /**
         * Returns the Position of the GameObject in its parent GameBoard.
         *
         * @return position
         */
        public Point getPosition() {
            return this.position;
        }

        /**
         * Sets the Position of the GameObject relative to its parent GameBoard with the top left corner being (0|0).
         * This Method is equal to GameObject.setPosition() and should therefore not be called.
         *
         * @param position The Point's x and y values need to be between 0 and the unitCount of the GameBoard..
         */
        public void setPosition(Point position) {
            if (position.x > 0 && position.y > 0)
                this.position = position;
        }

        /**
         * Returns the dimensionsFromCenter attribute.
         *
         * @return dimensionsFromCenter.
         */
        public Rect getDimensionsFromCenter() {
            return this.dimensionsFromCenter;
        }

        /**
         * Sets the DimensionsFromCenter attribute of the GameObjectType. <p></p>
         * The dimensionsFromCenter array defines the space between the GameObjects position point and the sides.
         *
         * @param dimensionsFromCenter top, left, bottom, right being the space between position point and sides, not the actual length of each side.
         */
        public void setDimensionsFromCenter(Rect dimensionsFromCenter) {
            this.dimensionsFromCenter = dimensionsFromCenter;
        }
    }
}
