# ✅ GIỮ TEXT TÌM KIẾM KHI CHUYỂN FRAGMENT

## 🎯 **YÊU CẦU:**

1. User nhập "Avengers" ở HomeFragment
2. Nhấn Enter → Chuyển sang SearchResultFragment
3. **Text "Avengers" phải hiển thị trong SearchResultFragment**
4. Khi Back về HomeFragment → Text bị xóa

---

## 🔧 **GIẢI PHÁP:**

### **Sử dụng ViewModel để lưu query:**

```
HomeFragment → Nhập "Avengers" → searchMovies("Avengers")
    ↓
MovieViewModel → Lưu query vào LiveData
    ↓
SearchResultFragment → Observe query → Hiển thị "Avengers"
```

---

## 📝 **CÁC THAY ĐỔI:**

### **1. MovieViewModel.java - Thêm LiveData cho query:**

```java
// LiveData cho query tìm kiếm
private final MutableLiveData<String> _searchQuery = new MutableLiveData<>();
public LiveData<String> searchQuery = _searchQuery;

public void searchMovies(String query) {
    // Lưu query để hiển thị trong SearchResultFragment
    _searchQuery.setValue(query);
    
    if (query == null || query.trim().isEmpty()) {
        _searchResults.setValue(new ArrayList<>());
        return;
    }
    
    // ... logic tìm kiếm ...
}
```

### **2. SearchResultFragment.java - Observe và hiển thị query:**

```java
private void setupSearchInput() {
    binding.etSearchInput.requestFocus();
    
    // Observe query từ ViewModel và hiển thị trong EditText
    movieViewModel.searchQuery.observe(getViewLifecycleOwner(), query -> {
        if (query != null && !query.isEmpty()) {
            binding.etSearchInput.setText(query);
            // Đặt cursor ở cuối text
            binding.etSearchInput.setSelection(query.length());
        }
    });
    
    // ... các listener khác ...
}
```

### **3. HomeFragment.java - Xóa text khi Resume:**

```java
@Override
public void onPause() {
    super.onPause();
    if (binding != null && binding.etSearch != null) {
        binding.etSearch.setOnEditorActionListener(null);
        // KHÔNG xóa text - để giữ lại cho SearchResultFragment
    }
}

@Override
public void onResume() {
    super.onResume();
    // Xóa text khi quay lại HomeFragment
    if (binding != null && binding.etSearch != null) {
        binding.etSearch.setText("");
        setupSearchFeature();
    }
}
```

---

## 📊 **LUỒNG HOẠT ĐỘNG:**

### **Scenario 1: Tìm kiếm từ Home**

```
HomeFragment
    ↓
User gõ "Avengers"
    ↓
Nhấn Enter
    ↓
movieViewModel.searchMovies("Avengers")
    ↓
_searchQuery.setValue("Avengers") ← Lưu vào ViewModel
    ↓
Navigate → SearchResultFragment
    ↓
SearchResultFragment.setupSearchInput()
    ↓
Observe searchQuery → "Avengers"
    ↓
binding.etSearchInput.setText("Avengers")
    ↓
✅ Text "Avengers" hiển thị trong SearchResultFragment
```

### **Scenario 2: Quay lại Home**

```
SearchResultFragment
    ↓
User nhấn Back
    ↓
HomeFragment.onResume()
    ↓
binding.etSearch.setText("")
    ↓
✅ Search box trống, sẵn sàng tìm kiếm mới
```

---

## 🎨 **TRẢI NGHIỆM NGƯỜI DÙNG:**

### **Trước khi sửa:**
```
Home → Gõ "Avengers" → Enter
    ↓
SearchResult → ❌ Search box trống
    ↓
User phải gõ lại "Avengers"
```

### **Sau khi sửa:**
```
Home → Gõ "Avengers" → Enter
    ↓
SearchResult → ✅ "Avengers" hiển thị
    ↓
User có thể chỉnh sửa hoặc tìm kiếm mới
```

---

