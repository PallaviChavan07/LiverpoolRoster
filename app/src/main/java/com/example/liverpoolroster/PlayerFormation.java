package com.example.liverpoolroster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.example.liverpoolroster.database.PlayerBaseHelper;
import com.example.liverpoolroster.database.PlayerCursorWrapper;
import com.example.liverpoolroster.database.PlayerDbSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerFormation {
    static String DATE_PATTERN = "MM/dd/yyyy";
    private static PlayerFormation playerFormation;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static int i = 0;

    public static PlayerFormation get(Context context) {
        if (playerFormation == null) {
            playerFormation = new PlayerFormation(context);
        }
        return playerFormation;
    }

    private PlayerFormation(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PlayerBaseHelper(mContext).getWritableDatabase();
        //add a pre defined list of players to db
        if (getPlayerDBCount() < 1) AddDefaultPlayerList();
    }

    private PlayerCursorWrapper queryPlayers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PlayerDbSchema.PlayerTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
//                null,
//                null,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new PlayerCursorWrapper(cursor);
    }

    public List<Player> getPlayers() {
        List<Player> Players = new ArrayList<>();
        PlayerCursorWrapper cursor = queryPlayers(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Players.add(cursor.getPlayer());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return Players;
    }

    public void addPlayer(Player p) {
        ContentValues values = getContentValues(p);
        mDatabase.insert(PlayerDbSchema.PlayerTable.NAME, null, values);
    }

    public void removePlayer(Player p) {
        String uuidString = p.getmID().toString();
        mDatabase.delete(PlayerDbSchema.PlayerTable.NAME,
                PlayerDbSchema.PlayerTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public void deletePlayerWithId(String uuidString) {
        mDatabase.delete(PlayerDbSchema.PlayerTable.NAME,
                PlayerDbSchema.PlayerTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public void updatePlayer(Player Player) {
        String uuidString = Player.getmID().toString();
        ContentValues values = getContentValues(Player);
        mDatabase.update(PlayerDbSchema.PlayerTable.NAME, values,
                PlayerDbSchema.PlayerTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public Player getPlayer(UUID id) {
        PlayerCursorWrapper cursor = queryPlayers(
                PlayerDbSchema.PlayerTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getPlayer();
        } finally {
            cursor.close();
        }
    }

    private int getPlayerDBCount() {
        PlayerCursorWrapper cursor = queryPlayers(null, null);
        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Player Player) {
        ContentValues values = new ContentValues();
        values.put(PlayerDbSchema.PlayerTable.Cols.UUID, Player.getmID().toString());
        values.put(PlayerDbSchema.PlayerTable.Cols.NAME, Player.getName());
        values.put(PlayerDbSchema.PlayerTable.Cols.NUMBER, Player.getNumber());
        values.put(PlayerDbSchema.PlayerTable.Cols.BIRTHDATE, Player.getBirthDate().getTime());
        values.put(PlayerDbSchema.PlayerTable.Cols.NATIONALITY, Player.getNationality());
        values.put(PlayerDbSchema.PlayerTable.Cols.POSITION, Player.getPosition());
        values.put(PlayerDbSchema.PlayerTable.Cols.PROFILEURL, Player.getProfileLink());
        return values;
    }

    private void AddDefaultPlayerList() {
        String baseProfileUrl = "https://www.liverpoolfc.com/team/first-team/player/";
        String names[] = new String[]{"A. Becker","M. Salah","R. Firmino","J. Milner","S. Mane","J. Gomez","G. Wijnaldum","A. Robertson","J. Matip","T. Arnold","J. Henderson"};
        String positions[] = new String[]{"Goalkeeper","Forward","Forward","Midfielder","Forward","Defender","Midfielder","Defender","Defender","Defender","Midfielder"};
        String nationalities[] = new String[]{"Brazil","Egypt","Brazil","England","Senegal","England","Netherlands","Scotland","Cameroon","England","England"};
        int nums[] = new int[]{13,11,9,7,19,12,5,26,32,66,14};
        String birthdates[] = new String[]{"10/02/1992","06/15/1992","10/02/1991","01/04/1986","04/10/1992","5/23/1997","11/11/1990","03/11/1994","08/08/1991","10/07/1998","06/17/1990"};
        String playerProfileUrl[] = new String[]{baseProfileUrl+"alisson-becker",baseProfileUrl+"mohamed-salah",baseProfileUrl+"roberto-firmino",baseProfileUrl+"james-milner",baseProfileUrl+"sadio-mane",baseProfileUrl+"joe-gomez",baseProfileUrl+"georginio-wijnaldum",baseProfileUrl+"andy-robertson",baseProfileUrl+"joel-matip",baseProfileUrl+"trent-alexander-arnold",baseProfileUrl+"jordan-henderson"};
        for (int i = 0; i < 10; i++) {
            Player player = new Player();
            player.setName( names[i] );
            player.setPosition( positions[i] );
            player.setNationality( nationalities[i] );
            player.setNumber( nums[i] );
            try {
                player.setBirthDate( new SimpleDateFormat(DATE_PATTERN ).parse( birthdates[i] ) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            player.setProfileLink( playerProfileUrl[i] );
            addPlayer(player);
        }
    }
}
