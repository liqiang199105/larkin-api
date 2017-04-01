package com.larkin.web.service.user;


import com.larkin.web.http.exception.ApiException;
import com.larkin.web.model.user.UserModel;

public interface UserService {
    public UserModel get(final String userId);

}