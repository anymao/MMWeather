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

public class AllCitiesDataOpenHelper extends SQLiteOpenHelper{
    private static final String tag = "AllCitiesDataOpenHelper";
    private Context mContext;
    private static final String CREATE_TABLE_ALL_CITIES  = "create table ALL_CITIES (" +
            "id integer primary key ," +
            "province text ," +
            "city text ," +
            "district text)";
    public AllCitiesDataOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALL_CITIES);
        initAllCities(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private void initAllCities(final SQLiteDatabase db) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                LogUtil.v(tag,"in run");
                try {
                    Reader reader = new InputStreamReader(
                            mContext.getAssets().open("cities.json"));
                    List<CityEntity> cities = gson.fromJson(reader,new TypeToken<List<CityEntity>>()
                    {}.getType());
                    ContentValues values = new ContentValues();
                    for (CityEntity city : cities) {
                        values.put("id",city.getId());
                        values.put("city",city.getCity());
                        values.put("province",city.getProvince());
                        values.put("district",city.getDistrict());
                        db.insert("ALL_CITIES",null,values);
                        values.clear();
                    }
                    db.close();
                    LogUtil.v(tag,"所有城市列表初始化完毕..共"+cities.size()+"条数据");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
