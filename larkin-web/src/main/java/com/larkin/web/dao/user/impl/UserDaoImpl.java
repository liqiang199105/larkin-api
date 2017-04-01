package com.larkin.web.dao.user.impl;


import com.larkin.web.dao.user.UserDao;
import com.larkin.web.model.user.UserModel;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;


@Repository
public class UserDaoImpl implements UserDao {
    private static Logger logger = Logger.getLogger(UserDaoImpl.class);

    private static final String namespace = "user.";

    @Resource private SqlSession sqlSession;

    @Override
    public UserModel get(final String userId){
        return (UserModel) sqlSession.selectOne(namespace + "get", userId);
    }

    public List<UserModel> getUserByNick(final String nick){
        return  sqlSession.selectList(namespace + "getUserByNick", nick);

    }

    @Override
    public UserModel getUserByPhone(final String phone){
        return (UserModel) sqlSession.selectOne(namespace + "getUserByPhone", phone);

    }

    @Override
    public void replaceUserVerifyCode(final UserModel userModel){
        sqlSession.insert(namespace + "replacePhoneVerifyCode", userModel);
    }

    @Override
    public UserModel insertUserModel(final UserModel userModel){
        sqlSession.insert(namespace + "insertUserModel", userModel);
        return userModel;
    }

    @Override
    public UserModel updateUserInfo(final UserModel userModel){
        int row = sqlSession.update(namespace + "updateUserInfo", userModel);
        return row > 0 ? this.get(userModel.getUserId()) :  userModel;
    }
}
