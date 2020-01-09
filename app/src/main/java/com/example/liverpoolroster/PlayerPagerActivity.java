package com.example.liverpoolroster;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.List;
import java.util.UUID;

public class PlayerPagerActivity extends AppCompatActivity {
    private static final String EXTRA_PLAYER_ID = "com.example.liverpoolroster.player_id";
    private ViewPager mViewPager;
    private List<Player> mPlayers;
    boolean newPlayerFlag;
    private Player player;

    public static Intent newIntent(Context packageContext, UUID playerId) {
        Intent intent = new Intent(packageContext, PlayerPagerActivity.class);
        intent.putExtra(EXTRA_PLAYER_ID, playerId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_player_pager );

        UUID playerId = (UUID) getIntent().getSerializableExtra(EXTRA_PLAYER_ID);
        newPlayerFlag = getIntent().getBooleanExtra( "newPlayerFlag",false );
        mViewPager = (ViewPager) findViewById(R.id.player_view_pager);

        mPlayers = PlayerFormation.get(this).getPlayers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                player = mPlayers.get(position);
                return PlayerFragment.newInstance(player.getmID());
            }

            @Override
            public int getCount() {
                return mPlayers.size();
            }
        });
        for (int i = 0; i < mPlayers.size(); i++) {
            if (mPlayers.get(i).getmID().equals(playerId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<Fragment> activePlayerFragments = getSupportFragmentManager().getFragments();

        for (int i = 0; i < activePlayerFragments.size(); i++) {
            if (activePlayerFragments.get(i) instanceof PlayerFragment && newPlayerFlag) {
                if (!player.getmID().toString().equals(((PlayerFragment) activePlayerFragments.get(i)).getPlayerID().toString())) {
                    String uuidofplayertodelete = ((PlayerFragment) activePlayerFragments.get(i)).getPlayerID().toString();
                    PlayerFormation.get(this).deletePlayerWithId(uuidofplayertodelete);
                }
            }
        }
        newPlayerFlag = false;
    }
}
