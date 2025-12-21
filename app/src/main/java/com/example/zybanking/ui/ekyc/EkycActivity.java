package com.example.zybanking.ui.ekyc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.ekyc.EkycRequest;
import com.example.zybanking.data.models.ekyc.EkycResponse;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EkycActivity extends AppCompatActivity {
    // UI Components
    private ImageView btnBack, imgFrontPreview, imgBackPreview, imgSelfiePreview;
    private CardView cardFront, cardBack, cardSelfie;
    // Khai báo biến cho các Layout chứa Icon để ẩn đi
    private LinearLayout layoutIconFront, layoutIconBack, layoutIconSelfie;
    private TextView tvKycStatus;
    private Button btnSubmitKyc;

    // Data & API
    private ApiService apiService;
    private String userToken;
    private String currentUserId;
    private String frontUrl = null, backUrl = null, selfieUrl = null;
    private int uploadType = 0;

    // Image Picker
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    handleImageSelection(selectedImage);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_ekyc);

        initViews();
        setupData();

        // Gọi hàm kiểm tra trạng thái ngay khi mở màn hình
        checkExistingEkyc();

        btnBack.setOnClickListener(v -> finish());

        // Sự kiện chọn ảnh
        cardFront.setOnClickListener(v -> { uploadType = 1; openGallery(); });
        cardBack.setOnClickListener(v -> { uploadType = 2; openGallery(); });
        cardSelfie.setOnClickListener(v -> { uploadType = 3; openGallery(); });

        btnSubmitKyc.setOnClickListener(v -> handleSubmitEkyc());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_ekyc);
        imgFrontPreview = findViewById(R.id.img_front_preview);
        imgBackPreview = findViewById(R.id.img_back_preview);
        imgSelfiePreview = findViewById(R.id.img_selfie_preview);

        cardFront = findViewById(R.id.card_front_id);
        cardBack = findViewById(R.id.card_back_id);
        cardSelfie = findViewById(R.id.card_selfie);

        // Ánh xạ các layout icon mới thêm ID trong XML
        layoutIconFront = findViewById(R.id.layout_icon_front);
        layoutIconBack = findViewById(R.id.layout_icon_back);
        layoutIconSelfie = findViewById(R.id.layout_icon_selfie);

        tvKycStatus = findViewById(R.id.tv_kyc_status);
        btnSubmitKyc = findViewById(R.id.btn_submit_kyc);
    }

    private void setupData() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE);
        userToken = sharedPref.getString("access_token", "");
        currentUserId = sharedPref.getString("user_id", "");
        Log.d("EKYC_CHECK", "Current User ID: " + currentUserId);
    }

    // --- LOGIC LOAD DỮ LIỆU CŨ ---
    private void checkExistingEkyc() {
        apiService.getMyEkyc(currentUserId).enqueue(new Callback<EkycResponse>() {
            @Override
            public void onResponse(Call<EkycResponse> call, Response<EkycResponse> response) {
                Log.d("EKYC_DEBUG", "Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    EkycResponse res = response.body();
                    Log.d("EKYC_DEBUG", "Status: " + res.status);
                    if (res.data != null) {
                        Log.d("EKYC_DEBUG", "EKYC Status: " + res.data.status);
                        updateUIWithExistingData(res.data);
                    } else {
                        Log.d("EKYC_DEBUG", "Data is NULL");
                    }
                } else {
                    Log.e("EKYC_DEBUG", "Error Body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<EkycResponse> call, Throwable t) {
                Log.e("EKYC_ERROR", "Lỗi chi tiết: " + t.getMessage()); // Xem log này trong Logcat
                // Hiển thị lỗi rõ hơn lên màn hình
                Toast.makeText(EkycActivity.this, "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUIWithExistingData(EkycResponse.EkycData data) {
        if (data == null || data.status == null) return;

        // Chuẩn hóa chuỗi trạng thái về chữ thường để so sánh
        String status = data.status.toLowerCase().trim();

        switch (status) {
            case "pending":
                tvKycStatus.setText("Trạng thái: Đang chờ duyệt");
                tvKycStatus.setTextColor(Color.parseColor("#FFA500")); // Màu cam
                disableForm(); // Không cho phép sửa đổi khi đang chờ
                break;

            case "approved":
                tvKycStatus.setText("Trạng thái: Đã xác thực");
                tvKycStatus.setTextColor(Color.parseColor("#008000")); // Màu xanh lá
                disableForm(); // Đã xác thực thì khóa form
                break;

            case "rejected": // Khớp với Database "rejected"
                tvKycStatus.setText("Trạng thái: Bị từ chối - Hãy gửi lại");
                tvKycStatus.setTextColor(Color.RED);
                btnSubmitKyc.setEnabled(true); // Cho phép gửi lại
                btnSubmitKyc.setAlpha(1.0f);
                btnSubmitKyc.setText("Gửi lại yêu cầu");

                // Cho phép click lại vào ảnh để chọn ảnh mới
                enableFormImages();
                break;

            default:
                tvKycStatus.setText("Trạng thái: Chưa xác thực");
                tvKycStatus.setTextColor(Color.GRAY);
                break;
        }
        // 2. Hiển thị ảnh cũ (Decode Base64 -> Bitmap)
        if (data.frontUrl != null) {
            displayBase64Image(data.frontUrl, imgFrontPreview, layoutIconFront);
            frontUrl = data.frontUrl; // Lưu lại để gửi nếu cần sửa
        }
        if (data.backUrl != null) {
            displayBase64Image(data.backUrl, imgBackPreview, layoutIconBack);
            backUrl = data.backUrl;
        }
        if (data.selfieUrl != null) {
            displayBase64Image(data.selfieUrl, imgSelfiePreview, layoutIconSelfie);
            selfieUrl = data.selfieUrl;
        }
    }
    private void enableFormImages() {
        cardFront.setClickable(true);
        cardBack.setClickable(true);
        cardSelfie.setClickable(true);
    }

    private void displayBase64Image(String base64Str, ImageView imageView, View iconLayout) {
        if (base64Str == null || base64Str.isEmpty()) return;
        try {
            String cleanBase64 = base64Str;
            if (base64Str.contains(",")) {
                cleanBase64 = base64Str.split(",")[1];
            }
            // QUAN TRỌNG: Xóa ký tự xuống dòng
            cleanBase64 = cleanBase64.replaceAll("\\s+", "");

            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (decodedBitmap != null) {
                imageView.setImageBitmap(decodedBitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (iconLayout != null) iconLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("EKYC_IMG", "Lỗi decode ảnh: " + e.getMessage());
        }
    }
    private void disableForm() {
        btnSubmitKyc.setEnabled(false);
        btnSubmitKyc.setAlpha(0.5f);
        btnSubmitKyc.setText("Đã gửi hồ sơ");
        cardFront.setClickable(false);
        cardBack.setClickable(false);
        cardSelfie.setClickable(false);
    }

    // --- LOGIC CHỌN ẢNH & UPLOAD ---
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleImageSelection(Uri uri) {
        String base64Image = encodeImageToBase64(uri);
        if (base64Image == null) return;
        if (uploadType == 1) {
            frontUrl = base64Image;
            imgFrontPreview.setImageURI(uri);
            imgFrontPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (layoutIconFront != null) layoutIconFront.setVisibility(View.GONE); // Hide Icon
        } else if (uploadType == 2) {
            backUrl = base64Image;
            imgBackPreview.setImageURI(uri);
            imgBackPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (layoutIconBack != null) layoutIconBack.setVisibility(View.GONE); // Hide Icon
        } else if (uploadType == 3) {
            selfieUrl = base64Image;
            imgSelfiePreview.setImageURI(uri);
            imgSelfiePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (layoutIconSelfie != null) layoutIconSelfie.setVisibility(View.GONE); // Hide Icon
        }
    }

    private void handleSubmitEkyc() {
        if (frontUrl == null || backUrl == null || selfieUrl == null) {
            Toast.makeText(this, "Vui lòng chọn đủ 3 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        EkycRequest request = new EkycRequest();
        request.setImgFrontUrl(frontUrl);
        request.setImgBackUrl(backUrl);
        request.setSelfieUrl(selfieUrl);

        String authHeader = userToken.startsWith("Bearer ") ? userToken : "Bearer " + userToken;

        apiService.createEkyc(authHeader, currentUserId, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EkycActivity.this, "Gửi hồ sơ thành công!", Toast.LENGTH_LONG).show();

                    // 1. CẬP NHẬT GIAO DIỆN NGAY LẬP TỨC
                    tvKycStatus.setText("Trạng thái: Đang chờ duyệt");
                    tvKycStatus.setTextColor(Color.parseColor("#FFA500"));
                    disableForm();

                    // 2. QUAN TRỌNG: XÓA DÒNG NÀY ĐI
                    // checkExistingEkyc(); <--- Nguyên nhân gây lỗi đây!

                    // 3. Trả kết quả về Home
                    setResult(RESULT_OK);

                    // Nếu muốn chắc chắn, có thể đóng màn hình này luôn để user quay lại Home
                    // finish();
                } else {
                    // ... Xử lý lỗi như cũ ...
                    if (response.code() == 400) {
                        // Chỉ gọi check lại nếu bị báo lỗi trùng để xem trạng thái thực là gì
                        checkExistingEkyc();
                        Toast.makeText(EkycActivity.this, "Hồ sơ đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EkycActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(EkycActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            // 1. RESIZE: Giảm xuống còn tối đa 600px (thay vì 1024 hay giữ nguyên)
            // 600px là đủ để xem trên điện thoại
            Bitmap resizedBitmap = getResizedBitmap(selectedImage, 600);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 2. COMPRESS: Giảm chất lượng xuống 50%
            // Mức 50% giúp giảm dung lượng file đi 5-10 lần so với 100%
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

            byte[] b = baos.toByteArray();

            // Log để xem dung lượng sau khi nén (đơn vị KB)
            Log.d("EKYC_SIZE", "Dung lượng ảnh: " + (b.length / 1024) + " KB");

            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}