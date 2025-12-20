package com.example.zybanking.data.models;
import com.google.gson.annotations.SerializedName;

public class Branch {
    @SerializedName("BRANCH_ID") // Tương ứng 'id' bên React
    public String branchId;

    @SerializedName("NAME") // Tương ứng 'name'
    public String name;

    @SerializedName("ADDRESS") // Tương ứng 'address'
    public String address;

    @SerializedName("LAT") // Tương ứng 'lat'
    public Double lat;

    @SerializedName("LNG") // Tương ứng 'lon' bên React
    public Double lng;

    @SerializedName("OPEN_HOURS")
    public String openHours;

    @SerializedName("DISTANCE_M") // Tương ứng 'distanceM'
    public Double distanceM;
}