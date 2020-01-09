package com.example.liverpoolroster.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.example.liverpoolroster.Player;
import java.util.Date;
import java.util.UUID;

public class PlayerCursorWrapper extends CursorWrapper {
    public PlayerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Player getPlayer() {
        String uuidString = getString(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.UUID));
        String name = getString(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.NAME));
        int number = getInt(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.NUMBER));
        String position = getString(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.POSITION));
        String nationality = getString(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.NATIONALITY));
        long birthDate = getLong(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.BIRTHDATE));
        String profileUrlString = getString(getColumnIndex(PlayerDbSchema.PlayerTable.Cols.PROFILEURL));

        Player p = new Player(UUID.fromString(uuidString));
        p.setName(name);
        p.setNumber(number);
        p.setPosition(position);
        p.setNationality(nationality);
        p.setBirthDate(new Date(birthDate));
        p.setProfileLink( profileUrlString );

        return p;
    }
}
