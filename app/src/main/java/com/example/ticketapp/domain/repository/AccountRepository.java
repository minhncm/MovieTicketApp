package com.example.ticketapp.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.AuthResult;
import com.example.ticketapp.utils.Resource;

public interface AccountRepository {
    LiveData<AuthResult> login(String email, String password);
    
    LiveData<AuthResult> register(String name, String email, String password);
    
    LiveData<AuthResult> resetPassword(String email);
    
    void logout();
    
    boolean isUserLoggedIn();
    
    LiveData<Boolean> observeAuthState();
    
    LiveData<Resource<Account>> getUserData();
    
    LiveData<Resource<Account>> getUserProfileFromApi(String uid);
    
    LiveData<Account> getCurrentUser();
    
    void setCurrentUser(Account user);
    
    void clearUser();
}