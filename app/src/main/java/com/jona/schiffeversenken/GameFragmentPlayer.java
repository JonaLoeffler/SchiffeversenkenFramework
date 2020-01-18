package com.jona.schiffeversenken;

/**
 * Created by Jona on 10.09.2016.
 */

import android.content.ClipData;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.jona.schiffeversenken.gameobjects.BattleShipsGameBoard;
import com.jona.schiffeversenken.gameobjects.Ship;
import com.jona.schiffeversenken.gameobjects.Tile;
import com.jona.schiffeversenken.gameobjects.ships.AirCraftCarrier;
import com.jona.schiffeversenken.gameobjects.ships.BattleShip;
import com.jona.schiffeversenken.gameobjects.ships.Cruiser;
import com.jona.schiffeversenken.gameobjects.ships.Destroyer;
import com.jona.schiffeversenken.utility.MyColorFilter;

import framework.core.GameObject;

/**
 * One of the game fragments, this one holds the player's own grid
 */
public class GameFragmentPlayer extends Fragment implements View.OnTouchListener, Constants {

    private static final String TAG = "GameFragmentPlayer";

    public Ship shipAirCraftCarrier, shipBattleShip, shipCruiser1, shipCruiser2, shipDestroyer;
    public ImageButton btnTurnLeft, btnTurnRight;
    public Button btnDone;
    public ShipPicker picker;
    public boolean enemyDonePlacingShips;
    private Ship selectedShip;

    private BattleShipsGame game;
    private MainActivity main;

    public void setGame(MainActivity main, BattleShipsGame game) {
        this.game = game;
        this.main = main;
    }

    /**
     * wird aufgerufen, nachdem alle schiffe gesetzt und bestaetigt wurden
     */
    private void actionDonePlacingShips() {
        Log.d(TAG, "Done placing ships");
        // interface veraendern fuer neue spielphase
        btnTurnLeft.setVisibility(View.GONE);
        btnTurnRight.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        main.setPlayerGameState(GAMESTATE_PLAYER_DONE_PLACING_SHIPS);

        for (int i = 0; i < game.gbPlayer.getChildCount(); i++) {
            game.gbPlayer.getChildAt(i).setOnClickListener(null);
            game.gbPlayer.getChildAt(i).setOnDragListener(null);
            game.gbPlayer.getChildAt(i).setOnTouchListener(null);
        }

        // koordination zwischen clients um spiel zu beginnen
        if (!enemyDonePlacingShips) {
            Log.d(TAG, "Enemy not ready, sending message");
            main.sendMessage(MESSAGE_GAME_STATE_CHANGE, GAMESTATE_PLAYER_DONE_PLACING_SHIPS, 0);
        } else {
            main.setPlayerGameState(GAMESTATE_PLAYER_SELECTING_TILE);

        }

        // wechsel auf anderen tab
        main.mPager.setCurrentItem(1, true);
    }

    /**
     * dreht das ausgewaehlte schiff nach links wenn maeglich
     */
    private void actionTurnLeft() {
        if (selectedShip != null && selectedShip.turnable == true) {
            game.placeShipInGrid(selectedShip.getPosition(), BattleShipsGameBoard.LayoutParams.ORIENTATION_VERTICAL,
                    selectedShip);
        }
    }

    /**
     * dreht das ausgewaehlte schiff nach rechts wenn moeglich
     */
    private void actionTurnRight() {
        if (selectedShip != null && selectedShip.turnable == true) {
            game.placeShipInGrid(selectedShip.getPosition(), BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL,
                    selectedShip);
        }
    }

