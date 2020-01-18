package com.jona.schiffeversenken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jona.schiffeversenken.gameobjects.BattleShipsGameBoard;
import com.jona.schiffeversenken.gameobjects.StateTile;
import com.jona.schiffeversenken.utility.MyColorFilter;
import com.jona.schiffeversenken.utility.SimpleViewPagerIndicator;
import com.jona.schiffeversenken.utility.Util;
import com.jona.schiffeversenken.utility.ZoomOutPageTransformer;

import framework.bluetooth.BluetoothService;
import framework.bluetooth.DeviceListActivity;
import framework.ui.SettingsActivity;

class DragShadow extends View.DragShadowBuilder {

    // drag shadow beim setzen der schiffe

    public DragShadow(View view) {
        super(view);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        View v = getView();
        int height = v.getHeight();
        int width = v.getWidth();

        shadowSize.set(width, height);
        shadowTouchPoint.set(width / 5, height / 2);
    }
}

public class MainActivity extends FragmentActivity implements Constants {

    private static final String TAG = "MainActivity";
    /**
     * The number of pages in the FragmentPager
     */
    private static final int NUM_PAGES = 2;
    /**
     * spielphase des spielers,
     */
    public int playerGameState = GAMESTATE_PLAYER_PLACING_SHIPS;
    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access to game fragments
     */
    public ViewPager mPager;
    /**
     * The gameFragment attributes
     */
    public GameFragmentOpponent gameFragmentOpponent = null;
    public GameFragmentPlayer gameFragmentPlayer = null;
    BattleShipsGame game;
    /**
     * punkte des spielers, bei 0 sind alle schiffe versenk
     */
    private int pointCount = 17;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    /**
     * Member object for the chat services
     */
    private BluetoothService mService = null;
    private ImageButton btn_bluetooth;
    private TextView tv_connectionstate, tv_gamestate;
    /**
     * The Handler that gets information from the BluetoothService
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_CONNECTION_STATE_CHANGE:
                    Log.d(TAG, "Refreshing connection state change");
                    switch (msg.arg1) {
                        case Constants.STATE_CONNECTED:
                            setConnectionState(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            MyColorFilter.filter(getApplicationContext(), btn_bluetooth, R.color.accentColor, R.drawable.bluetooth_connected_black);
                            MainActivity.this.sendMessage(REQUEST_GAME_STATE, 0, 0);
                            break;
                        case Constants.STATE_CONNECTING:
                            setConnectionState(getString(R.string.title_connecting));
                            MyColorFilter.filter(getApplicationContext(), btn_bluetooth, R.color.accentColor, R.drawable.bluetooth_searching_black);
                            break;
                        case Constants.STATE_LISTEN:
                            setConnectionState(getString(R.string.title_listening));
                            MyColorFilter.filter(getApplicationContext(), btn_bluetooth, R.color.accentColor, R.drawable.bluetooth_black);
                            break;
                        case Constants.STATE_NONE:
                            setConnectionState(getString(R.string.title_not_connected));
                            MyColorFilter.filter(getApplicationContext(), btn_bluetooth, R.color.accentColor, R.drawable.bluetooth_black);
                            break;
                        default:
                            break;
                    }
                    break;

                case MESSAGE_READ:
                    // nachricht empfangen, an messageDecoder weiterleiten
                    Log.d(TAG, "Message received, passing on to decoder");
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    messageDecoder(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    Log.d(TAG, "Connected Device's name: " + mConnectedDeviceName);
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Establish a connection with another device
     */
    private void connectDevice(String deviceAddress) {
        Log.d(TAG, "Device selected, attempting to connect to address: " + deviceAddress);
        // get the bluetooth device
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        // Attempt to connect to the device
        mService.connect(device);
    }

    /**
     * two players have to be in the same gamestate when connecting
     */
    private void checkIfConnectionIsLegal(int enemyGameState) {

        if ((enemyGameState == GAMESTATE_PLAYER_PLACING_SHIPS && playerGameState == GAMESTATE_PLAYER_PLACING_SHIPS)
                || (enemyGameState == GAMESTATE_PLAYER_SELECTED_TILE && playerGameState == GAMESTATE_PLAYER_SELECTING_TILE)
                || (enemyGameState == GAMESTATE_PLAYER_SELECTING_TILE && playerGameState == GAMESTATE_PLAYER_SELECTED_TILE)
                || (enemyGameState == GAMESTATE_PLAYER_SELECTED_TILE && playerGameState == GAMESTATE_PLAYER_DONE_PLACING_SHIPS)
                || (enemyGameState == GAMESTATE_PLAYER_DONE_PLACING_SHIPS && playerGameState == GAMESTATE_PLAYER_PLACING_SHIPS)
                || (enemyGameState == GAMESTATE_PLAYER_PLACING_SHIPS && playerGameState == GAMESTATE_PLAYER_DONE_PLACING_SHIPS)

                ) {
        } else {
            Toast.makeText(this, R.string.title_illegal_connection, Toast.LENGTH_LONG).show();
            mService.start();
        }
    }

