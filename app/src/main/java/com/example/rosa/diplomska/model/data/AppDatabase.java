package com.example.rosa.diplomska.model.data;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
//TODO: migracija
//migracija bo dodana kasneje
//zaenkrat je brez da ko cem pocistit bazo, samo spremenim verzijo pa rebuildam
//TODO: data sync
//to bo tut prislo kasneje

@Database(entities = { User.class, Post.class}, version = 1)
@TypeConverters({RoomTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "localAdaDatabase.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME).fallbackToDestructiveMigration().build();
    }

    public abstract UserDao getUserDao();
    public abstract PostDao getPostDao();
}