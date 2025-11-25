//package com.example.zybanking.data.remote;
//
//import com.example.zybanking.data.model.User; // Giả sử bạn có class User ở đây
//import java.util.List;
//import retrofit2.Call;
//import retrofit2.http.GET;
//
///**
// * Đây LÀ một interface, không phải là một class.
// * Interface này định nghĩa các điểm cuối (endpoints) API cho Retrofit.
// */
//public interface ApiService {
//
//    /**
//     * Lấy danh sách người dùng.
//     *
//     * Trong Java, chúng ta không dùng 'suspend fun'.
//     * Thay vào đó, chúng ta trả về một đối tượng 'Call<T>' của Retrofit.
//     *
//     * @return Một đối tượng Call chứa một List<User>
//     */
//    @GET("users")
//    Call<List<User>> getUsers();
//
//    // GHI CHÚ: Hãy đảm bảo bạn có một class 'User' đã được định nghĩa
//    // (ví dụ: trong com.example.zybanking.data.model.User)
//    // để Gson có thể phân tích (parse) JSON.
//}