package top.anymore.mmweather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import top.anymore.mmweather.R;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.sqlite.DataStoreUtil;

/**
 * Created by anymore on 17-5-4.
 */

public class HotCityListAdapter extends RecyclerView.Adapter<HotCityListAdapter.ViewHolder>{
    private static final String tag = "HotCityListAdapter";
    private List<CityEntity> cityEntities;
    private DataStoreUtil mDataStoreUtil;
    public HotCityListAdapter(List<CityEntity> cityEntities, Context context) {
        this.cityEntities = cityEntities;
        mDataStoreUtil = new DataStoreUtil("mmweather.db",context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_city_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.tvHotCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityEntity entity = cityEntities.get(holder.getAdapterPosition());
                mDataStoreUtil.subscribeCity(entity);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CityEntity entity = cityEntities.get(position);
        holder.tvHotCityName.setText(entity.getDistrict());
    }

    @Override
    public int getItemCount() {
        return cityEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvHotCityName;
        public ViewHolder(View itemView) {
            super(itemView);
            tvHotCityName = (TextView) itemView.findViewById(R.id.tv_hot_city_name);
        }
    }
}
