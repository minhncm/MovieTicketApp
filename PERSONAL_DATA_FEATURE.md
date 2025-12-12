# Chức năng Xem Thông Tin Cá Nhân Chi Tiết

## Tổng quan
Đã thêm thành công chức năng xem thông tin cá nhân chi tiết cho mục "Thông tin cá nhân" trong SettingsFragment.

## Tính năng mới

### PersonalDataFragment
- **Vị trí**: `app/src/main/java/com/example/ticketapp/view/PersonalDataFragment.java`
- **Layout**: `app/src/main/res/layout/fragment_personal_data.xml`

### Các thông tin hiển thị

#### 1. Profile Header
- Avatar người dùng (có thể click để thay đổi)
- Tên và role của user
- Nút "Chỉnh sửa hồ sơ" để navigate đến EditProfileFragment

#### 2. Thông tin cơ bản
- **ID người dùng**: Hiển thị UID từ Firebase
- **Email**: Email đăng ký tài khoản
- **Số điện thoại**: Placeholder data (+84 123 456 789)
- **Ngày sinh**: Placeholder data (15/03/1995)
- **Giới tính**: Placeholder data (Nam)
- **Địa chỉ**: Placeholder data (123 Đường ABC, Quận 1, TP.HCM)

#### 3. Thống kê tài khoản
- **Thành viên từ**: Tính toán từ 1 năm trước (placeholder)
- **Tổng vé đã mua**: Placeholder data (25 vé)
- **Điểm thưởng**: Placeholder data (1,250 điểm) - hiển thị màu xanh

#### 4. Thao tác nhanh
- **Đổi mật khẩu**: Placeholder (sẽ phát triển)
- **Cập nhật số điện thoại**: Placeholder (sẽ phát triển)
- **Cập nhật địa chỉ**: Placeholder (sẽ phát triển)
- **Lịch sử mua vé**: Navigate đến MyTicket
- **Điểm thưởng của tôi**: Placeholder (sẽ phát triển)

## Navigation Flow

### Từ SettingsFragment
1. User tap vào "Thông tin cá nhân" trong SettingsFragment
2. Navigate đến PersonalDataFragment
3. Hiển thị thông tin chi tiết của user

### Từ PersonalDataFragment
- **Chỉnh sửa hồ sơ** → EditProfileFragment
- **Lịch sử mua vé** → MyTicket Fragment
- Các chức năng khác → Toast thông báo "đang phát triển"

## Cập nhật Navigation

### nav_graph.xml
```xml
<fragment
    android:id="@+id/personalDataFragment"
    android:name="com.example.ticketapp.view.PersonalDataFragment"
    android:label="@string/txt_personal_data"
    tools:layout="@layout/fragment_personal_data" >
    <action
        android:id="@+id/action_personalDataFragment_to_editProfile"
        app:destination="@id/editProfileFragment" />
</fragment>
```

### SettingsFragment
- Thêm action `action_nav_settings_to_personalData`
- Thêm click listener cho `personalDataSettings`

## UI Components mới

### Icons
- `ic_phone.xml`: Icon điện thoại
- `ic_location.xml`: Icon vị trí/địa chỉ
- `ic_arrow_right.xml`: Mũi tên chỉ hướng

### Colors
- `card_bg_color`: Màu nền cho các card (#252A34)

## Tích hợp với hệ thống hiện có

### ProfileViewModel
- Sử dụng ProfileViewModel để lấy thông tin user
- Observe `getUserProfile()` để cập nhật UI
- Tích hợp với Account model hiện có

### Data Binding
- Sử dụng ViewBinding cho type-safe access
- LiveData observer pattern cho reactive UI
- Glide cho image loading

## Placeholder Data
Hiện tại sử dụng placeholder data cho:
- Số điện thoại
- Ngày sinh
- Giới tính
- Địa chỉ
- Thống kê tài khoản (ngày tham gia, số vé, điểm thưởng)

## Các cải tiến trong tương lai

### Backend Integration
1. **API endpoints** cho cập nhật thông tin cá nhân
2. **Real data** thay thế placeholder data
3. **Upload avatar** functionality
4. **Phone verification** system

### Additional Features
1. **Change Password** screen
2. **Update Phone Number** với OTP verification
3. **Update Address** với location picker
4. **Reward Points** detail screen với lịch sử tích điểm
5. **Account Statistics** với charts và analytics

### UI/UX Improvements
1. **Pull-to-refresh** để cập nhật data
2. **Loading states** cho các operations
3. **Error handling** với retry mechanisms
4. **Offline support** với cached data

## Cách sử dụng

1. Mở ứng dụng và đăng nhập
2. Navigate đến Settings (từ Profile tab hoặc menu)
3. Tap vào "Thông tin cá nhân" trong phần Account
4. Xem thông tin chi tiết và sử dụng các thao tác nhanh
5. Tap "Chỉnh sửa hồ sơ" để cập nhật thông tin cơ bản

## Technical Implementation

### Architecture
- **MVVM Pattern**: ViewModel + LiveData + View Binding
- **Navigation Component**: Safe Args cho type-safe navigation
- **Dependency Injection**: Hilt cho DI
- **Image Loading**: Glide với placeholder và error handling

### Code Quality
- **Null Safety**: Proper null checks
- **Resource Management**: Proper lifecycle handling
- **Memory Leaks**: ViewBinding cleanup in onDestroyView
- **User Experience**: Toast messages cho unimplemented features