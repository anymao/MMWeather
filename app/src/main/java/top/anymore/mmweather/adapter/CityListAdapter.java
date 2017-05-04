package top.anymore.mmweather.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import top.anymore.mmweather.R;
import top.anymore.mmweather.activity.MainActivity;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.sqlite.DataStoreUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder>{
    private List<CityEntity> cityEntities;
    private int selectedPosition;
    private MainActivity mainActivity;
    private DataStoreUtil mDataStoreUtil;
    private Context mContext;
    public CityListAdapter(List<CityEntity> cityEntities, MainActivity mainActivity,Context context) {
        this.cityEntities = cityEntities;
        this.mainActivity = mainActivity;
        selectedPosition = 0;
        mContext = context;
        mDataStoreUtil = new DataStoreUtil("mmweather.db",mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CityEntity entity = cityEntities.get(position);
                mainActivity.replaceContentFragment(entity);
                int prepostion = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(prepostion);
                notifyItemChanged(selectedPosition);
                mainActivity.mActionBar.setTitle(entity.getDistrict());
            }
        });
        final Context context = parent.getContext();
        holder.tvCityName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("删除这个城市？")
                        .setMessage("您是否想要取消订阅这个城市的天气？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int index = holder.getAdapterPosition();
                                if (index == 0){
                                    Toast.makeText(mContext,"这个城市不能被删除...",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                CityEntity entity = cityEntities.remove(index);
                                mDataStoreUtil.unSubscribeCity(entity);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("不,继续订阅", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setCancelable(true);
                builder.show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCityName.setText(cityEntities.get(position).getDistrict());
        if (position == selectedPosition){
            holder.ivIsSselected.setVisibility(View.VISIBLE);
        }else {
            holder.ivIsSselected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return cityEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvCityName;
        ImageView ivIsSselected;
        public ViewHolder(View itemView) {
            super(itemView);
            tvCityName = (TextView) itemView.findViewById(R.id.tv_cityname);
            ivIsSselected = (ImageView) itemView.findViewById(R.id.iv_isselected);
        }
    }
}
