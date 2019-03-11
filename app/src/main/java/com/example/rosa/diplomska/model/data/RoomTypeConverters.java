package com.example.rosa.diplomska.model.data;


import android.arch.persistence.room.TypeConverter;

import java.sql.Time;
import java.sql.Timestamp;

public class RoomTypeConverters {

    @TypeConverter
    public static int fromBoolean(boolean v) {
        return v ? 1 : 0;

    }
    @TypeConverter
    public static boolean toBoolean(int v) {
        return v == 1;
    }

    @TypeConverter
    public static String fromTimestamp(Timestamp t) {
        return null;
    }

    @TypeConverter
    public static Timestamp toTimeStamp(String s) {
        return null;
    }

}
