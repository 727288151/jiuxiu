/**
 * 
 */
package com.hualing365.jiuxiu.service;

import java.util.List;

import com.hualing365.jiuxiu.entity.UserLog;

/**
 * 用户出入日志service接口类
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 1:40:43 AM
 */
public interface IUserLogService {

	/**
	 * 增加一条记录
	 * @param userLog
	 */
	public void addUserLog(UserLog userLog);
	
	/**
	 * 根据uid查询最后登录的记录
	 * @param roomId
	 * @param uid
	 * @return
	 */
	public UserLog queryLatestUserLogOnline(int roomId, int uid);

	/**
	 * 获取某个直播间当前在线用户列表
	 * @param roomId
	 * @return
	 */
	public List<UserLog> queryAllUserOnline(int roomId);
	
	/**
	 * 用户下线
	 * @param userLog
	 */
	public void offline(UserLog userLog);
	
	/**
	 * 查询用户日志
	 * @param roomId
	 * @param uid
	 * @param count
	 * @return
	 */
	public List<UserLog> queryUserLog(int roomId, int uid, int count);
	
	/**
	 * 查询用户日志
	 * @param roomId
	 * @param count
	 * @return
	 */
	public List<UserLog> queryUserLog(int roomId, int count);
}
