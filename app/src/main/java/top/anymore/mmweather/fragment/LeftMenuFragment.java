package top.anymore.mmweather.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import top.anymore.mmweather.R;
import top.anymore.mmweather.activity.AddCityActivity;
import top.anymore.mmweather.activity.MainActivity;
import top.anymore.mmweather.adapter.CityListAdapter;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.logutil.LogUtil;
import top.anymore.mmweather.sqlite.DataStoreUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class LeftMenuFragment extends Fragment{
    private static final String tag = "LeftMenuFragment";
    private RecyclerView rvCities;
    private List<CityEntity> cityEntities;
    private DataStoreUtil mDataStoreUtil;
    private CityListAdapter mAdapter;
    private MainActivity mainActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataStoreUtil = new DataStoreUtil("mmweather.db",getContext());
        cityEntities = mDataStoreUtil.getSubscribedCities();
        mainActivity = (MainActivity) getActivity();
        if (!cityEntities.isEmpty()){
            mainActivity.mActionBar.setTitle(cityEntities.get(0).getDistrict());
        }
        mAdapter = new CityListAdapter(cityEntities, (MainActivity) getActivity(),getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_left,container,false);
        rvCities = (RecyclerView) view.findViewById(R.id.rv_cities);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        register();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCities.setLayoutManager(layoutManager);
        rvCities.setAdapter(mAdapter);
    }
    private void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(DataStoreUtil.ACTION_ADD_NEW_CITY);
        filter.addAction(DataStoreUtil.ACTION_DEFAULT_CITY_CHANGED);
        getContext().registerReceiver(receiver,filter);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DataStoreUtil.ACTION_DEFAULT_CITY_CHANGED)){
                cityEntities.remove(0);
                cityEntities.add(0, (CityEntity) intent.getSerializableExtra(DataStoreUtil.EXTRA_CITY_INFO));
                mAdapter.notifyItemChanged(0);
            }
            if (action.equals(DataStoreUtil.ACTION_ADD_NEW_CITY)){
                cityEntities.add((CityEntity) intent.getSerializableExtra(DataStoreUtil.EXTRA_CITY_INFO));
                mAdapter.notifyDataSetChanged();
            }
        }
    };
}
