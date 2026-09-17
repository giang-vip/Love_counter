# Dự Án Love Counter - Kiến Trúc Clean Architecture + MVVM Chuẩn Chuyên Nghiệp

Dự án ứng dụng Android **Love Counter (Giao_dien)** được tổ chức phân tầng 3 lớp chuẩn **Google Android Architecture Guidelines**: **`domain/`**, **`data/`**, và **`ui/`**.

---

## 📁 Cấu Trúc Thư Mục Dự Án (Project Structure)

```
com.example.giao_dien/
│
├── ads/                                  # Tầng Quảng Cáo (Google AdMob SDK)
│   ├── AdConfig.kt                       # Quản lý tập trung 100% Mã Quảng Cáo (Ad Unit ID)
│   ├── AppOpenAdManager.kt               # Quản lý Quảng cáo Mở ứng dụng (App Open Ad)
│   ├── BannerAdManager.kt                # Quản lý Quảng cáo Biểu ngữ Adaptive Banner
│   ├── InterstitialAdManager.kt          # Quản lý Quảng cáo Chèn giữa Toàn màn hình (Ads Intern)
│   └── NativeAdManager.kt                # Quản lý Quảng cáo Tự nhiên (Native Ad có MediaView)
│
├── domain/                               # Tầng Nghiệp vụ (Domain Layer)
│   ├── model/                            # Models nghiệp vụ
│   │   ├── PersonInfo.kt                 # Thông tin cá nhân (name, gender, birthday, avatarUri)
│   │   ├── CoupleInfo.kt                 # Thông tin cặp đôi (maleInfo, femaleInfo, loveStartDate)
│   │   ├── GenderType.kt                 # Enum giới tính (MALE, FEMALE, OTHER)
│   │   ├── Language.kt                   # Data class Ngôn ngữ
│   │   ├── IntroPage.kt                  # Data class Trang Intro
│   │   ├── MemoryInfo.kt                 # Data class Kỷ niệm (id, title, date, imageUri)
│   │   ├── NameTestResult.kt             # Data class Kết quả bói Tên (yourName, partnerName, percent, description)
│   │   ├── HoroscopeItem.kt              # Data class Cung Hoàng Đạo (id, name, iconRes)
│   │   └── HoroscopeResult.kt            # Data class Kết quả bói Cung Hoàng Đạo
│   └── repository/                       # Interfaces Hợp đồng Dữ liệu
│       ├── AppRepository.kt              # Interface dữ liệu Cấu hình App (Language, Intro, Permission)
│       ├── CoupleRepository.kt           # Interface dữ liệu Cặp đôi (Person, Love days, Background)
│       ├── MemoryRepository.kt           # Interface dữ liệu Kỷ niệm
│       ├── NameTestRepository.kt         # Interface bói Tương hợp Tên
│       └── HoroscopeRepository.kt        # Interface bói Cung Hoàng Đạo
│
├── data/                                 # Tầng Dữ liệu (Data Layer)
│   ├── local/
│   │   ├── AppPreferences.kt             # SharedPreferences (Lưu cài đặt nhanh)
│   │   └── db/                           # Room Database (SQLite Engine)
│   │       ├── entity/
│   │       │   ├── PersonEntity.kt       # Bảng SQLite person_table
│   │       │   ├── CoupleEntity.kt       # Bảng SQLite couple_table
│   │       │   └── MemoryEntity.kt       # Bảng SQLite memory_table
│   │       ├── dao/
│   │       │   ├── PersonDao.kt          # Access Object cho Person
│   │       │   ├── CoupleDao.kt          # Access Object cho Couple
│   │       │   └── MemoryDao.kt          # Access Object cho Memory
│   │       └── AppDatabase.kt          # RoomDatabase class khởi tạo SQLite love_counter_db (v2)
│   └── repository/                       # Triển khai Repositories (Impl)
│       ├── AppRepositoryImpl.kt          # Implementation AppRepository
│       ├── CoupleRepositoryImpl.kt       # Implementation CoupleRepository (Room SQLite + Preferences)
│       ├── MemoryRepositoryImpl.kt       # Implementation MemoryRepository
│       ├── NameTestRepositoryImpl.kt     # Implementation thuật toán bói Tên
│       └── HoroscopeRepositoryImpl.kt    # Implementation 12 Cung & 7 mốc nhận xét bói Cung
│
└── ui/                                   # Tầng Giao diện & ViewModels (UI Layer - Clean MVVM theo Feature)
    ├── splash/                           # Feature Splash / Loading
    │   ├── MainActivity.kt               # Activity đếm 4 chấm & điều hướng thông minh
    │   └── SplashViewModel.kt            # ViewModel đếm 3s bằng Coroutines StateFlow
    │
    ├── language/                         # Feature Chọn Ngôn Ngữ
    │   ├── LanguageActivity.kt           # Activity render danh sách ngôn ngữ (Hỗ trợ cờ mở từ Setting)
    │   ├── LanguageViewModel.kt          # ViewModel quản lý ngôn ngữ chọn & lưu
    │   └── LanguageAdapter.kt            # Adapter ListAdapter + DiffUtil
    │
    ├── intro/                            # Feature Giới Thiệu (Intro Sliders)
    │   ├── IntroActivity.kt              # Activity điều khiển ViewPager2 & Dots
    │   ├── IntroViewModel.kt             # ViewModel quản lý các trang Intro & sự kiện Next
    │   └── IntroPagerAdapter.kt          # Adapter cho ViewPager2 màn Intro
    │
    ├── permission/                       # Feature Cấp Quyền (Permission)
    │   ├── PermissionActivity.kt         # Activity hiển thị Switch & Dialog xin quyền
    │   └── PermissionViewModel.kt        # ViewModel quản lý cờ trạng thái cấp quyền
    │
    ├── home/                             # Feature Trang Chủ (Home Screen)
    │   ├── HomeActivity.kt               # Activity chứa ViewPager2 & Bottom Menu 3 Tabs
    │   ├── HomeViewModel.kt              # ViewModel quản lý thông tin cặp đôi & số ngày yêu
    │   ├── HomePagerAdapter.kt           # FragmentStateAdapter quản lý 3 Fragments
    │   ├── HomeFragment.kt               # Fragment đếm ngày yêu, tính tuổi động & icon giới tính
    │   ├── LoveTestFragment.kt           # Fragment Kiểm tra Tương hợp (Name Test & Horoscope Test)
    │   ├── LoveTestViewModel.kt          # ViewModel cho LoveTestFragment
    │   ├── MemoryFragment.kt             # Fragment Danh sách Kỷ niệm (Chuyển đổi Empty/List state)
    │   └── MemoryViewModel.kt            # ViewModel quản lý quan sát danh sách Kỷ niệm từ Room DB
    │
    ├── love_test/                        # Feature Bói Tương Hợp Tên
    │   ├── NameTestActivity.kt           # Activity nhập tên 2 người
    │   ├── NameTestViewModel.kt          # ViewModel validate & điều hướng
    │   ├── NameTestResultActivity.kt     # Activity hiển thị % trái tim, tên & chia sẻ
    │   └── NameTestResultViewModel.kt    # ViewModel tính toán & cung cấp dữ liệu kết quả bói Tên
    │
    ├── horoscope/                        # Feature Bói Cung Hoàng Đạo
    │   ├── HoroscopeTestActivity.kt      # Activity chọn 2 cung hoàng đạo
    │   ├── HoroscopeTestViewModel.kt     # ViewModel quản lý chọn cung
    │   ├── HoroscopeAdapter.kt           # Adapter lưới 2 cột Cung Hoàng Đạo
    │   ├── HoroscopeBottomSheetDialogFragment.kt # BottomSheet chọn 12 cung hoàng đạo
    │   ├── HoroscopeTestResultActivity.kt # Activity hiển thị kết quả %, icon cung & nhận xét
    │   └── HoroscopeTestResultViewModel.kt # ViewModel tính toán & cung cấp kết quả bói Cung
    │
    ├── memory/                           # Feature Thêm Kỷ Niệm
    │   ├── AddMemoryActivity.kt          # Activity chọn ảnh, tiêu đề, ngày & lưu kỷ niệm
    │   ├── AddMemoryViewModel.kt         # ViewModel xử lý lưu kỷ niệm vào Room DB
    │   └── MemoryAdapter.kt              # Adapter hiển thị danh sách thẻ kỷ niệm
    │
    ├── start_date/                       # Feature Chọn Ngày Yêu (Start Date)
    │   ├── StartDateActivity.kt          # Activity hiển thị thẻ "Date of love" + Custom DatePicker
    │   └── StartDateViewModel.kt         # ViewModel quản lý lưu ngày bắt đầu yêu
    │
    ├── background/                       # Feature Chọn Ảnh Nền (Choose Background)
    │   ├── ChooseBackgroundActivity.kt     # Activity/Sheet chọn ảnh nền (Tải ảnh từ máy / Chọn ảnh mẫu)
    │   ├── ChooseBackgroundViewModel.kt    # ViewModel quản lý chọn & lưu ảnh nền
    │   └── BackgroundAdapter.kt          # Adapter danh sách ảnh nền
    │
    ├── change_info/                      # Feature Chỉnh Sửa Thông Tin
    │   ├── ChangeInfoActivity.kt         # Activity sửa thông tin Nam/Nữ (Ảnh Camera/Gallery, Ngày sinh)
    │   └── ChangeInfoViewModel.kt        # ViewModel quản lý lưu thông tin cá nhân qua CoupleRepository
    │
    └── setting/                          # Feature Cài Đặt (Settings)
        └── SettingActivity.kt            # Activity Cài đặt (Language, Rate, Share, Feedback, Privacy Policy)
```

