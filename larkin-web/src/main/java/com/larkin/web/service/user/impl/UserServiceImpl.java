package com.larkin.web.service.user.impl;

import com.larkin.web.dao.user.UserDao;
import com.larkin.web.model.user.UserModel;
import com.larkin.web.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Resource private UserDao userDao;

    @Override
    public UserModel get(final String userId){
        return userDao.get(userId);
    }

}
