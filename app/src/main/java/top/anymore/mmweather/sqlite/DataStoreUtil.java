package top.anymore.mmweather.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.entity.WeatherDataEntity;
import top.anymore.mmweather.gsonutil.GsonUtil;
import top.anymore.mmweather.logutil.LogUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class DataStoreUtil {
    private static final String tag = "DataStoreUtil";
    private DataOpenHelper mDataOpenHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private GsonUtil mGsonUtil;
    public static final String ACTION_DEFAULT_CITY_CHANGED = "top.any-more.mmweather.ACTION_DEFAULT_CITY_CHANGED";
    public static final String EXTRA_CITY_INFO = "top.any-more.mmweather.EXTRA_CITY_INFO";
    public static final String ACTION_ADD_NEW_CITY = "top.any-more.mmweather.ACTION_ADD_NEW_CITY";
    public DataStoreUtil(String dbName, Context context) {
        mDataOpenHelper = new DataOpenHelper(context,dbName,null,1);
        mContext = context;
        mDatabase = mDataOpenHelper.getWritableDatabase();
        mGsonUtil = GsonUtil.getInstance();
    }

    public void storeWeatherData(int id,String weatherData){
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("weather",weatherData);
        int result = mDatabase.update("WEATHER_DATA",values,"id = ?",new String[]{""+id});
        if (result == 0){
            mDatabase.insert("WEATHER_DATA",null,values);
            LogUtil.v(tag,"不存在这个城市的数据，首次添加");
        }else {
            LogUtil.v(tag,"存在这个城市的数据，更新成功");
        }
    }
    public WeatherDataEntity getWeatherData(int id){
        Cursor cursor = mDatabase.query("WEATHER_DATA",null,"id = ?",new String[]{id+""},null,null,null);
        WeatherDataEntity entity = null;
        if (cursor.moveToFirst()){
            String data = cursor.getString(cursor.getColumnIndex("weather"));
            entity = mGsonUtil.json2entity(data);
        }
        cursor.close();
        return entity;
    }
    public boolean unSubscribeCity(CityEntity cityEntity){
        int result = mDatabase.delete("SUBSCRIBED_CITIES","id = ?",new String[]{cityEntity.getId()+""});
        if (result == 1){
            Toast.makeText(mContext,"删除城市成功...",Toast.LENGTH_SHORT).show();
            mDatabase.delete("WEATHER_DATA","id = ?",new String[]{cityEntity.getId()+""});
            return true;
        }else {
            Toast.makeText(mContext,"删除城市失败...",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public boolean subscribeCity(CityEntity cityEntity){
        int id = cityEntity.getId();
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("province",cityEntity.getProvince());
        values.put("city",cityEntity.getCity());
        values.put("district",cityEntity.getDistrict());
        Cursor cursor = mDatabase.query("SUBSCRIBED_CITIES",null,"id = ?",new String[]{id+""},null,null,null);
        if (cursor.moveToFirst()){
            cursor.close();
            Toast.makeText(mContext,"您已经添加过该城市信息...",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            mDatabase.insert("SUBSCRIBED_CITIES",null,values);
            Intent intent = new Intent(ACTION_ADD_NEW_CITY);
            intent.putExtra(EXTRA_CITY_INFO,cityEntity);
            mContext.sendBroadcast(intent);
            Toast.makeText(mContext,"添加成功...",Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    public List<CityEntity> getSubscribedCities(){
        List<CityEntity> cityEntities = new ArrayList<>();
        Cursor cursor = mDatabase.query("SUBSCRIBED_CITIES",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                CityEntity entity = new CityEntity();
                entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProvince(cursor.getString(cursor.getColumnIndex("province")));
                entity.setCity(cursor.getString(cursor.getColumnIndex("city")));
                entity.setDistrict(cursor.getString(cursor.getColumnIndex("district")));
                cityEntities.add(entity);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return cityEntities;
    }
    public void setDefalutCity(CityEntity cityEntity){
        ContentValues values = new ContentValues();
        values.put("id",0);
        values.put("province",cityEntity.getProvince());
        values.put("city",cityEntity.getCity());
        values.put("district",cityEntity.getDistrict());
        int result = mDatabase.update("SUBSCRIBED_CITIES",values,"id = ?",new String[]{"0"});
        if (result == 0){
            mDatabase.insert("SUBSCRIBED_CITIES",null,values);
        }
        Intent intent = new Intent(ACTION_DEFAULT_CITY_CHANGED);
        intent.putExtra(EXTRA_CITY_INFO,cityEntity);
        mContext.sendBroadcast(intent);
    }
    public SQLiteDatabase getDatabase(){
        return mDatabase;
    }
}
