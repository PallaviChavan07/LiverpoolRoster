package com.example.liverpoolroster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import static java.time.Period.between;

public class PlayerFragment extends Fragment {
    private static final String ARG_PLAYER_ID = "player_id";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_IMAGE = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private Player mPlayer;
    private EditText mPlayerName;
    private EditText mPlayerNumber;
    private EditText mPlayerPosition;
    private EditText mPlayerNationality;
    private EditText mPlayerDateofBirth;
    private Button mSavePlayerButton;
    private ImageView mPlayerImgView;
    private Date bdate;
    private boolean editTextFlag;
    private TextView playerProfileUrllTextView;
    private EditText profileEditText;
    AlertDialog.Builder deletePlayerAlertDialog;
    public static boolean newPlayerFlag;
    private UUID playerID;

    public UUID getPlayerID() {
        return playerID;
    }

    public static PlayerFragment newInstance(UUID playerId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYER_ID, playerId);
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerID = (UUID) getArguments().getSerializable(ARG_PLAYER_ID);
        mPlayer = PlayerFormation.get(getActivity()).getPlayer(playerID);
        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) newPlayerFlag = extras.getBoolean( "newPlayerFlag" );
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_player, container, false);
        mPlayerName = (EditText) v.findViewById(R.id.txtPlayerName);
        mPlayerNumber = (EditText)v.findViewById( R.id.txtPlayerNumber );
        mPlayerPosition = (EditText)v.findViewById( R.id.txtPlayerPosition );
        mPlayerNationality = (EditText)v.findViewById( R.id.txtPlayerNationality );
        mPlayerDateofBirth = (EditText)v.findViewById( R.id.txtPlayerBirthdate );
        mSavePlayerButton = (Button)v.findViewById( R.id.savePlayerButton );
        mPlayerImgView = (ImageView)v.findViewById( R.id.detailPlayerImgView );
        playerProfileUrllTextView = (TextView)v.findViewById( R.id.txtPlayerProfileUrl );
        profileEditText = (EditText)v.findViewById( R.id.profileEdtTxt );
        profileEditText.setVisibility( View.GONE );

        mPlayerName.setText(mPlayer.getName());
        mPlayerNumber.setText(String.valueOf( mPlayer.getNumber() ));
        mPlayerPosition.setText(mPlayer.getPosition());
        mPlayerNationality.setText(mPlayer.getNationality());
        new DownloadImageTask(mPlayerImgView).execute(mPlayer.getName());
        //mPlayerImgView.setImageResource( getPlayerImageResource() );
        //playerProfileUrllTextView.setText(  );

        if(newPlayerFlag){
            editTextFlag = true;
            showProfileEditText();
            getActivity().setTitle("Add a new player");
        }else{
            getActivity().setTitle("Player Information");
        }

        enableDisableEditTexts( editTextFlag );
        mSavePlayerButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(StringUtils.isBlank( mPlayerName.getText().toString() )){
                    Toast.makeText(getActivity(), R.string.nameValidateMsg,
                            Toast.LENGTH_LONG).show();
                }else if(StringUtils.isBlank( mPlayerNumber.getText().toString() ) || !StringUtils.isNumeric( mPlayerNumber.getText().toString() )){
                    Toast.makeText(getActivity(), R.string.numberValidateMsg,
                            Toast.LENGTH_LONG).show();
                }else if(StringUtils.isBlank( mPlayerPosition.getText().toString() )){
                    Toast.makeText(getActivity(), R.string.positionValidateMsg,
                            Toast.LENGTH_LONG).show();
                }else if(StringUtils.isBlank( mPlayerNationality.getText().toString() )){
                    Toast.makeText(getActivity(), R.string.nationalityValidateMsg,
                            Toast.LENGTH_LONG).show();
                }else if(StringUtils.isBlank( mPlayerDateofBirth.getText().toString() )){
                    Toast.makeText(getActivity(), R.string.bdateValidateMsg,
                            Toast.LENGTH_LONG).show();
                }else{
                    mPlayer.setName( mPlayerName.getText().toString() );
                    mPlayer.setPosition( mPlayerPosition.getText().toString() );
                    mPlayer.setNumber( Integer.parseInt( mPlayerNumber.getText().toString() ) );
                    mPlayer.setNationality( mPlayerNationality.getText().toString() );
                    mPlayer.setBirthDate( bdate );
                    mPlayer.setProfileLink( profileEditText.getText().toString() );
                    PlayerFormation.get(getActivity()).updatePlayer(mPlayer);
                    Toast.makeText( getActivity(), R.string.save_toast, Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( getActivity(), PlayerListActivity.class );
                    startActivity( intent );
                }

            }
        } );
