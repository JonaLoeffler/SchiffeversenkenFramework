package com.jona.schiffeversenken;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameOverFragment extends android.support.v4.app.Fragment {

    public static final String KEY_POINTS_OPPONENT = "pointsopponent";
    public static final String KEY_POINTS_PLAYER = "pointsplayer";
    public static final String KEY_WINNER = "winner";

    int pointsOpponent;
    int pointsPlayer;
    boolean winner;
    TextView tvPointsOpponent, tvPointsPlayer, winOrLose;

    public void setArguments(Bundle bundle) {
        this.pointsOpponent = bundle.getInt(KEY_POINTS_OPPONENT);
        this.pointsPlayer = bundle.getInt(KEY_POINTS_PLAYER);
        this.winner = bundle.getBoolean(KEY_WINNER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_over, container, false);

        tvPointsOpponent = (TextView) rootView.findViewById(R.id.tv_points_opponent);
        tvPointsPlayer = (TextView) rootView.findViewById(R.id.tv_points_player);
        winOrLose = (TextView) rootView.findViewById(R.id.tv_win_or_lose);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        tvPointsOpponent.setText("" + pointsOpponent);
        tvPointsPlayer.setText("" + pointsPlayer);

        if (winner) {
            winOrLose.setText(getResources().getString(R.string.you_win));
        } else {
            winOrLose.setText(getResources().getString(R.string.you_lose));
        }
    }

}
