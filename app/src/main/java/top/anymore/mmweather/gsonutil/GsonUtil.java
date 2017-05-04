package top.anymore.mmweather.gsonutil;

import com.google.gson.Gson;

import top.anymore.mmweather.entity.WeatherDataEntity;

/**
 * Created by anymore on 17-5-3.
 */

public class GsonUtil {
    private static final String tag = "GsonUtil";
    private Gson mGson;
    private static GsonUtil instance;

    private GsonUtil(){
        mGson = new Gson();
    }
    public static GsonUtil getInstance(){
        if (instance == null){
            synchronized (GsonUtil.class){
                if (instance == null){
                    instance = new GsonUtil();
                }
            }
        }
        return instance;
    }
    public WeatherDataEntity json2entity(String result){
        return mGson.fromJson(result,WeatherDataEntity.class);
    }
    public String entity2json(WeatherDataEntity entity){
        return mGson.toJson(entity);
    }
}
