package com.jona.schiffeversenken;

/**
 * Created by Jona on 10.09.2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jona.schiffeversenken.gameobjects.BattleShipsGameBoard;
import com.jona.schiffeversenken.gameobjects.Tile;

/**
 * one of the game fragments, this one holds the enemy's grid
 */
public class GameFragmentOpponent extends Fragment implements View.OnClickListener, Constants {

    private static final String TAG = "GameFragmentOpponent";

    private ImageButton btn_TileSelected;
    private Tile selectedEnemyTile;

    private BattleShipsGame game;
    private MainActivity main;

    public void setGame(MainActivity main, BattleShipsGame game) {
        this.game = game;
        this.main = main;
    }

    /**
     * wird ausgefuehrt sobald feld ausgewaehlt und bestaetigt wurde
     */
    private void actionTileSelected(View view) {
        selectedEnemyTile = (Tile) view;
        if (!game.isTileUsed(selectedEnemyTile.getTileTag())) {
            main.sendMessage(MESSAGE_GAME_EVENT, EVENT_ENEMY_FIRED, selectedEnemyTile.getTileTag());
            btn_TileSelected.setVisibility(View.GONE);
            selectedEnemyTile.setBackgroundResource(R.drawable.tile_frame);
            selectedEnemyTile = null;
            main.setPlayerGameState(GAMESTATE_PLAYER_SELECTED_TILE);
        } else {
            Log.d(TAG, "actionTileSelected: Tile already occupied");
        }
    }

    private void initGridView() {
        for (int i = 0; i < 100; i++) {
            Tile tile = new Tile(getActivity());
            tile.setTileTag(i);
            tile.setGameObjectType(BattleShipsGameBoard.LayoutParams.LAYOUT_TYPE_TILE);
            tile.setOnClickListener(this);
            game.gbOpponent.addView(tile);
        }
    }

    /**
     * onClickListener fuer die einzelnen tiles des Feldes, zustaendig fuer die
     * auswahl eines feldes
     */
    @Override
    public void onClick(View view) {
        Tile tile = (Tile) view;
        if (tile.getTileTag() >= 0 && tile.getTileTag() < 100) {
            if (main.playerGameState == GAMESTATE_PLAYER_SELECTING_TILE) {
                Log.d(TAG, "Tile selected");
                if (selectedEnemyTile != null) {
                    if (selectedEnemyTile == view) {

                        selectedEnemyTile.setBackground(null);
                        btn_TileSelected.setVisibility(View.GONE);
                        selectedEnemyTile = null;
                    } else {
                        selectedEnemyTile.setBackground(null);
                        tile.setBackgroundResource(R.drawable.tile_frame_selected);
                        btn_TileSelected.setVisibility(View.VISIBLE);
                        selectedEnemyTile = (Tile) view;
                    }
                } else {
                    view.setBackgroundResource(R.drawable.tile_frame_selected);
                    btn_TileSelected.setVisibility(View.VISIBLE);
                    selectedEnemyTile = (Tile) view;
                }
                int tag = tile.getTileTag();
                Log.d(TAG, "Tile clicked, position: " + tag);
            }
        }
    }

    /**
     * erstellt die Views
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_opponent, container, false);

        game.gbOpponent = (BattleShipsGameBoard) rootView.findViewById(R.id.gameboard_opponent);

        initGridView();

        btn_TileSelected = (ImageButton) rootView.findViewById(R.id.btn_tile_selected);
        btn_TileSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEnemyTile != null) {
                    Log.d(TAG, "Fired on tile '" + selectedEnemyTile.getTileTag() + "'");
                    actionTileSelected(selectedEnemyTile);
                } else {
                    Log.d(TAG, "No tile selected");
                }
            }
        });
        return rootView;
    }
}