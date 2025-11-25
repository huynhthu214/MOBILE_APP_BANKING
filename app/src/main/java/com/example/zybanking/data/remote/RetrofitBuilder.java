package com.example.zybanking.data.remote;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Lớp này thay thế cho Kotlin 'object' để cung cấp một thể hiện
 * Retrofit duy nhất (singleton) trong môi trường Java.
 *
 * Lớp này là 'final' và có một constructor 'private' để ngăn chặn
 * việc tạo đối tượng từ bên ngoài (chỉ cho phép truy cập tĩnh).
 */
public class RetrofitBuilder {

    private static final String BASE_URL = "https://mockapi.io/"; // tạm thời

    private static final OkHttpClient client;
    private static final Retrofit retrofit;

    // Khối 'static' này được chạy một lần khi lớp được tải,
    // tương tự như cách các thuộc tính 'val' được khởi tạo trong 'object' Kotlin.
    static {
        // 1. Cấu hình HttpLoggingInterceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 2. Xây dựng OkHttpClient
        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // 3. Xây dựng Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Constructor private để ngăn chặn việc tạo đối tượng (instantiation).
     * Đây là một lớp tiện ích tĩnh.
     */
    private RetrofitBuilder() {
        // Không cho phép tạo đối tượng của lớp này
    }

    /**
     * Phương thức tĩnh (static) để tạo một service API.
     *
     * @param service Lớp interface của service (ví dụ: ApiService.class)
     * @param <T>     Kiểu generic của service
     * @return Một thể hiện (instance) của service
     */
    public static <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}