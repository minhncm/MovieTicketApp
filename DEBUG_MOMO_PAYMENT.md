# Debug MoMo Payment Issue

## Vấn đề
ProgressDialog hiển thị "Đang chờ thanh toán..." và không tắt sau khi thanh toán thành công.

## Nguyên nhân có thể
1. **BroadcastReceiver không nhận được callback** - Deep link không hoạt động
2. **Fragment bị destroy** khi mở browser
3. **Observer được gọi nhiều lần** - LiveData không remove observer

## Đã Fix
✅ Thêm null check và logging trong `processBooking()`
✅ Thêm try-catch khi unregister receiver
✅ Thêm delay 300ms trước khi navigate
✅ Thêm onPause/onResume để debug

## Cách Debug

### 1. Kiểm tra Logcat
Chạy app và filter theo tag `PaymentMethodSelection` hoặc `MainActivity`:

```bash
adb logcat | grep -E "PaymentMethodSelection|MainActivity"
```

Hoặc trong Android Studio Logcat, filter:
```
tag:PaymentMethodSelection OR tag:MainActivity
```

### 2. Kiểm tra Deep Link
Sau khi thanh toán thành công, xem log:
- `MainActivity: Deep link received: ticketapp://payment/callback?...`
- `MainActivity: ResultCode: 0, OrderId: xxx`
- `PaymentMethodSelection: MoMo callback received - ResultCode: 0`
- `PaymentMethodSelection: Booking status: LOADING`
- `PaymentMethodSelection: Booking success!`

### 3. Test Deep Link thủ công
Chạy lệnh sau để test deep link:
```bash
adb shell am start -W -a android.intent.action.VIEW -d "ticketapp://payment/callback?resultCode=0&orderId=TEST123&message=Success"
```

Nếu app mở và nhận được callback → Deep link hoạt động ✅
Nếu không → Kiểm tra AndroidManifest.xml

## Các Trường Hợp

### Case 1: Deep Link không hoạt động
**Triệu chứng**: Không thấy log "Deep link received" trong MainActivity

**Giải pháp**:
1. Kiểm tra AndroidManifest.xml có intent-filter đúng
2. MainActivity phải có `android:exported="true"`
3. MainActivity phải có `android:launchMode="singleTop"`
4. Test bằng adb command ở trên

### Case 2: BroadcastReceiver không nhận
**Triệu chứng**: Thấy log "Deep link received" nhưng không thấy "MoMo callback received"

**Giải pháp**:
1. Fragment có thể bị destroy khi mở browser
2. Thử dùng LocalBroadcastManager thay vì sendBroadcast
3. Hoặc lưu result vào SharedPreferences và check trong onResume

### Case 3: Observer gọi nhiều lần
**Triệu chứng**: Thấy nhiều log "Booking status: SUCCESS"

**Giải pháp**:
1. Dùng `observeOnce` thay vì `observe`
2. Hoặc remove observer sau khi SUCCESS/ERROR
3. Đã fix bằng cách thêm check và delay

## Giải pháp thay thế

### Option 1: Dùng SharedPreferences
Thay vì BroadcastReceiver, lưu result vào SharedPreferences:

**MainActivity.java**:
```java
SharedPreferences prefs = getSharedPreferences("payment", MODE_PRIVATE);
prefs.edit()
    .putString("resultCode", resultCode)
    .putString("orderId", orderId)
    .putLong("timestamp", System.currentTimeMillis())
    .apply();
```

**PaymentMethodSelectionFragment.java** trong `onResume()`:
```java
SharedPreferences prefs = requireActivity().getSharedPreferences("payment", Context.MODE_PRIVATE);
String resultCode = prefs.getString("resultCode", null);
long timestamp = prefs.getLong("timestamp", 0);

// Chỉ xử lý nếu là result mới (trong 10 giây)
if (resultCode != null && System.currentTimeMillis() - timestamp < 10000) {
    // Clear để không xử lý lại
    prefs.edit().clear().apply();
    
    if ("0".equals(resultCode)) {
        processBooking();
    }
}
```

### Option 2: Dùng ViewModel
Lưu payment result trong ViewModel để share giữa Activity và Fragment.

### Option 3: Dùng Navigation Arguments
Pass result qua SafeArgs khi navigate.

## Test Steps

1. **Clean & Rebuild** project
2. **Uninstall app** từ device: `adb uninstall com.example.ticketapp`
3. **Install lại**: Run từ Android Studio
4. **Enable logging**: Mở Logcat và filter
5. **Test payment**:
   - Chọn ghế → MoMo
   - Thanh toán thành công
   - Xem logs
6. **Check result**:
   - Dialog có dismiss không?
   - eTicket có hiển thị không?
   - Thông tin ghế có đúng không?

## Expected Logs (Success Flow)

```
PaymentMethodSelection: MoMo payment initiated
MainActivity: Deep link received: ticketapp://payment/callback?resultCode=0&orderId=TICKET_xxx
MainActivity: ResultCode: 0, OrderId: TICKET_xxx, Message: Success
PaymentMethodSelection: MoMo callback received - ResultCode: 0
PaymentMethodSelection: Booking status: LOADING
PaymentMethodSelection: Booking status: SUCCESS
PaymentMethodSelection: Booking success!
```

## Nếu vẫn không hoạt động

Hãy gửi cho tôi:
1. Full logcat output (filter theo tag)
2. Screenshot của error
3. Kết quả test deep link bằng adb command
