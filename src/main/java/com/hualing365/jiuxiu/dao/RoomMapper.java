/**
 * 
 */
package com.hualing365.jiuxiu.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.hualing365.jiuxiu.entity.Room;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 12:10:34 AM
 */
@Mapper
@Repository
public interface RoomMapper {
	
	@Select("select * from t_room where roomId = #{roomId}")
	@ResultType(Room.class)
	public Room queryRoomById(int roomId);

	@Select("select roomid, roomname, active from t_room where active = 1")
	public List<Room> queryAllActiveRooms();
	
	@Update("update t_room set totalcount=#{totalCount}, realcount=#{realCount}, admincount=#{adminCount}, robotcount=#{robotCount}, blankcount=#{blankCount} where roomid=#{roomId}")
	public void updateRoom(Room room);
	
	@Update("update t_room set active=#{active} where roomid=#{roomId}")
	public void updateRoomOnOff(@Param("roomId") int roomId, @Param("active") int active);
}
