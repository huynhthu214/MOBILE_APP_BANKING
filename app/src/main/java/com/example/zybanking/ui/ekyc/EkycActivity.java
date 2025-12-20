package com.example.zybanking.ui.ekyc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.zybanking.R;
import com.example.zybanking.data.models.BasicResponse;
import com.example.zybanking.data.models.ekyc.EkycRequest;
import com.example.zybanking.data.remote.ApiService;
import com.example.zybanking.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EkycActivity extends AppCompatActivity {
    private ImageView btnBack, imgFrontPreview, imgBackPreview, imgSelfiePreview;
    private CardView cardFront, cardBack, cardSelfie;
    private TextView tvKycStatus;
    private Button btnSubmitKyc;

    private ApiService apiService;
    private String userToken;
    private String frontUrl = null, backUrl = null, selfieUrl = null;
    private int uploadType = 0; // 1: Front, 2: Back, 3: Selfie

    // Trình lấy ảnh từ Gallery
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

        btnBack.setOnClickListener(v -> finish());

        // Sự kiện chọn ảnh
        cardFront.setOnClickListener(v -> { uploadType = 1; openGallery(); });
        cardBack.setOnClickListener(v -> { uploadType = 2; openGallery(); });
        cardSelfie.setOnClickListener(v -> { uploadType = 3; openGallery(); });

        btnSubmitKyc.setOnClickListener(v -> handleSubmitEkyc());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleImageSelection(Uri uri) {
        // Lưu ý: Trong đồ án thực tế, bạn cần upload file này lên Firebase/Server
        // để lấy URL. Ở đây tạm thời dùng đường dẫn Uri làm URL để gửi lên DB.
        String path = uri.toString();
        if (uploadType == 1) { frontUrl = path; imgFrontPreview.setImageURI(uri); }
        else if (uploadType == 2) { backUrl = path; imgBackPreview.setImageURI(uri); }
        else if (uploadType == 3) { selfieUrl = path; imgSelfiePreview.setImageURI(uri); }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_ekyc);
        imgFrontPreview = findViewById(R.id.img_front_preview);
        imgBackPreview = findViewById(R.id.img_back_preview);
        imgSelfiePreview = findViewById(R.id.img_selfie_preview);
        cardFront = findViewById(R.id.card_front_id);
        cardBack = findViewById(R.id.card_back_id);
        cardSelfie = findViewById(R.id.card_selfie);
        tvKycStatus = findViewById(R.id.tv_kyc_status);
        btnSubmitKyc = findViewById(R.id.btn_submit_kyc);
    }

    private void setupData() {
        // Khớp với RetrofitClient.getClient() của bạn
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userToken = sharedPref.getString("auth_token", "");
    }

    private void handleSubmitEkyc() {
        if (frontUrl == null || backUrl == null || selfieUrl == null) {
            Toast.makeText(this, "Vui lòng chọn đủ 3 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo đối tượng request
        EkycRequest request = new EkycRequest();

        // Gọi đúng tên hàm setter từ model
        request.setImgFrontUrl(frontUrl);
        request.setImgBackUrl(backUrl);
        request.setSelfieUrl(selfieUrl); // Đã sửa từ setImgSelfieUrl thành setSelfieUrl

        String authHeader = userToken.startsWith("Bearer ") ? userToken : "Bearer " + userToken;

        apiService.submitEKYC(authHeader, request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful()) {
                    tvKycStatus.setText("Trạng thái: Đang chờ duyệt");
                    btnSubmitKyc.setEnabled(false);
                    btnSubmitKyc.setAlpha(0.5f);
                    Toast.makeText(EkycActivity.this, "Gửi hồ sơ thành công!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EkycActivity.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(EkycActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}