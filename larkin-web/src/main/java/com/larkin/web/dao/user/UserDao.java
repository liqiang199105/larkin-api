package com.larkin.web.dao.user;


import com.larkin.web.model.user.UserModel;

import java.util.List;

public interface UserDao {

    public UserModel get(final String userId);

    public List<UserModel> getUserByNick(final String nick);

    public UserModel getUserByPhone(final String phone);

    public void replaceUserVerifyCode(final UserModel userModel);

    public UserModel insertUserModel(final UserModel userModel);

    public UserModel updateUserInfo(final UserModel userModel);
}
