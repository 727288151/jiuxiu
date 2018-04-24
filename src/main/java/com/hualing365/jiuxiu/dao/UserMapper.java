/**
 * 
 */
package com.hualing365.jiuxiu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.hualing365.jiuxiu.entity.User;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 10, 2018 6:34:19 PM
 */
@Mapper
@Repository
public interface UserMapper {

	@Select("select * from t_user where uid=#{uid}")
	@ResultType(User.class)
	public User queryUserById(int uid);
	
	@Insert("insert into t_user(uid, accountid, nickname, headimage, familybadge, datetime) values(#{uid}, #{accountId}, #{nickName}, #{headImage}, #{familyBadge}, #{dateTime})")
	public void addUser(User user);
	
	@Update("update t_user set nickname=#{nickName}, headimage=#{headImage}, familybadge=#{familyBadge}, datetime=#{dateTime} where uid=#{uid}")
	public void updateUser(User user);
	
	@Insert("insert into t_user_history(uid, accountid, nickname, headimage, familybadge, datetime) values(#{uid}, #{accountId}, #{nickName}, #{headImage}, #{familyBadge}, #{dateTime})")
	public void addUserHistory(User user);
	
	@Select("select * from t_user_history where uid=#{uid} order by id asc")
	@ResultType(User.class)
	public List<User> queryHistory(int uid);
	
	@Select("select * from t_user_history order by id asc")
	@ResultType(User.class)
	public List<User> queryAllUserHistory();
}
