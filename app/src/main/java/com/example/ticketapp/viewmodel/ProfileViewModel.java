package com.example.ticketapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketapp.domain.model.Account;
import com.example.ticketapp.domain.model.Res.AuthResult;
import com.example.ticketapp.data.repository.AccountRepository;
import com.example.ticketapp.utils.Resource;


import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final AccountRepository accountRepository;
    private MutableLiveData<Account> userData = new MutableLiveData<>();
    public  LiveData<Account> _userData = userData;

    @Inject
    public ProfileViewModel(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public  LiveData<AuthResult> login(String email, String password){

   return accountRepository.login(email,password);

    }
    public LiveData<AuthResult>  register(String name, String email, String password){
return         accountRepository.register(name, email, password);




    }
    public LiveData<Account> getUserProfile() {
        loadUserProfile();
        return userData;
    }
    public void setUserProfile(Account userProfile) {
        accountRepository.setCurrentUser(userProfile);
        loadUserProfile();
    }

    public void loadUserProfile() {
        LiveData<Account> user = accountRepository.getCurrentUser();
        userData.setValue(user.getValue());
    }
    public LiveData<Resource<Account>> geUserById(){
         return accountRepository.getUserData();
    }
    public  void logout(){
        accountRepository.logout();
    }
    public  Boolean isUserLoggedIn(){
        return accountRepository.isUserLoggedIn();
    }
public LiveData<Boolean> observerAuthState(){
    return accountRepository.observeAuthState();}

}