    private void ensureDiscoverable() {
        // make device discoverable
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 900);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Message decoder, decodes strings from bluetooth connection and decides
     * what to do
     */
    private void messageDecoder(String string) {

        Log.d(TAG, "Decoding Message: " + string);

        String stringRest = "";

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '#') {
                stringRest = string.substring(i);
                string = string.substring(0, i);
            }
        }

        Message msg = Util.stringToMessage(string);
        Log.d(TAG, "messageDecoder: " + msg.what + " " + msg.arg1 + " " + msg.arg2);

        switch (msg.what) {
            case MESSAGE_GAME_STATE_CHANGE:
                switch (msg.arg1) {
                    case GAMESTATE_PLAYER_PLACING_SHIPS:
                        break;
                    case GAMESTATE_PLAYER_DONE_PLACING_SHIPS:
                        gameFragmentPlayer.enemyDonePlacingShips = true;
                        Log.d(TAG, "Enemy done placing Ships");
                        break;
                    case GAMESTATE_PLAYER_SELECTING_TILE:
                        break;
                    case GAMESTATE_PLAYER_SELECTED_TILE:
                        break;
                    default:
                        break;
                }
            case MESSAGE_GAME_EVENT:
                switch (msg.arg1) {
                    case EVENT_ENEMY_FIRED:
                        Log.d(TAG, "Dealing with enemy fire");
                        if (game.isShipHit(msg.arg2)) {
                            Log.d(TAG, "messageDecoder: Enemy hit us");
                            game.addStateTile(BattleShipsGameBoard.P[msg.arg2], StateTile.TILESTATE_HIT, true);
                            sendMessage(MESSAGE_GAME_EVENT, EVENT_HIT, msg.arg2);
                            pointCount--;
                            if (pointCount == 0) {
                                actionGameLost();
                            }
                        } else {
                            Log.d(TAG, "messageDecoder: Enemy missed us");
                            game.addStateTile(BattleShipsGameBoard.P[msg.arg2], StateTile.TILESTATE_MISS, true);
                            sendMessage(MESSAGE_GAME_EVENT, EVENT_MISS, msg.arg2);
                        }
                        setPlayerGameState(GAMESTATE_PLAYER_SELECTING_TILE);
                        break;
                    case EVENT_HIT:
                        Log.d(TAG, "We hit them");
                        game.addStateTile(BattleShipsGameBoard.P[msg.arg2], StateTile.TILESTATE_HIT, false);
                        break;
                    case EVENT_MISS:
                        Log.d(TAG, "We missed them");
                        game.addStateTile(BattleShipsGameBoard.P[msg.arg2], StateTile.TILESTATE_MISS, false);
                        break;
                    case EVENT_SHIP_SUNK:
                        break;
                    case EVENT_ENEMY_WON:
                        sendMessage(MESSAGE_GAME_EVENT, EVENT_PLAYER_WON, pointCount);
                        showGameOverScreen(pointCount, msg.arg2, true);
                        break;
                    case EVENT_PLAYER_WON:
                        showGameOverScreen(pointCount, msg.arg2, false);
                    default:
                        break;
                }

            case REQUEST_GAME_STATE:
                sendMessage(ANSWER_GAME_STATE, this.playerGameState, 0);
                break;
            case ANSWER_GAME_STATE:
                checkIfConnectionIsLegal(msg.arg1);
                break;
            default:
                break;
        }

        if (stringRest.length() > 1) {
            if (stringRest.charAt(0) == '#')
                stringRest = stringRest.substring(1);
            messageDecoder(stringRest);
        }
    }

    private void showGameOverScreen(int pointsplayer, int pointsopponent, boolean winner) {

        // show game over fragment
        GameOverFragment fragment = new GameOverFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(GameOverFragment.KEY_POINTS_OPPONENT, 17 - pointsopponent);
        bundle.putInt(GameOverFragment.KEY_POINTS_PLAYER, 17 - pointsplayer);
        bundle.putBoolean(GameOverFragment.KEY_WINNER, winner);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container_gameover_fragment, fragment).commit();
        mPager.setVisibility(View.GONE);
        tv_gamestate.setVisibility(View.GONE);
        findViewById(R.id.page_indicator).setVisibility(View.GONE);
    }

    private void actionGameLost() {
        sendMessage(MESSAGE_GAME_EVENT, EVENT_ENEMY_WON, pointCount);
    }

    /**
     * Handles the information coming from the selectDevice Activity and
     * connects to a new device
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // when devicelistactivity returns a device to connect to
                if (resultCode == Activity.RESULT_OK) {
                    /*
      MAC adresse des verbundenen geraets, fuer spaetere wiederherstellung der
      verbindung
     */
                    String mConnectedDeviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    connectDevice(mConnectedDeviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // when the request to enable bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    setupGame();
                } else {
                    this.finish();
                }

            default:
                break;
        }
    }

    /**
     * erlaubt es, im pager zurueckzublaettern
     */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the
            // system to handle the
            // Back button. This calls finish() on this activity and pops the
            // back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * onCreate der MainActivity, alle wichtigen views und attribute werden
     * initialisiert
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_main);

