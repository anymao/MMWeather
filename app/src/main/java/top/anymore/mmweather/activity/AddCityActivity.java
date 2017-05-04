package top.anymore.mmweather.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import top.anymore.mmweather.R;
import top.anymore.mmweather.adapter.CityCursorAdapter;
import top.anymore.mmweather.adapter.HotCityListAdapter;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.sqlite.AllCitiesDataOpenHelper;
import top.anymore.mmweather.sqlite.DataStoreUtil;


public class AddCityActivity extends AppCompatActivity {
    private static final String tag = "AddCityActivity";
    private Button btnBack,btnSubmit;
    private AutoCompleteTextView actvCity;
    private RecyclerView rvHotCities;
    private CityCursorAdapter mCityCursorAdapter;
    private AllCitiesDataOpenHelper mDataOpenHelper;
    private DataStoreUtil mDataStoreUtil;
    private CityEntity entity;
    private List<CityEntity> hotCityEntities;
    private HotCityListAdapter mHotCityListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏系统ActionBar
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_city);
        initViews();
        setListener();
    }

    private void initViews() {
        btnBack = (Button) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        actvCity = (AutoCompleteTextView) findViewById(R.id.actv_city);
        rvHotCities = (RecyclerView) findViewById(R.id.rv_hot_cities);
    }
    private void setListener(){
        mDataStoreUtil = new DataStoreUtil("mmweather.db",this);
        mDataOpenHelper = new AllCitiesDataOpenHelper(this,"cities.db",null,1);
        mCityCursorAdapter = new CityCursorAdapter(AddCityActivity.this,null,0,mDataOpenHelper.getReadableDatabase());
        actvCity.setAdapter(mCityCursorAdapter);
        actvCity.setThreshold(1);
        actvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView item = (TextView) view;
                actvCity.setText(item.getText());
//                entity = (CityEntity) mCityCursorAdapter.getItem(i);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCity();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hotCityEntities = new ArrayList<>();
        mHotCityListAdapter = new HotCityListAdapter(hotCityEntities,this);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        rvHotCities.setLayoutManager(layoutManager);
        rvHotCities.setAdapter(mHotCityListAdapter);
        new InitHotCitiesTask().execute();
    }

    private void addCity() {
        String input = actvCity.getText().toString();
        CityEntity entity = new CityEntity(input);
        mDataStoreUtil.subscribeCity(entity);
    }

    private class InitHotCitiesTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            Reader reader = null;
            try {
                Gson gson = new Gson();
                reader = new InputStreamReader(getAssets().open("hot_cities.json"));
                List<CityEntity> list = gson.fromJson(reader,new TypeToken<List<CityEntity>>()
                {}.getType());
                hotCityEntities.clear();
                hotCityEntities.addAll(list);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHotCityListAdapter.notifyDataSetChanged();
        }
    }
}
