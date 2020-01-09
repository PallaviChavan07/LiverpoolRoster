package com.example.liverpoolroster;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class Player {
    private UUID mID;
    private String name;
    private int number;
    private String position;
    private String nationality;
    private Date birthDate;
    private String profileLink;

    public Player() {
        mID = UUID.randomUUID();
        birthDate = new Date(  );
    }

    public Player(UUID id) {
        mID = id;
        birthDate = new Date(  );
    }

    public UUID getmID() {
        return mID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }
}
