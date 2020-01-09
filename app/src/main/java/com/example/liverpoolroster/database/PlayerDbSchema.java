package com.example.liverpoolroster.database;

public class PlayerDbSchema {
    public static final class PlayerTable {
        public static final String NAME = "lfcplayers";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String NUMBER = "number";
            public static final String POSITION = "position";
            public static final String NATIONALITY = "nationality";
            public static final String BIRTHDATE = "birthDate";
            public static final String PROFILEURL = "profileUrl";
        }
    }
}
