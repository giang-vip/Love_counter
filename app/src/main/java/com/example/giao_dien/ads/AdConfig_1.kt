package com.example.giao_dien.ads

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * FILE ADCONFIG PHONG CÁCH "CHUYÊN NGHIỆP" (GIỐNG ANH LEAD)
 * 
 * Tại sao file này lại "sạch" hơn file cũ?
 * 1. Không viết chết (Hardcode) các chuỗi ID "ca-app-pub..." vào đây.
 * 2. Chỉ tập trung vào việc định nghĩa "Cái khuôn" để hứng dữ liệu từ Firebase.
 * 3. Mọi Mã ID sẽ được quản lý tập trung ở Firebase (Online) hoặc strings.xml (Offline).
 */
data class AdConfig_1(
    // --- PHẦN 1: CÁC CỜ BẬT/TẮT QUẢNG CÁO (Ánh xạ từ Key trên Firebase Console) ---

    @SerializedName("config_ads") 
    @Expose 
    var configAds: Boolean = true, // Cờ tổng: Tắt cái này là toàn App mất sạch quảng cáo

    @SerializedName("inter_splash") 
    @Expose 
    var interSplash: Boolean = true, // Bật/Tắt quảng cáo lúc mở App

    @SerializedName("native_language") 
    @Expose 
    var nativeLanguage: Boolean = true, // Bật/Tắt quảng cáo màn hình chọn ngôn ngữ

    @SerializedName("native_intro") 
    @Expose 
    var nativeIntro: Boolean = true, // Bật/Tắt quảng cáo màn hình giới thiệu

    @SerializedName("banner_home") 
    @Expose 
    var bannerHome: Boolean = true, // Bật/Tắt banner ở đáy màn hình chính

    @SerializedName("app_open_resume") 
    @Expose 
    var appOpenResume: Boolean = true, // Bật/Tắt quảng cáo khi người dùng quay lại App (từ ẩn sang hiện)

    @SerializedName("reward_name_test") 
    @Expose 
    var rewardNameTest: Boolean = true, // Bật/Tắt quảng cáo xem bói tên

    @SerializedName("reward_horoscope") 
    @Expose 
    var rewardHoroscope: Boolean = true, // Bật/Tắt quảng cáo xem tử vi


    // --- PHẦN 2: CÁC BIẾN HỨNG MÃ ID TỪ FIREBASE (Linh hoạt tối đa) ---
    // Anh Lead thường để trống hoặc gán mặc định là chuỗi rỗng.
    // Nếu trên Firebase bạn gửi kèm mã ID mới, nó sẽ tự động ghi đè vào đây.

    @SerializedName("inter_splash_id") 
    @Expose 
    var interSplashId: String = "",

    @SerializedName("native_language_id") 
    @Expose 
    var nativeLanguageId: String = "",

    @SerializedName("banner_home_id") 
    @Expose 
    var bannerHomeId: String = ""
) {
    /**
     * TƯ DUY HÀM LẤY ID (HÀM CỨU HỘ):
     * Chúng ta sẽ viết các hàm thông minh để lấy ID. 
     * Ưu tiên: Firebase (Mã thật) > Nếu rỗng thì lấy strings.xml (Mã Test).
     */
    companion object {
        // Ví dụ cách lấy ID Interstitial thông minh:
        fun getInterSplashId(context: android.content.Context): String {
            val remoteId = FirebaseConfigManager.getInstance().adConfig.interSplashId
            return if (remoteId.isNotEmpty()) {
                remoteId // Trả về mã thật từ Firebase
            } else {
                // Nếu Firebase chưa có, mò vào file strings.xml của thư viện :ads lấy mã Test
                context.getString(com.ads.admob.R.string.list_id_test) // Hoặc ID cụ thể trong đó
                "ca-app-pub-3940256099942544/1033173712" // Fallback an toàn nhất
            }
        }
    }
}
