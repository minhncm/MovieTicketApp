package com.example.ticketapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketapp.data.network.ApiService;
import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.AuthResult;
import com.example.ticketapp.domain.repository.AccountRepository;
import com.example.ticketapp.utils.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final ApiService apiService;
    private MutableLiveData<Account> currentUser = new MutableLiveData<>();

    @Inject
    public AccountRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public LiveData<Account> getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(Account _currentUser) {
        currentUser.setValue(_currentUser);
    }

    @Override
    public void clearUser() {
        this.currentUser = null;
    }

    @Override
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

    @Override
    public LiveData<AuthResult> register(String name, String email, String password) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            saveUserToFirestore(firebaseUser.getUid(), name, email, result);
                                        } else {
                                            result.setValue(new AuthResult(false, "Lỗi cập nhật thông tin"));
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
                    result.setValue(new AuthResult(false, "Lỗi lưu thông tin: " + e.getMessage()));
                });
    }

    @Override
    public LiveData<AuthResult> resetPassword(String email) {
        MutableLiveData<AuthResult> result = new MutableLiveData<>();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.setValue(new AuthResult(true, "Email đặt lại mật khẩu đã được gửi"));
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Gửi email thất bại";
                        result.setValue(new AuthResult(false, errorMessage));
                    }
                });

        return result;
    }

    @Override
    public void logout() {
        mAuth.signOut();
    }

    @Override
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    @Override
    public LiveData<Boolean> observeAuthState() {
        MutableLiveData<Boolean> authState = new MutableLiveData<>();

        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            authState.setValue(user != null);
        };

        mAuth.addAuthStateListener(authStateListener);
        authState.setValue(mAuth.getCurrentUser() != null);

        return authState;
    }

    @Override
    public LiveData<Resource<Account>> getUserData() {
        MutableLiveData<Resource<Account>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            resultLiveData.setValue(Resource.error("Người dùng chưa đăng nhập."));
            return resultLiveData;
        }

        String userId = firebaseUser.getUid();

        // Gọi API để lấy thông tin user
        apiService.getUserProfile(userId).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account user = response.body();
                    // Đảm bảo uid được set
                    if (user.getUid() == null) {
                        user.setUid(userId);
                    }
                    
                    // Lấy posterUrl từ Firestore
                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String posterUrl = documentSnapshot.getString("posterUrl");
                                    user.setPosterUrl(posterUrl);
                                }
                                resultLiveData.setValue(Resource.success(user));
                            })
                            .addOnFailureListener(e -> {
                                // Vẫn trả về user dù không lấy được posterUrl
                                resultLiveData.setValue(Resource.success(user));
                            });
                } else {
                    // Fallback: lấy từ Firestore nếu API fail
                    fetchUserFromFirestore(userId, firebaseUser, resultLiveData);
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                // Fallback: lấy từ Firestore nếu API fail
                fetchUserFromFirestore(userId, firebaseUser, resultLiveData);
            }
        });

        return resultLiveData;
    }

    private void fetchUserFromFirestore(String userId, FirebaseUser firebaseUser, 
                                        MutableLiveData<Resource<Account>> resultLiveData) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Account user = new Account();
                    user.setUid(userId);
                    user.setEmail(firebaseUser.getEmail());
                    user.setUsername(firebaseUser.getDisplayName());
                    
                    if (documentSnapshot.exists()) {
                        String posterUrl = documentSnapshot.getString("posterUrl");
                        String phoneNumber = documentSnapshot.getString("phoneNumber");
                        String address = documentSnapshot.getString("address");
                        
                        user.setPosterUrl(posterUrl);
                        user.setPhoneNumber(phoneNumber);
                        user.setAddress(address);
                    }
                    resultLiveData.setValue(Resource.success(user));
                })
                .addOnFailureListener(e -> {
                    // Fallback cuối cùng: chỉ dùng Firebase Auth
                    Account fallbackUser = new Account();
                    fallbackUser.setUid(userId);
                    fallbackUser.setEmail(firebaseUser.getEmail());
                    fallbackUser.setUsername(firebaseUser.getDisplayName());
                    resultLiveData.setValue(Resource.success(fallbackUser));
                });
    }

    @Override
    public LiveData<Resource<Account>> getUserProfileFromApi(String uid) {
        MutableLiveData<Resource<Account>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getUserProfile(uid).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Không thể tải thông tin người dùng"));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage()));
            }
        });

        return result;
    }
}