//        mPlayerImgView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(editTextFlag){
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE);
//                }
//
//            }
//        } );

        updateDate();
        mPlayerDateofBirth.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextFlag){
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mPlayer.getBirthDate());
                    dialog.setTargetFragment(PlayerFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                }

            }
        } );

        playerProfileUrllTextView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer.getProfileLink() != null && mPlayer.getProfileLink().length()>0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlayer.getProfileLink()));
                    Intent browserChooserIntent = Intent.createChooser(browserIntent , "Choose browser of your choice");
                    startActivity(browserChooserIntent );
                }else{
                    Toast.makeText(getActivity(), R.string.profile_not_exist, Toast.LENGTH_SHORT).show();
                }

            }
        } );
        return v;
    }

    public void showProfileEditText() {
        profileEditText.setVisibility( View.VISIBLE );
        profileEditText.setText( mPlayer.getProfileLink() );
        profileEditText.setInputType( InputType.TYPE_NULL|InputType.TYPE_TEXT_FLAG_MULTI_LINE );
        playerProfileUrllTextView.setText( "Profile Link" );
        playerProfileUrllTextView.setTextColor( Color.WHITE );
    }


/*
    public int getPlayerImageResource(){
        int imageResource = 0;
        String uri = "@drawable/";
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
        return imageResource;
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            bdate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPlayerDateofBirth.setText( DateFormatUtils.format(bdate, PlayerFormation.DATE_PATTERN) );
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        mPlayerImgView.setImageBitmap( bitmap );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDate() {
        bdate = mPlayer.getBirthDate();
        mPlayerDateofBirth.setText(mPlayer.getBirthDate() == null? "" : DateFormatUtils.format(mPlayer.getBirthDate(), PlayerFormation.DATE_PATTERN) );
    }

    public void enableDisableEditTexts(boolean editTextFlag){
        mPlayerName.setFocusable( editTextFlag );
        mPlayerName.setFocusableInTouchMode( editTextFlag );
        mPlayerNumber.setFocusable( editTextFlag );
        mPlayerNumber.setFocusableInTouchMode( editTextFlag );
        mPlayerPosition.setFocusable( editTextFlag );
        mPlayerPosition.setFocusableInTouchMode( editTextFlag );
        mPlayerNationality.setFocusable( editTextFlag );
        mPlayerNationality.setFocusableInTouchMode( editTextFlag );
        mPlayerDateofBirth.setFocusable( editTextFlag );
        mPlayerDateofBirth.setFocusableInTouchMode( editTextFlag );
        mPlayerImgView.setClickable( editTextFlag );

        if(editTextFlag)
            mSavePlayerButton.setVisibility( View.VISIBLE );
        else
            mSavePlayerButton.setVisibility( View.INVISIBLE );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_player, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_player:
                editTextFlag = true;
                enableDisableEditTexts(true);
                showProfileEditText();
                return true;
            case R.id.delete_player:
                deletePlayerAlertDialog = new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure ")
                        .setMessage("You want to remove player from roster?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removePlayerFromRoster();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                deletePlayerAlertDialog.show();

                return  true;
            case R.id.share_player:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getPlayerDetails());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.player_detial_subject));
                i = Intent.createChooser(i, getString(R.string.send_playerdetails));
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getPlayerDetails() {
        String playerDetails;

        String clubName = getString( R.string.player_detial_subject );
        String name = getString( R.string.player_detial_name , mPlayer.getName() );
        String position = getString( R.string.player_detial_position , mPlayer.getPosition() );
        String nationality = getString( R.string.player_detial_nationality , mPlayer.getNationality() );
        playerDetails = getString( R.string.player_detail, clubName, name, position, nationality  );
        return playerDetails;

    }

    public void removePlayerFromRoster(){
        PlayerFormation.get(getActivity()).removePlayer(mPlayer);
        Toast.makeText(getActivity(), mPlayer.getName()+" has been deleted. ", Toast.LENGTH_SHORT ).show();
        getActivity().finish();
    }

    @SuppressLint("NewApi")
    public int getPlayerAge(LocalDate birthDate, LocalDate currentDate) {
        if ((birthDate != null) && (currentDate != null)) {
            return between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }

    /**** WEB API CODE  ****/
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... names) {
            String playerName = names[0];
            Bitmap bmp = null;
            try {
                if(playerName != null && playerName.trim().length()>0){
                    String [] splitName = playerName.toLowerCase().split( " " );
                    if(splitName.length > 1){
                        playerName = splitName[1];
                    }else
                        playerName = splitName[0];
                }
                else playerName = "placeholder";    //new player

                bmp = new Utils().DownloadFromFlickr(playerName);
                //if there is no image url then download place holder image as a fail safe
                if(bmp == null) bmp = new Utils().DownloadFromFlickr("placeholder");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
           // Bitmap pngResult = getPNGTransparentImage(result);



            bmImage.setImageBitmap(result);
            bmImage.setBackgroundResource( R.color.trans );
        }
    }

//    public Bitmap getPNGTransparentImage(Bitmap result)  {
//        //File storageDir = Environment.getExternalStoragePublicDirectory(
//          //      Environment.DIRECTORY_PICTURES);
//        File imageFile = null;
//        File externalRoot = Environment.getExternalStorageDirectory();
//        File tempDir = new File(externalRoot, "temp");
//
//        try {
//            imageFile = File.createTempFile(
//                    mPlayer.getName(),  /* prefix */
//                    ".png",         /* suffix */
//                    tempDir      /* directory */
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        FileOutputStream outStream = null;
//        InputStream in = null;
//        try {
//            outStream = new FileOutputStream(imageFile);
//            result.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            outStream.flush();
//            outStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//
//    }
}