---

## 🔄 Luồng Điều Hướng Ứng Dụng & Quảng Cáo (App Navigation & Ads Flow)

```
                       [Mở App (MainActivity)]
                                  │
             ┌────────────────────┴────────────────────┐
             ▼                                         ▼
 (1) Loading Dots (2-3 giây)               (2) Preload Interstitial Ad
             │                                         │
             └────────────────────┬────────────────────┘
                                  ▼
                     (3) Hiển Thị Ads Intern
                 (Nút [X] để đóng sau vài giây)
                                  │
                       [Người dùng bấm [X]]
                                  │
                                  ▼
          (4) Điều hướng vào App theo đúng trạng thái:
           ├── Lần đầu mở app ──────► [LanguageActivity] (Show Native Ad)
           │                                 │
           │                                 ▼
           │                         [IntroActivity] (Show Native Ad)
           │                                 │
           │                                 ▼
           │                         [PermissionActivity] (Show Native Ad)
           │                                 │
           └─────────────────────────┼───────┘
                                     ▼
                              [HomeActivity]
                                  ├── HomeFragment
                                  ├── LoveTestFragment
                                  │     ├── [NameTestActivity] ──► (Show Ads) ──► [NameTestResultActivity]
                                  │     └── [HoroscopeTestActivity] ──► (Show Ads) ──► [HoroscopeTestResultActivity]
                                  └── MemoryFragment ──► [AddMemoryActivity]
```

