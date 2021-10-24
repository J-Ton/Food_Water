package com.example.foodwater.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.foodwater.R;

public final class SqliteHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Food_and_Water_db";
    private static final String TABLE_STATS_WATER = "statsWater";
    private static final String TABLE_STATS_EAT = "statsEat";
    private static final String TABLE_FACTS_WATER = "FactsWater";
    private static final String TABLE_FACTS_EAT = "FactsEat";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_INTAKE = "intook";
    private static final String KEY_TOTAL_INTAKE = "totalintake";
    private static final String KEY_FACT= "fact";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate( SQLiteDatabase db) {
        String CREATE_STATS_TABLE_WATER = "CREATE TABLE " + TABLE_STATS_WATER + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE," + KEY_INTAKE + " INT," + KEY_TOTAL_INTAKE + " INT" + ")";
        String CREATE_STATS_TABLE_EAT = "CREATE TABLE " + TABLE_STATS_EAT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT UNIQUE," + KEY_INTAKE + " INT," + KEY_TOTAL_INTAKE + " INT" + ")";
        String CREATE_TABLE_FACTS_WATER = "CREATE TABLE " + TABLE_FACTS_WATER + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FACT + " TEXT" + ")";
        String CREATE_TABLE_FACTS_EAT = "CREATE TABLE " + TABLE_FACTS_EAT + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FACT + " TEXT" + ")";
        if (db != null) {
            db.execSQL(CREATE_STATS_TABLE_EAT);
            db.execSQL(CREATE_STATS_TABLE_WATER);
            db.execSQL(CREATE_TABLE_FACTS_WATER);
            db.execSQL(CREATE_TABLE_FACTS_EAT);

            //Wypelnienie tabeli faktów
            ContentValues values = new ContentValues();
            int length;
            String[] factsWater = context.getResources().getStringArray(R.array.factsWater);
            length = factsWater.length;
            for (int i = 0; i < length; i++) {
                values.put(KEY_FACT, factsWater[i]);
                db.insert(TABLE_FACTS_WATER, null, values);
            }
            String[] factsEat = context.getResources().getStringArray(R.array.factsFood);
            length = factsEat.length;
            for (int i = 0; i < length; i++) {
                values.put(KEY_FACT, factsEat[i]);
                db.insert(TABLE_FACTS_EAT, null, values);
            }
        }

    }

    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS_WATER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS_EAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACTS_WATER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACTS_EAT);
        this.onCreate(db);
    }

    public final long addAll( String date, int intake, int totalintake, boolean flagEat) {
        if (this.checkExistance(date, flagEat) == 0) {
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, date);
            values.put(KEY_INTAKE, intake);
            values.put(KEY_TOTAL_INTAKE, totalintake);
            SQLiteDatabase db = this.getWritableDatabase();
            long response;
            if(flagEat){
                response = db.insert(TABLE_STATS_EAT, null, values);
            }else{
                response = db.insert(TABLE_STATS_WATER, null, values);
            }
            db.close();
            return response;
        } else {
            return -1;
        }
    }

    public final int getIntake(String date, boolean flagEat) {
        String selectQuery;
        if(flagEat){
            selectQuery = "SELECT " + KEY_INTAKE + " FROM " + TABLE_STATS_EAT + " WHERE " + KEY_DATE + " = ?";
        }else{
            selectQuery = "SELECT " + KEY_INTAKE + " FROM " + TABLE_STATS_WATER + " WHERE " + KEY_DATE + " = ?";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor it = db.rawQuery(selectQuery, new String[]{date});
        if (it.moveToFirst()) {
            return it.getInt(it.getColumnIndex(KEY_INTAKE));
        }
        return 0;
    }

    public final int addIntake(String date, int selectedOption, boolean flagEat) {
        int intook = this.getIntake(date, flagEat);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_INTAKE, intook + selectedOption);
        int response;
        if(flagEat){
            response = db.update(TABLE_STATS_EAT, contentValues, KEY_DATE + " = ?", new String[]{date});
        }else{
            response = db.update(TABLE_STATS_WATER, contentValues, KEY_DATE + " = ?", new String[]{date});
        }
        db.close();
        return response;
    }

    public final int checkExistance( String date, boolean flagEat) {
        String selectQuery;
        if(flagEat){
            selectQuery = "SELECT " + KEY_INTAKE + " FROM " + TABLE_STATS_EAT + " WHERE " + KEY_DATE + " = ?";
        }else{
            selectQuery = "SELECT " + KEY_INTAKE + " FROM " + TABLE_STATS_WATER + " WHERE " + KEY_DATE + " = ?";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor it = db.rawQuery(selectQuery, new String[]{date});
        if (it.moveToFirst()) {
            return it.getCount();
        }
        return 0;
    }


    public final Cursor getAllStats(boolean flagEat) {
        String selectQuery;
        if(flagEat){
            selectQuery = "SELECT * FROM " + TABLE_STATS_EAT;
        }else{
            selectQuery = "SELECT * FROM " + TABLE_STATS_WATER;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public final int updateTotalIntake( String date, int totalintake, boolean flagEat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TOTAL_INTAKE, totalintake);
        int response;
        if(flagEat){
            response = db.update(TABLE_STATS_EAT, contentValues, KEY_DATE + " = ?", new String[]{date});
        }else{
            response = db.update(TABLE_STATS_WATER, contentValues, KEY_DATE + " = ?", new String[]{date});
        }
        db.close();
        return response;
    }

    public final Cursor getFacts(boolean flagEat){
        String selectQuery;
        if(flagEat){
            selectQuery = "SELECT * FROM " + TABLE_FACTS_EAT;
        } else {
            selectQuery = "SELECT * FROM " + TABLE_FACTS_WATER;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public final int getIntakeWater( String date) {
        String selectQuery = "SELECT " + KEY_TOTAL_INTAKE + " FROM " + TABLE_STATS_WATER + " WHERE " + KEY_DATE + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor it = db.rawQuery(selectQuery, new String[]{date});
        if (it.moveToFirst()) {
            return it.getInt(it.getColumnIndex(KEY_TOTAL_INTAKE));
        }
        return 0;
    }
}
