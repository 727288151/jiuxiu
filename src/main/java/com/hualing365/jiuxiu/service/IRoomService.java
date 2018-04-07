/**
 * 
 */
package com.hualing365.jiuxiu.service;

import java.util.List;

import com.hualing365.jiuxiu.entity.Room;

/**
 * 直播间Service接口类
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 12:07:55 AM
 */
public interface IRoomService {
	
	/**
	 * 根据roomId查询
	 * @param roomId
	 * @return
	 */
	public Room queryRoomById(int roomId);

	/**
	 * 获取直播间active状态的列表
	 * @return
	 */
	public List<Room> queryAllActiveRooms();
	
	/**
	 * 修改room信息
	 * @param room
	 */
	public void updateRoom(Room room);
	
	/**
	 * 修改room状态
	 * @param roomId
	 * @param active
	 */
	public void updateRoomOnOff(int roomId, int active);
}