---

## 🛠️ Công Nghệ & Kỹ Thuật Nổi Bật (Tech Stack)

- **Kiến trúc 3 Tầng**: Clean Architecture (`domain`, `data`, `ui`) + MVVM.
- **Tích hợp Quảng Cáo Google AdMob & Firebase Remote Config Chuẩn Senior**:
  - `FirebaseRemoteConfigHelper`: Quản lý đồng bộ Bật/Tắt quảng cáo khẩn cấp và Mã Ad Unit ID từ xa từ trang web Firebase Console mà không cần nộp bản cập nhật app lên Google Play.
  - `AdConfig`: Quản lý tập trung 100% Mã Quảng Cáo (Ad Unit ID) tại một vị trí duy nhất.
  - `InterstitialAdManager`: Quản lý quảng cáo chèn giữa toàn màn hình (Ads Intern) dạng Singleton chuyên nghiệp. Tích hợp tại Mở app (Splash), Bói tình yêu (Name Test & Horoscope Test), và khi bấm nút **Save (Lưu)** ở các màn Chỉnh sửa thông tin, Thêm kỷ niệm, Chọn ngày yêu, Chọn ảnh nền.
  - `NativeAdManager`: Quản lý quảng cáo tự nhiên tích hợp **`MediaView`** chiếu ảnh/video sinh động, tích hợp **Shimmer Loading Animation** (Khung xương quét sáng chờ ad) chống giật màn hình trên cả 3 màn Onboarding (`LanguageActivity`, `IntroActivity`, `PermissionActivity`).
  - `BannerAdManager`: Quản lý Adaptive Banner Ad tự co dãn cố định ở đáy màn hình Trang chủ (`HomeActivity`).
  - `RewardedAdManager`: Quản lý Rewarded Video Ad bắt buộc xem 30s để mở khóa tính năng Bói Tên (`NameTestActivity`) và Bói Cung Hoàng Đạo (`HoroscopeTestActivity`), kết hợp hiệu ứng Animation đếm % tự nhiên 2.5s ở màn hình Kết Quả.
  - `AppOpenAdManager`: Quản lý App Open Ad tự động hiển thị quảng cáo chào mừng khi người dùng bấm thoát app (Background) và quay lại ứng dụng (Foreground/Resume) sử dụng `ProcessLifecycleOwner`.
- **Cơ sở dữ liệu Cục bộ**: **Room Database (SQLite)** cho Person, Couple, và Memory (`memory_table`).
- **Lưu trữ Ảnh Cục Bộ**: `ImageStorageManager` sao chép ảnh vào Internal Storage chống tràn quyền truy cập URI.
- **Custom Date Picker Dialog Siêu Mượt**: `DatePickerDialogHelper` tích hợp `MaterialCalendarView` giao diện ô vuông đỏ bo góc đồng bộ toàn app, hỗ trợ **bấm trực tiếp vào tiêu đề Tháng - Năm** để chọn nhanh Tháng (1-12) & Năm (1950-2050) cuộn phản hồi tức thì 60-120 FPS.
- **Tính Tuổi Tự Động**: Tự động tính tuổi nam/nữ chính xác dựa trên ngày sinh và ngày hiện tại.
- **Trạng thái UI (UI State)**: `StateFlow` & `SharedFlow` (`extraBufferCapacity = 1`).
- **Nút FAB Nổi Kỷ Niệm**: Nút bấm thêm mới kỷ niệm đặt tại góc dưới bên phải chuẩn giao diện Material.
- **Thuật toán Bói toán Ổn định**: Tính % tương thích bằng Hash chuỗi đầu vào (Name & Horoscope) giúp kết quả trả về cố định và khách quan.
- **Đa Ngôn Ngữ Chuẩn Hóa (Localization)**: 100% chuỗi ký tự UI được gom vào `res/values/strings.xml` hỗ trợ Placeholder (`%1$s`, `%1$d`), sẵn sàng cho việc mở rộng đa ngôn ngữ (`values-vi`, `values-en`, `values-ja`, `values-ko`, v.v.).
