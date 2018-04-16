package br.marraware.reflectionandroiddao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;

/**
 * Created by joao_gabriel on 21/07/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int SCHEMA_VERSION = 2;

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DatabaseHelper createBase(Context context) {
        DatabaseHelper instance = new DatabaseHelper(context.getApplicationContext(), "crm-mts-db", null, SCHEMA_VERSION);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ReflectionDatabaseManager.createTable(TestModel.class,db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ReflectionDatabaseManager.dropTable(TestModel.class,db);
        onCreate(db);
    }
}
