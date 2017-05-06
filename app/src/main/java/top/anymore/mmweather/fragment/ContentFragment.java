package top.anymore.mmweather.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.anymore.mmweather.R;
import top.anymore.mmweather.activity.MainActivity;
import top.anymore.mmweather.adapter.WeatherAdapter;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.entity.FutureWeather;
import top.anymore.mmweather.entity.WeatherDataEntity;
import top.anymore.mmweather.gsonutil.GsonUtil;
import top.anymore.mmweather.logutil.LogUtil;
import top.anymore.mmweather.sqlite.DataStoreUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class ContentFragment extends Fragment{
    private static final String testurl = "http://www.any-more.top/weather/weather.json";
    private static final String tag = "ContentFragment";
    private static final String key = "你的Key";
    private BDLocation mBdLocation;
    private int cityid;
    private String cityname;
    private SwipeRefreshLayout srfRefresh;
    private RecyclerView rvWeatherDatas;
    private OkHttpClient mOkHttpClient;
    private GsonUtil mGsonUtil;
    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_FAILED = 1;
    private DataStoreUtil mDataStoreUtil;
    private WeatherAdapter mWeatherAdapter;
    private List<FutureWeather> futureWeathers;
    private MainActivity mainActivity;
    private CityEntity mCityEntity;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (srfRefresh.isRefreshing()){
                srfRefresh.setRefreshing(false);
            }
            switch (msg.what){
                case RESULT_SUCCESS:
//                    mainActivity.mActionBar.setTitle(cityname);
                    mWeatherAdapter.notifyDataSetChanged();
                    break;
                case RESULT_FAILED:
                    Toast.makeText(getContext(),"access internet failed",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    public static ContentFragment newInstance(CityEntity cityEntity){
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("iscity",true);
        bundle.putSerializable("city",cityEntity);
        fragment.setArguments(bundle);
        return fragment;
    }
    public static ContentFragment newInstance(BDLocation bdLocation){
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("iscityid",false);
        bundle.putParcelable("location",bdLocation);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .build();
        mGsonUtil = GsonUtil.getInstance();
        mDataStoreUtil = new DataStoreUtil("mmweather.db",getContext());
        futureWeathers = new ArrayList<>();
        mWeatherAdapter = new WeatherAdapter(futureWeathers,getContext());
        mainActivity = (MainActivity) getActivity();
        Bundle args = getArguments();
        if (args.getBoolean("iscity")){
            mCityEntity = (CityEntity) args.getSerializable("city");
        }else {
            mBdLocation = args.getParcelable("location");
            mCityEntity = new CityEntity();
            mCityEntity.setId(0);
            mCityEntity.setProvince(mBdLocation.getProvince());
            mCityEntity.setCity(mBdLocation.getCity());
            mCityEntity.setDistrict(mBdLocation.getDistrict());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_middle,container,false);
        srfRefresh = (SwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        rvWeatherDatas = (RecyclerView) view.findViewById(R.id.rv_weather_datas);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        LogUtil.v(tag,mBdLocation.getCity());
        initView();
        getRecordWeather();
        getWeather();
    }

    private void initView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvWeatherDatas.setLayoutManager(layoutManager);
        rvWeatherDatas.setAdapter(mWeatherAdapter);
        srfRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeather();
            }
        });
    }

    private void getWeather(){
        String url = "";
        if (mBdLocation != null){
            url = getURL(mBdLocation);
        }else {
            url = getURL(mCityEntity.getId());
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = RESULT_FAILED;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                WeatherDataEntity entity = mGsonUtil.json2entity(result);
                if (mBdLocation != null){
                    mDataStoreUtil.storeWeatherData(0,result);
                }else {
                    mDataStoreUtil.storeWeatherData(mCityEntity.getId(),result);
                }
                cityname = entity.getResult().getToday().getCity();
                mWeatherAdapter.setTodayWeather(entity.getResult().getToday());
                futureWeathers.clear();
                futureWeathers.addAll(Arrays.asList(entity.getResult().getFuture()));
                Message message = Message.obtain();
                message.what = RESULT_SUCCESS;
                mHandler.sendMessage(message);
            }
        });
    }
    private String getURL(int id){
        //添加一个可用的key，解除下面的注释就可以了
        //根据城市id获取url的逻辑
//        String url = "http://v.juhe.cn/weather/index?format=2&dtype=json&cityname="+id+"&key="+key;
//        return url;
        return testurl;
    }
    private String getURL(BDLocation location){
        //添加一个可用的key，解除下面的注释就可以了
        //根据经纬度获取url的逻辑
//        String url = "http://v.juhe.cn/weather/geo?format=2&dtype=json&key="+key+"&lon="+location.getLongitude()+"&lat="+location.getLatitude();
//        return url;
        return testurl;

    }
    private void getRecordWeather(){
        WeatherDataEntity entity;
        if (mBdLocation != null){
            entity = mDataStoreUtil.getWeatherData(0);
        }else {
            entity = mDataStoreUtil.getWeatherData(mCityEntity.getId());
        }
        if (entity == null){
            return;
        }
        cityname = entity.getResult().getToday().getCity();
        mWeatherAdapter.setTodayWeather(entity.getResult().getToday());
        futureWeathers.clear();
        futureWeathers.addAll(Arrays.asList(entity.getResult().getFuture()));
        mWeatherAdapter.notifyDataSetChanged();
    }
}
