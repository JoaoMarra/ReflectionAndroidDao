package br.marraware.reflectiondatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public abstract class ReflectionDatabaseManager{

    private static SQLiteOpenHelper dataBaseHelper;

    public final static void initDataBase(SQLiteOpenHelper helper) {
        dataBaseHelper = helper;
        db();
    }

    public static SQLiteOpenHelper getInstance() {
        return dataBaseHelper;
    }

    public static SQLiteDatabase db() {
        return dataBaseHelper.getWritableDatabase();
    }
}
