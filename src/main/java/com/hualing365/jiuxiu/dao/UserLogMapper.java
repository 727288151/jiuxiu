/**
 * 
 */
package com.hualing365.jiuxiu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.hualing365.jiuxiu.entity.User;
import com.hualing365.jiuxiu.entity.UserLog;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 1:42:16 AM
 */
@Mapper
@Repository
public interface UserLogMapper {

	@Insert("insert into t_user_log (roomid, uid, wealthlevel, wealthleveldesc, logindatetime, logoutdatetime, duration, online, hide, os) values (#{roomId}, #{uid}, #{wealthLevel}, #{wealthLevelDesc}, #{loginDateTime}, #{logoutDateTime}, #{duration}, #{online}, #{hide}, #{os})")
	public void addUserLog(UserLog userLog);
	
	@Select("select * from t_user_log where roomid = #{roomId} and uid = #{uid} and online=1 order by id desc limit 0,1")
	public UserLog queryLatestUserLogOnline(@Param("roomId") int roomId, @Param("uid") int uid);

	@Select("select u.nickname,u.accountid,ul.* from t_user_log ul left join t_user u on u.uid=ul.uid where ul.online=1 and ul.roomid = #{roomId}")
	@ResultType(User.class)
	public List<UserLog> queryAllUserOnline(int roomId);
	
	@Update("update t_user_log set logoutdatetime = #{logoutDateTime}, duration = #{duration}, online=0 where id=#{id}")
	public void offline(UserLog userLog);
	
	@Select("select u.nickname,u.accountid,ul.* from t_user_log ul left join t_user u on u.uid=ul.uid where ul.roomid=#{roomId} and ul.uid=#{uid} order by id desc limit 0,#{count}")
	public List<UserLog> queryUserLogWithUID(@Param("roomId") int roomId,@Param("uid") int uid,@Param("count") int count);
	
	@Select("select u.nickname,u.accountid,ul.* from t_user_log ul left join t_user u on u.uid=ul.uid where ul.roomid=#{roomId} order by id desc limit 0,#{count}")
	public List<UserLog> queryUserLogWithoutUID(@Param("roomId") int roomId, @Param("count") int count);
}
