package com.example.zybanking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Chuyển thẳng sang LoginActivity
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }

//        // Test Firebase
//        FirebaseAuth.getInstance().signInAnonymously()
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("FIREBASE_TEST", "Firebase hoạt động OK");
//                        } else {
//                            Log.e("FIREBASE_TEST", "Firebase lỗi", task.getException());
//                        }
//                    }
//                });
//    }
    }
}