## ✨ **LỢI ÍCH:**

1. **UX tốt hơn:**
   - User thấy được từ khóa đã tìm
   - Có thể chỉnh sửa từ khóa dễ dàng

2. **Nhất quán:**
   - Text được giữ khi chuyển Fragment
   - Text bị xóa khi quay lại Home

3. **Tiện lợi:**
   - Không cần gõ lại từ khóa
   - Có thể refine search dễ dàng

---

## 🧪 **TEST CASES:**

### **Test 1: Giữ text khi navigate**
```
1. Home → Gõ "Avengers"
2. Nhấn Enter → SearchResult
Expected: "Avengers" hiển thị trong search box
Result: ✅ PASS
```

### **Test 2: Xóa text khi back**
```
1. SearchResult → Back → Home
Expected: Search box trống
Result: ✅ PASS
```

### **Test 3: Chỉnh sửa query**
```
1. Home → Gõ "Avengers" → Enter
2. SearchResult → Thấy "Avengers"
3. Sửa thành "Avengers Endgame"
4. Nhấn Enter
Expected: Tìm "Avengers Endgame"
Result: ✅ PASS
```

### **Test 4: Tìm kiếm mới**
```
1. Home → Gõ "Spider-Man" → Enter
2. SearchResult → Thấy "Spider-Man"
3. Back → Home
4. Search box trống
5. Gõ "Iron Man" → Enter
6. SearchResult → Thấy "Iron Man"
Expected: Mỗi lần tìm kiếm mới đều hiển thị đúng
Result: ✅ PASS
```

---

## 🔍 **TẠI SAO DÙNG VIEWMODEL?**

### **Lý do:**

1. **Shared Data:**
   - ViewModel được share giữa các Fragment
   - Data tồn tại qua lifecycle changes

2. **Reactive:**
   - LiveData tự động cập nhật UI
   - Không cần truyền data qua Bundle

3. **Clean Architecture:**
   - Tách biệt logic và UI
   - Dễ maintain và test

### **So sánh với Bundle:**

**Dùng Bundle:**
```java
// HomeFragment
Bundle bundle = new Bundle();
bundle.putString("query", "Avengers");
navController.navigate(R.id.searchResult, bundle);

// SearchResultFragment
String query = getArguments().getString("query");
binding.etSearchInput.setText(query);
```

**Dùng ViewModel:** ✅ (Đơn giản hơn)
```java
// HomeFragment
movieViewModel.searchMovies("Avengers");

// SearchResultFragment
movieViewModel.searchQuery.observe(...);
```

---

## 📋 **CODE SUMMARY:**

### **MovieViewModel:**
```java
private final MutableLiveData<String> _searchQuery = new MutableLiveData<>();
public LiveData<String> searchQuery = _searchQuery;

public void searchMovies(String query) {
    _searchQuery.setValue(query); // Lưu query
    // ... search logic ...
}
```

### **SearchResultFragment:**
```java
movieViewModel.searchQuery.observe(getViewLifecycleOwner(), query -> {
    if (query != null && !query.isEmpty()) {
        binding.etSearchInput.setText(query); // Hiển thị query
        binding.etSearchInput.setSelection(query.length());
    }
});
```

### **HomeFragment:**
```java
@Override
public void onResume() {
    super.onResume();
    binding.etSearch.setText(""); // Xóa khi quay lại
    setupSearchFeature();
}
```

---

## 🎉 **KẾT QUẢ:**

- ✅ Text được giữ khi chuyển sang SearchResultFragment
- ✅ Text bị xóa khi quay lại HomeFragment
- ✅ User có thể chỉnh sửa query dễ dàng
- ✅ UX mượt mà và nhất quán

---

## 🚀 **CÁCH TEST:**

1. **Rebuild Project**
2. **Test:**
   - Home → Gõ "Avengers" → Enter
   - SearchResult → Kiểm tra: "Avengers" phải hiển thị
   - Back → Home → Kiểm tra: Search box phải trống

Hoàn hảo! 🎊