//      if the adapter is null the device does not support bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // TODO Fehlermeldung
            this.finish();
        }

        // find layout views
        tv_connectionstate = (TextView) findViewById(R.id.tv_connectionstate);
        tv_gamestate = (TextView) findViewById(R.id.tv_gamestate);

        game = new BattleShipsGame(this);

        // Instantiate the GameFragments
        gameFragmentOpponent = new GameFragmentOpponent();
        gameFragmentOpponent.setGame(this, game);

        gameFragmentPlayer = new GameFragmentPlayer();
        gameFragmentPlayer.setGame(this, game);

        // initiate buttons
        btn_bluetooth = (ImageButton) findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectDevice = new Intent(getApplicationContext(), DeviceListActivity.class);

                startActivityForResult(selectDevice, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });

        MyColorFilter.filter(this, btn_bluetooth, R.color.accentColor, R.drawable.bluetooth_black);
        /*
      Layout Views
     */
        ImageButton btn_settings = (ImageButton) findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                opensettings();
            }
        });

        MyColorFilter.filter(this, btn_settings, R.color.accentColor, R.drawable.settings_black);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        GameFragmentPagerAdapter mPagerAdapter = new GameFragmentPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(NUM_PAGES);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        SimpleViewPagerIndicator pageIndicator = (SimpleViewPagerIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(mPager);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * bluetooth service beendet wenn app geschlossen wird
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mService.getState() == Constants.STATE_NONE) {
                // Start the Bluetooth chat services
                mService.start();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            ensureDiscoverable();
            // Otherwise, setup the game session
        } else if (mService == null) {
            setupGame();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jona.schiffeversenken/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    /**
     * Opens the settings activity
     */
    private void opensettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * sends a message to the handler of the connected device
     */
    public void sendMessage(int what, int arg1, int arg2) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;

        String string = Util.messageToString(msg);

        // Check that we're actually connected before trying anything
        if (mService.getState() != Constants.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            gameFragmentPlayer.btnDone.setVisibility(View.VISIBLE);
            return;
        }

        // Check that there's actually something to send
        if (string.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = string.getBytes();
            mService.write(send);

            // Reset out string buffer to zero
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * Updates the connection state in the UI
     */
    private void setConnectionState(CharSequence status) {
        tv_connectionstate.setText(status);
    }

    /**
     * Updates gameState in UI
     */
    public void setPlayerGameState(int state) {

        playerGameState = state;

        switch (state) {
            case GAMESTATE_PLAYER_DONE_PLACING_SHIPS:
                tv_gamestate.setText(getResources().getString(R.string.state_done_placing_ships));
                break;
            case GAMESTATE_PLAYER_PLACING_SHIPS:
                tv_gamestate.setText(getResources().getString(R.string.state_placing_ships));
                break;
            case GAMESTATE_PLAYER_SELECTED_TILE:
                tv_gamestate.setText(getResources().getString(R.string.state_enemy_selecting_tile));
                break;
            case GAMESTATE_PLAYER_SELECTING_TILE:
                tv_gamestate.setText(getResources().getString(R.string.state_selecting_tile));
                break;

            default:
                break;
        }

    }

    /**
     * set up the UI and the background operations for the game
     */
    private void setupGame() {
        setPlayerGameState(GAMESTATE_PLAYER_PLACING_SHIPS);

        // Initialize the BluetoothService to perfom bluetooth connections
        mService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.jona.schiffeversenken/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * A simple pager adapter that represents 2 ScreenSlidePageFragment objects,
     * in sequence.
     */
    private class GameFragmentPagerAdapter extends FragmentPagerAdapter {
        public GameFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Placing Fragments in pager");
            if (position == 0) {
                return gameFragmentPlayer;
            } else if (position == 1) {
                return gameFragmentOpponent;
            } else {
                return null;
            }
        }
    }
}
