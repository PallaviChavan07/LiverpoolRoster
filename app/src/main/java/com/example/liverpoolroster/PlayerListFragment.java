package com.example.liverpoolroster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class PlayerListFragment extends Fragment {
    private RecyclerView mPlayerRecyclerView;
    private PlayerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_player_list, container, false );
        mPlayerRecyclerView = (RecyclerView) view.findViewById( R.id.player_recycler_view );
        mPlayerRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        updateUI();
        return view;
    }

    private class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Player mPlayer;
        private TextView mPlayerNameTextview;
        private TextView mPlayerPositionTextview;
        private ImageView mPlayerImageView;

        public PlayerHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_player, parent, false));
            itemView.setOnClickListener(this);
            mPlayerNameTextview = (TextView) itemView.findViewById( R.id.player_name );
            mPlayerPositionTextview = (TextView) itemView.findViewById( R.id.player_position );
            mPlayerImageView = (ImageView)itemView.findViewById( R.id.player_image );
        }

        public void bind(Player player) {
            String uri = "@drawable/";
            int imageResource = 0;
            mPlayer = player;
            mPlayerNameTextview.setText(mPlayer.getName() );
            mPlayerPositionTextview.setText( mPlayer.getPosition() );

            if(mPlayer.getName() != null && mPlayer.getName().trim().length()>0){
                String [] splitName = mPlayer.getName().toLowerCase().split( " " );
                if(splitName.length > 1){
                    uri = uri.concat( splitName[1] );
                }else
                    uri = uri.concat( splitName[0] );
                imageResource = getResources().getIdentifier( uri, null , "com.example.liverpoolroster");
            }
            if(imageResource <= 0){
                uri = "@drawable/placeholder";
                imageResource = getResources().getIdentifier( uri, null , "com.example.liverpoolroster");
            }
            mPlayerImageView.setImageResource( imageResource );
        }


        @Override
        public void onClick(View view) {
            Intent intent = PlayerPagerActivity.newIntent(getActivity(), mPlayer.getmID());
            startActivity(intent);
        }

    }

    private class PlayerAdapter extends RecyclerView.Adapter<PlayerHolder> {
        private List<Player> mPlayers;
        private PlayerAdapter() { }
        private void setPlayers(List<Player> players) {
            mPlayers = players;
        }

        @Override
        public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PlayerHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PlayerHolder holder, final int position) {
            final Player player = mPlayers.get(position);
            holder.bind(player);
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_player_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_player:
                Player player = new Player();
                PlayerFormation.get(getActivity()).addPlayer(player);
                Intent intent = PlayerPagerActivity.newIntent(getActivity(), player.getmID());
                intent.putExtra( "newPlayerFlag",true );
                startActivity(intent);
                return true;
            case R.id.location:
                Uri loc = Uri.parse("geo:0,0?q=Anfield Rd, Liverpool L4 0TH, UK");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, loc);
                startActivity(mapIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        PlayerFormation playerFormation = PlayerFormation.get(getActivity());
        List<Player> players = playerFormation.getPlayers();

        if (mAdapter == null) {
            mAdapter = new PlayerAdapter();
            mAdapter.setPlayers(players);
            mPlayerRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPlayers(players);
            mAdapter.notifyDataSetChanged();
        }
    }
}
