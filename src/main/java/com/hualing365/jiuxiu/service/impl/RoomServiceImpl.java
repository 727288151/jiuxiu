/**
 * 
 */
package com.hualing365.jiuxiu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hualing365.jiuxiu.dao.RoomMapper;
import com.hualing365.jiuxiu.entity.Room;
import com.hualing365.jiuxiu.service.IRoomService;

/**
 * 直播间service实现类
 * @author im.harley.lee@qq.com
 * @since Mar 11, 2018 12:10:19 AM
 */
@Service
public class RoomServiceImpl implements IRoomService {

	@Autowired
	RoomMapper roomMapper;
	
	@Override
	public Room queryRoomById(int roomId) {
		return roomMapper.queryRoomById(roomId);
	}
	
	@Override
	public List<Room> queryAllActiveRooms() {
		return roomMapper.queryAllActiveRooms();
	}
	
	@Override
	public void updateRoom(Room room) {
		roomMapper.updateRoom(room);
	}
	
	@Override
	public void updateRoomOnOff(int roomId, int active) {
		roomMapper.updateRoomOnOff(roomId, active);
	}

}