    private void initGridView() {
        for (int i = 0; i < 100; i++) {
            Tile tile = new Tile(getActivity());
            tile.setTileTag(i);
            tile.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_TILE);
            tile.setOnDragListener(new TileOnDragListener());
            game.gbPlayer.addView(tile);
        }
    }

    /**
     * onclick methoden fuer die schiffe
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (view.getParent() == picker || view.getParent() == game.gbPlayer) {
            startDrag(view);
            return true;
        }
        return false;
    }

    /**
     * erstellt die Views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_player, container, false);

        game.gbPlayer = (BattleShipsGameBoard) rootView.findViewById(R.id.gameboard_player);
        picker = (ShipPicker) rootView.findViewById(R.id.ship_picker);

        shipAirCraftCarrier = new AirCraftCarrier(rootView.getContext());
        shipBattleShip = new BattleShip(rootView.getContext());
        shipCruiser1 = new Cruiser(rootView.getContext());
        shipCruiser2 = new Cruiser(rootView.getContext());
        shipDestroyer = new Destroyer(rootView.getContext());

        shipAirCraftCarrier.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_AIRCRAFTCARRIER);
        shipBattleShip.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_BATTLESHIP);
        shipCruiser1.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_CRUISER_1);
        shipCruiser2.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_CRUISER_2);
        shipDestroyer.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_DESTROYER);

        shipAirCraftCarrier.setTag(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_AIRCRAFTCARRIER);
        shipBattleShip.setTag(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_BATTLESHIP);
        shipCruiser1.setTag(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_CRUISER_1);
        shipCruiser2.setTag(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_CRUISER_2);
        shipDestroyer.setTag(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_DESTROYER);

        picker.addView(shipAirCraftCarrier);
        picker.addView(shipBattleShip);
        picker.addView(shipCruiser1);
        picker.addView(shipCruiser2);
        picker.addView(shipDestroyer);

        initGridView();

        btnTurnLeft = (ImageButton) rootView.findViewById(R.id.btn_turn_left);
        btnTurnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionTurnLeft();
            }
        });
        MyColorFilter.filter(rootView.getContext(), btnTurnLeft, R.color.secondaryTextColor, R.drawable.turn_left);

        btnTurnRight = (ImageButton) rootView.findViewById(R.id.btn_turn_right);
        btnTurnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionTurnRight();
            }
        });
        MyColorFilter.filter(rootView.getContext(), btnTurnRight, R.color.secondaryTextColor, R.drawable.turn_right);

        btnDone = (Button) rootView.findViewById(R.id.btn_done_placing_ships);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionDonePlacingShips();
            }
        });

        picker.setOnDragListener(new PickerOnDragListener());

        for (int i = 0; i < picker.getChildCount(); i++) {
            picker.getChildAt(i).setOnTouchListener(this);
        }

        return rootView;
    }

    /**
     * startet drag and drop geste fuer schiffe, +shadow
     */
    private void startDrag(View shipView) {
        shipView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        selectedShip = (Ship) shipView;

        btnTurnLeft.setVisibility(View.VISIBLE);
        btnTurnRight.setVisibility(View.VISIBLE);

        ClipData data = ClipData.newPlainText("", "");
        DragShadow shadow = new DragShadow(shipView);
        shipView.startDrag(data, shadow, shipView, 0);
    }

    public class TileOnDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            Point position;
            Ship ship = (Ship) event.getLocalState();
            GameObject object = (GameObject) v;

            // different possible types of dragevents
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // reparenting if needed
                    if (ship.getParent() != game.gbPlayer) {
                        main.gameFragmentPlayer.picker.removeView(ship);
                        game.gbPlayer.addView(ship);
                    }

                    position = object.getPosition();
                    Log.d(TAG, "Ship dragged over " + position);
                    ship.turnable = true;
                    game.placeShipInGrid(position, ship.getOrientation(), ship);

                    if (main.gameFragmentPlayer.picker.getChildCount() == 0)
                        main.findViewById(R.id.btn_done_placing_ships).setEnabled(true);

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    return false;
            }

            return false;
        }
    }

    /**
     * custom ondraglistener, setzt schiffe bei drag geste auf entsprechendes
     * feld
     */
    private class PickerOnDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View aPicker, DragEvent event) {
            Ship ship = (Ship) event.getLocalState();

            // different possible types of dragevents
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // reparenting if needed
                    if (ship.getParent() != aPicker) {
                        game.gbPlayer.removeView(ship);
                        game.eraseFieldsInArray(ship);
                        ship.setRotation(0);
                        ship.turnable = false;
                        ship.setOrientation(BattleShipsGameBoard.LayoutParams.ORIENTATION_HORIZONTAL);
                        main.gameFragmentPlayer.picker.addView(ship);
                    }
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    return false;
            }

            return false;
        }
    }
}