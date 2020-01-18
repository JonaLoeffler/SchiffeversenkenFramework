package com.jona.schiffeversenken;

/**
 * Globale Konstanten
 */

public interface Constants {

    /**
     * Intent Request codes
     */
    int REQUEST_CONNECT_DEVICE_SECURE = 0;
    int REQUEST_ENABLE_BT = 1;

    /**
     * Message types for data exchange between clients
     * <p/>
     * HANDLER
     */
    // what --> message type
    int MESSAGE_CONNECTION_STATE_CHANGE = 2;
    int MESSAGE_READ = 3;
    int MESSAGE_WRITE = 4;
    int MESSAGE_DEVICE_NAME = 5;
    int MESSAGE_FAILURE = 6;
    int MESSAGE_TOAST = 7;
    // arg1 --> connection states
    int STATE_NONE = 8;
    int STATE_LISTEN = 9;
    int STATE_CONNECTING = 10;
    int STATE_CONNECTED = 11;

    /**
     * Message types for data exchange between clients
     * <p/>
     * MESSAGE DECODER
     */
    // what
    int MESSAGE_SET_INITIAL_TURN = 12;
    int MESSAGE_INTENT_STARTINGGAME = 13;
    int MESSAGE_STARTINGGAME_NOW = 14;
    int MESSAGE_PLAYER_TURN_CHANGE = 15;
    int MESSAGE_GAME_STATE_CHANGE = 16;
    int MESSAGE_GAME_EVENT = 17;
    int REQUEST_GAME_STATE = 31;
    int ANSWER_GAME_STATE = 32;
    // arg1 --> game states, different states of the game
    int GAMESTATE_PLAYER_PLACING_SHIPS = 18;
    int GAMESTATE_PLAYER_DONE_PLACING_SHIPS = 20;
    int GAMESTATE_PLAYER_SELECTING_TILE = 22;
    int GAMESTATE_PLAYER_SELECTED_TILE = 24;
    // arg1 --> game events, fire on X, hit on Y, ...
    int EVENT_ENEMY_FIRED = 26;
    int EVENT_HIT = 27;
    int EVENT_MISS = 28;
    int EVENT_SHIP_SUNK = 29;
    int EVENT_ENEMY_WON = 30;
    int EVENT_PLAYER_WON = 31;
    // arg2 --> values

    // other
    String EXTRA_DEVICE_ADDRESS = "device_address";
    String DEVICE_NAME = "device_name";
    String FAILURE = "failure";
    String TOAST = "toast";
}
