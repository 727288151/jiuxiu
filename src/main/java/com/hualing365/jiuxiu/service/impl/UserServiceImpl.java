/**
 * 
 */
package com.hualing365.jiuxiu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hualing365.jiuxiu.dao.UserMapper;
import com.hualing365.jiuxiu.entity.User;
import com.hualing365.jiuxiu.service.IUserService;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 10, 2018 6:42:10 PM
 */
@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	UserMapper userMapper;

	@Override
	public User queryUserById(int uid) {
		return userMapper.queryUserById(uid);
	}
	
	@Override
	public void addUser(User user) {
		userMapper.addUser(user);
	}
	
	@Override
	public void updateUser(User user) {
		userMapper.updateUser(user);
	}
	
	@Override
	public void addUserHistory(User user) {
		userMapper.addUserHistory(user);
	}
	
	@Override
	public List<User> queryHistory(int uid) {
		return userMapper.queryHistory(uid);
	}

}
