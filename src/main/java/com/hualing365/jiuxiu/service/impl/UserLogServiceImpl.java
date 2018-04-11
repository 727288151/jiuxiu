/**
 * 
 */
package com.hualing365.jiuxiu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hualing365.jiuxiu.dao.UserLogMapper;
import com.hualing365.jiuxiu.entity.UserLog;
import com.hualing365.jiuxiu.service.IUserLogService;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 1:41:54 AM
 */
@Service
public class UserLogServiceImpl implements IUserLogService {
	
	@Autowired
	UserLogMapper userLogMapper;

	@Override
	public void addUserLog(UserLog userLog) {
		userLogMapper.addUserLog(userLog);
	}

	@Override
	public UserLog queryLatestUserLogOnline(int roomId, int uid) {
		return userLogMapper.queryLatestUserLogOnline(roomId, uid);
	}

	@Override
	public List<UserLog> queryAllUserOnline(int roomId) {
		return userLogMapper.queryAllUserOnline(roomId);
	}
	
	@Override
	public void offline(UserLog userLog) {
		userLogMapper.offline(userLog);
	}
	
	@Override
	public List<UserLog> queryUserLog(int roomId, int uid, int count) {
		return userLogMapper.queryUserLogWithUID(roomId, uid, count);
	}
	
	@Override
	public List<UserLog> queryUserLog(int roomId, int count) {
		return userLogMapper.queryUserLogWithoutUID(roomId, count);
	}
}
