package com.example.zybanking.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.Branch;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adapter hiển thị danh sách bên dưới
class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private List<Branch> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Branch branch);
        void onDirectionClick(Branch branch);
    }

    public BranchAdapter(List<Branch> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<Branch> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch_location, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch item = list.get(position);
        holder.tvName.setText(item.name);
        holder.tvAddress.setText(item.address);

        // Format khoảng cách giống file Support.txt line 106
        if (item.distanceM != null) {
            if (item.distanceM < 50) {
                holder.tvDistance.setText("Rất gần");
            } else {
                holder.tvDistance.setText(
                        String.format("Cách %.2f km", item.distanceM / 1000.0)
                );
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDirect.setOnClickListener(v -> listener.onDirectionClick(item));
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        android.widget.TextView tvName, tvAddress, tvDistance;
        android.view.View btnDirect;
        public BranchViewHolder(android.view.View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_branch_name);
            tvAddress = v.findViewById(R.id.tv_branch_address);
            tvDistance = v.findViewById(R.id.tv_branch_distance);
            btnDirect = v.findViewById(R.id.btn_directions);
        }
    }
}

// Activity chính
public class MapActivity extends AppCompatActivity implements BranchAdapter.OnItemClickListener {

    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private Polyline routeLine;
    private RecyclerView rvLocations;
    private BranchAdapter adapter;
    private List<Branch> branchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cấu hình OSM
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.map);

        initViews();
        setupMap();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissionAndLocate();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_map);
        toolbar.setNavigationOnClickListener(v -> finish());

        map = findViewById(R.id.map_view);
        rvLocations = findViewById(R.id.rv_locations);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BranchAdapter(branchList, this);
        rvLocations.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_my_location);
        fab.setOnClickListener(v -> {
            if (currentLocation != null) {
                GeoPoint pt = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                map.getController().animateTo(pt);
                map.getController().setZoom(16.0);
            } else {
                Toast.makeText(this, "Đang lấy vị trí...", Toast.LENGTH_SHORT).show();
                getUserLocation(); // Thử lấy lại
            }
        });
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
    }

    private void checkPermissionAndLocate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = location;

                    // 1. Zoom đến vị trí user
                    GeoPoint myPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    map.getController().setCenter(myPoint);
                    map.getController().setZoom(15.0);

                    // 2. Vẽ Marker User (Màu cam giống file Support.txt line 70)
                    Marker userMarker = new Marker(map);
                    userMarker.setPosition(myPoint);
                    userMarker.setTitle("Vị trí của bạn");
                    userMarker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_location)); // Icon Location màu cam/đỏ
                    userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(userMarker);

                    // 3. Gọi API lấy danh sách
                    fetchNearbyBranches(location.getLatitude(), location.getLongitude());
                } else {
                    Toast.makeText(this, "Không tìm thấy GPS. Hãy bật định vị.", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi GPS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void fetchNearbyBranches(double lat, double lng) {
        // Gọi API Python: /api/v1/branches/nearby
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getNearbyBranches(lat, lng, 10000).enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(Call<List<Branch>> call, Response<List<Branch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Branch> data = response.body();

                    // Cập nhật list
                    branchList.clear();
                    branchList.addAll(data);
                    adapter.updateList(branchList);

                    // Vẽ các marker ngân hàng
                    drawBranchMarkers();

                    // Tự động gợi ý đường đến điểm gần nhất (logic như đồ án yêu cầu)
                    if (!branchList.isEmpty()) {
                        Branch nearest = branchList.get(0);
                        drawRouteToBranch(nearest);
                        Toast.makeText(MapActivity.this, "Gần nhất: " + nearest.name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "Code: " + response.code());
                    Toast.makeText(MapActivity.this, "Không tìm thấy địa điểm nào.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {
                Log.e("API_FAIL", t.getMessage());
                Toast.makeText(MapActivity.this, "Lỗi kết nối Server: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void drawBranchMarkers() {
        // Xóa marker cũ (trừ marker user)
        // Trong thực tế nên quản lý list marker riêng, ở đây vẽ đè cho đơn giản
        for (Branch b : branchList) {
            if (b.lat != null && b.lng != null) {
                GeoPoint gp = new GeoPoint(b.lat, b.lng);
                Marker m = new Marker(map);
                m.setPosition(gp);
                m.setTitle(b.name);
                m.setSnippet(b.address);
                // Icon ngân hàng
                m.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map));

                m.setOnMarkerClickListener((marker, mapView) -> {
                    marker.showInfoWindow();
                    drawRouteToBranch(b); // Click marker thì vẽ đường
                    return true;
                });
                map.getOverlays().add(m);
            }
        }
        map.invalidate();
    }

    private void drawRouteToBranch(Branch b) {
        if (currentLocation == null || b.lat == null || b.lng == null) return;

        // Xóa đường cũ
        if (routeLine != null) map.getOverlays().remove(routeLine);

        // Nối điểm
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        points.add(new GeoPoint(b.lat, b.lng));

        routeLine = new Polyline();
        routeLine.setPoints(points);
        routeLine.setColor(Color.BLUE);
        routeLine.setWidth(8f);

        map.getOverlays().add(routeLine);
        map.invalidate();
    }

    // --- Xử lý sự kiện click từ danh sách (Interface) ---
    @Override
    public void onItemClick(Branch branch) {
        if (branch.lat != null && branch.lng != null) {
            GeoPoint gp = new GeoPoint(branch.lat, branch.lng);
            map.getController().animateTo(gp);
            map.getController().setZoom(17.0);
            drawRouteToBranch(branch);
        }
    }

    @Override
    public void onDirectionClick(Branch branch) {
        drawRouteToBranch(branch);
        Toast.makeText(this, "Đang dẫn đường tới " + branch.name, Toast.LENGTH_SHORT).show();
    }

    // --- Permission & Lifecycle ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        }
    }

    @Override
    protected void onResume() { super.onResume(); map.onResume(); }
    @Override
    protected void onPause() { super.onPause(); map.onPause(); }
}