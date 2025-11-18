package com.example.ticketapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.domain.model.Res.AuthResult;
import com.example.ticketapp.domain.model.Account;

import com.example.ticketapp.utils.Resource;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRepository {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private MutableLiveData<Account> currentUser = new MutableLiveData<>();

    public MutableLiveData<Account> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Account _currentUser) {
        currentUser.setValue(_currentUser);
    }

    public void clearUser() {
        this.currentUser = null;
    }
@Inject
    public AccountRepository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Đăng nhập
    public LiveData<AuthResult> login(String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Account user = new Account(
                                    firebaseUser.getUid(),
                                    firebaseUser.getDisplayName(),
                                    firebaseUser.getEmail()

                            );
                            result.setValue(new AuthResult(true, "Đăng nhập thành công", user));
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Đăng nhập thất bại";
                        result.setValue(new AuthResult(false, errorMessage));
                    }
                });

        return result;
    }

    // Đăng ký
    public LiveData<AuthResult> register(String name, String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Cập nhật tên người dùng
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Lưu vào Firestore
                                            saveUserToFirestore(firebaseUser.getUid(), name, email, result);
                                        } else {
                                            result.setValue(new AuthResult(false,
                                                    "Lỗi cập nhật thông tin"));
                                        }
                                    });
                        }
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Đăng ký thất bại";
                        result.setValue(new AuthResult(false, errorMessage));
                    }
                });

        return result;
    }

    private void saveUserToFirestore(String userId, String name, String email,
                                     MutableLiveData<AuthResult> result) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("createdAt", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Account user = new Account(userId, name, email);
                    result.setValue(new AuthResult(true, "Đăng ký thành công", user));
                })
                .addOnFailureListener(e -> {
                    result.setValue(new AuthResult(false,
                            "Lỗi lưu thông tin: " + e.getMessage()));
                });
    }

    // Đặt lại mật khẩu
    public LiveData<AuthResult> resetPassword(String email) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue(new AuthResult(true,
                                "Email đặt lại mật khẩu đã được gửi"));
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Gửi email thất bại";
                        result.setValue(new AuthResult(false, errorMessage));
                    }
                });

        return result;
    }

    // Đăng xuất
    public void logout() {
        mAuth.signOut();
    }

    // Lấy user hiện tại

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
    public LiveData<Boolean> observeAuthState() {
        MutableLiveData<Boolean> authState = new MutableLiveData<>();

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            authState.setValue(user != null);
        };

        // Đăng ký listener
        mAuth.addAuthStateListener(authStateListener);

        // Set giá trị ban đầu
        authState.setValue(mAuth.getCurrentUser() != null);

        return authState;
    }

    // Lấy thông tin user từ Firestore
    public LiveData<Resource<Account>> getUserData() {
        MutableLiveData<Resource<Account>> resultLiveData = new MutableLiveData<>();

        // 1. Phát ra trạng thái LOADING
        resultLiveData.setValue(Resource.loading());

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            // 2. Phát ra trạng thái ERROR nếu chưa đăng nhập
            resultLiveData.setValue(Resource.error("Người dùng chưa đăng nhập."));
            return resultLiveData;
        }

        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String userName = firebaseUser.getDisplayName();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String posterUrl = documentSnapshot.getString("posterUrl")  ;
                        Account user = documentSnapshot.toObject(Account.class);
                        if (user != null) {
                            user.setUid(userId);
                            user.setEmail(email);
                            user.setUsername(userName);
                            user.setPosterUrl(posterUrl);
                        }
                        // 3. Phát ra trạng thái SUCCESS
                        resultLiveData.setValue(Resource.success(user));
                    } else {
                        // Tài liệu không tồn tại
                        resultLiveData.setValue(Resource.error("Không tìm thấy dữ liệu người dùng."));
                    }
                })
                .addOnFailureListener(e -> {
                    // 4. Phát ra trạng thái ERROR khi truy vấn thất bại
                    String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Lỗi truy vấn dữ liệu.";
                    resultLiveData.setValue(Resource.error(errorMessage));
                });

        return resultLiveData;
    }}
