package top.anymore.mmweather.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.List;

import top.anymore.mmweather.R;
import top.anymore.mmweather.entity.CityEntity;
import top.anymore.mmweather.fragment.ContentFragment;
import top.anymore.mmweather.fragment.LeftMenuFragment;
import top.anymore.mmweather.location.LocationUtil;
import top.anymore.mmweather.sqlite.AllCitiesDataOpenHelper;
import top.anymore.mmweather.sqlite.DataStoreUtil;


public class MainActivity extends AppCompatActivity {
    private static final String tag = "MainActivity";
    private DrawerLayout drawer;
    private FrameLayout contentLayout,leftMenuLayout;
    private LocationUtil mLocationUtil;
    public ActionBarDrawerToggle mActionBarDrawerToggle;
    public ActionBar mActionBar;
    private DataStoreUtil mDataStoreUtil;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationUtil = new LocationUtil(this);
        mDataStoreUtil = new DataStoreUtil("mmweather.db",this);
        initViews();
        getCurrentLocation();
        register();

        //第一次使用的时候初始化所有城市列表
        new AllCitiesDataOpenHelper(this,"cities.db",null,1);
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationUtil.ACTION_GET_CURRENT_LOCATION);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initViews() {
        mActionBar = getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        LeftMenuFragment fragment = new LeftMenuFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.left_menu_layout,fragment);
        transaction.commit();
    }
    private void getCurrentLocation(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            mLocationUtil.getCurrentLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(MainActivity.this,"您必须同意所有权限，程序才能正常使用..",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    mLocationUtil.getCurrentLocation();
                }else {
                    Toast.makeText(MainActivity.this,"发生未知错位，程序异常退出..",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
    public void replaceContentFragment(CityEntity cityEntity){
        ContentFragment fragment = ContentFragment.newInstance(cityEntity);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout,fragment);
        transaction.commit();
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LocationUtil.ACTION_GET_CURRENT_LOCATION)){
                BDLocation location = intent.getParcelableExtra(LocationUtil.EXTRA_CURRENT_LOCATION);
                CityEntity cityEntity = new CityEntity();
                cityEntity.setId(0);
                cityEntity.setProvince(location.getProvince());
                cityEntity.setCity(location.getCity());
                cityEntity.setDistrict(location.getDistrict());
                mDataStoreUtil.setDefalutCity(cityEntity);
                ContentFragment fragment = ContentFragment.newInstance(location);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_layout,fragment);
                transaction.commit();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_add:
                Intent intent = new Intent(MainActivity.this,AddCityActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START,true);
        }else {
            if (!isExit){
                Toast.makeText(MainActivity.this,"再按一次返回到桌面",Toast.LENGTH_SHORT).show();
                isExit = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isExit = false;
                    }
                }).start();
            }else {
                finish();
            }
        }
    }

}
