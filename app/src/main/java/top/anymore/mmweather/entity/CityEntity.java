package top.anymore.mmweather.entity;

import java.io.Serializable;

import top.anymore.mmweather.logutil.LogUtil;

/**
 * Created by anymore on 17-5-3.
 */

public class CityEntity implements Serializable{
    private int id;
    private String province;
    private String city;
    private String district;

    public CityEntity() {
        super();
    }

    public CityEntity(String input){
        String[] ss = input.split("-");
        if (ss.length<4){
            return;
        }
        LogUtil.v("[city cou]",ss.toString());
        id = Integer.parseInt(ss[0]);
        province = ss[1];
        city = ss[2];
        district = ss[3];

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CityEntity that = (CityEntity) o;

        if (id != that.id) return false;
        if (province != null ? !province.equals(that.province) : that.province != null)
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return district != null ? district.equals(that.district) : that.district == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        return result;
    }
}
