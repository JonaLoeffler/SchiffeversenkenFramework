package com.jona.schiffeversenken;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.jona.schiffeversenken.gameobjects.BattleShipsGameBoard;
import com.jona.schiffeversenken.gameobjects.Ship;
import com.jona.schiffeversenken.gameobjects.StateTile;
import com.jona.schiffeversenken.utility.Util;

/**
 * Created by Jona on 22.07.2016.
 */
public class BattleShipsGame {

    public static final String TAG = "BattleShipsGame";
    /**
     * Player's Ships grid, stores ships of player
     */
    private final int[][] playersShipsGrid = new int[10][10];

    /**
     * Player's states grid, stores tile states
     */
    private final int[][] playersStatesGrid = new int[10][10];
    /**
     * Enemy's states grid, stores tile states
     */
    private final int[][] enemyStatesGrid = new int[10][10];

    public Context context;

    public BattleShipsGameBoard gbPlayer;
    public BattleShipsGameBoard gbOpponent;

    public BattleShipsGame(Context context) {
        this.context = context;

    }

    /**
     * creates and adds a statetile to the viewgroup
     */
    public void addStateTile(Point position, int state, boolean playerTile) {
        StateTile tile = new StateTile(context);

        Log.d(TAG, "addStateTile: state: " + state + " position: " + position.toString());
        tile.setState(state);
        tile.setPosition(position);

        if (playerTile) {
            gbPlayer.addView(tile);

            playersStatesGrid[(position.x - 5) / 10][(position.y - 5) / 10] = state;
            Log.d(TAG, "addStateTile: PlayersStatesGrid: ");
            Util.printArray(playersStatesGrid);
        } else {
            gbOpponent.addView(tile);

            enemyStatesGrid[(position.x - 5) / 10][(position.y - 5) / 10] = state;
            Log.d(TAG, "addStateTile: EnemyStatesGrid: ");
            Util.printArray(enemyStatesGrid);
        }

    }

    public boolean isTileUsed(int positionID) {
        Log.d(TAG, "isTileUsed: EnemyStatesGrid");
        Util.printArray(enemyStatesGrid);
        return enemyStatesGrid[Util.getX(positionID)][Util.getY(positionID)] != 0;
    }

    /**
     * changes the ships attributes to the given values
     */
    private Ship changeShipAttributes(Point newPosition, int newOrientation, Ship ship) {
        eraseFieldsInArray(ship);

        if (ship.getAnimation() == null) {
            if (newOrientation != ship.getOrientation()) {
                if (newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL) {
                    ship.setRotationWithAnimation(90, 500);
                    Log.d(TAG, "changeShipAttributes: turned to horizontal");
                }
                if (newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL) {
                    ship.setRotationWithAnimation(-90, 500);
                    Log.d(TAG, "changeShipAttributes: turned to vertical");
                }
            }
        }

        Log.d(TAG, "changeShipAttributes: " + newPosition.toString() + " " + newOrientation);

        ship.setPosition(newPosition);
        ship.setOrientation(newOrientation);
        markFieldsInArray(ship);
        return ship;
    }

    /**
     * deletes the entries of the given ship
     */
    public void eraseFieldsInArray(Ship ship) {
        try {
            for (int i = 0; i < playersShipsGrid.length; i++) {
                for (int j = 0; j < playersShipsGrid[i].length; j++) {
                    if (playersShipsGrid[i][j] == ship.getGameObjectType()) {
                        playersShipsGrid[i][j] = 0;
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests if a ship is on given tile
     */
    public boolean isShipHit(int posID) {
        Log.d(TAG, "isShipHit: Testing on tile " + posID);
        int x = Util.getX(posID);
        int y = Util.getY(posID);
        Log.d(TAG, "isShipHit: PlayersShipsGrid:");
        Util.printArray(playersShipsGrid);
        Log.d(TAG, "isShipHit: state at xy: " + playersShipsGrid[y][x]);
        return playersShipsGrid[y][x] != 0;
    }

    /**
     * checks if a ship can be placed at that position
     */
    private boolean isShipPositionLegal(Point newPosition, int newOrientation, Ship oldShip) {
        int x = (newPosition.x - 5) / 10;
        int y = (newPosition.y - 5) / 10;

        // pruefung ob schiff rechts aus spielfeld herausragt
        if (x + oldShip.getShipLength() > 10 && newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL) {
            Log.d(TAG, "Too far East");
            return false;
        }

        // pruefung ob schiff oben aus spielfeld herausragt
        if ((y + 1) - oldShip.getShipLength() < 0 && newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL) {
            Log.d(TAG, "Too far North");
            return false;
        }

        // pruefung ob andere schiffe im weg sind
        try {
            for (int i = 0; i < oldShip.getShipLength(); i++) {
                if (newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL) {
                    if (playersShipsGrid[x + i][y] != 0 && playersShipsGrid[x + i][y] != oldShip.getGameObjectType()) {
                        Log.d(TAG, "Tile occupied");
                        return false;
                    }
                }
                if (newOrientation == BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL) {
                    if (playersShipsGrid[x][y - i] != 0 && playersShipsGrid[x][y - i] != oldShip.getGameObjectType()) {
                        Log.d(TAG, "Tile occupied");
                        return false;
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "Out of bounds");
            return false;
        }

        return true;
    }

    /**
     * marks the fields occupied by the given ship in the grid
     */
    private void markFieldsInArray(Ship ship) {

        int x = (ship.getPosition().x - 5) / 10;
        int y = (ship.getPosition().y - 5) / 10;

        for (int i = 0; i < ship.getShipLength(); i++) {
            if (ship.getOrientation() == BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL) {
                playersShipsGrid[x + i][y] = ship.getGameObjectType();
            }
            if (ship.getOrientation() == BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL) {
                playersShipsGrid[x][y - i] = ship.getGameObjectType();
            }
        }
        Log.d(TAG, "markFieldsInArray: PlayersShipsGrid:");
        Util.printArray(playersShipsGrid);
    }

    /**
     * checks if position is legal, if yes applies changes, if no discards
     * everything
     */
    public Ship placeShipInGrid(Point newPosition, int newOrientation, Ship oldShip) {

        if (isShipPositionLegal(newPosition, newOrientation, oldShip)) {
            Log.d(TAG, "Selected Position is legal");
            return changeShipAttributes(newPosition, newOrientation, oldShip);
        } else {
            Log.d(TAG, "Position illegal");
            return null;
        }
    }

}
