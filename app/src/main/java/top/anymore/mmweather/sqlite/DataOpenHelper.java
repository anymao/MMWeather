package top.anymore.mmweather.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.logutil.LogUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class DataOpenHelper extends SQLiteOpenHelper{
    private static final String tag = "DataOpenHelper";
    private Context mContext;
    private static final String CREATE_TABLE_WEATHER_DATA = "create table WEATHER_DATA (" +
            "id integer primary key ," +
            "weather text)";
    private static final String CREATE_TABLE_SUBSCRIBED_CITIES = "create table SUBSCRIBED_CITIES (" +
            "id integer primary key ," +
            "province text ," +
            "city text ," +
            "district text)";

    public DataOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WEATHER_DATA);
        db.execSQL(CREATE_TABLE_SUBSCRIBED_CITIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
