/**
 * 
 */
package com.hualing365.jiuxiu.scheduler.thread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.hualing365.jiuxiu.entity.Room;
import com.hualing365.jiuxiu.service.IRoomService;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 10, 2018 10:47:24 PM
 */
@Component
public class AsyncScanRoomTask {
	
	public static Map<Integer, MyRunnable> runnableMap = new HashMap<Integer, MyRunnable>();
	private static Map<Integer, String> roomNameMap = new HashMap<Integer, String>();
	
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	IRoomService roomService;

	@Async
	public void executeAsyncTask(WebApplicationContext webAppCtx){
		while(true){
			Set<Integer> rooms = getActiveRooms();
			List<Room> roomList = roomService.queryAllActiveRooms();
			for(Room room : roomList){
				if(!runnableMap.containsKey(room.getRoomId())){
					//创建Runnable并交给线程池去执行
					MyRunnable myRunnable = webAppCtx.getBean(MyRunnable.class);
					//MyRunnable myRunnable = new MyRunnable(room.getRoomId(), room.getRoomName());
					myRunnable.setRoomId(room.getRoomId());
					myRunnable.setRoomName(room.getRoomName());
					taskExecutor.execute(myRunnable);
					
					//将roomId放入set集合，以便将查询的列表与正在运行的线程作比较
					//如果线程中有而列表中没有，则停止改线程，否则启动新线程
					rooms.add(room.getRoomId());
					runnableMap.put(room.getRoomId(), myRunnable);
				}
			}
			Iterator<Integer> iterator = runnableMap.keySet().iterator();
			while(iterator.hasNext()){
				int roomId = iterator.next();
				if(!rooms.contains(roomId)){
					MyRunnable r = runnableMap.get(roomId);
					r.setActive(false);
					iterator.remove();
					roomNameMap.remove(roomId);
				}
			}
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 获取所有要监听的房间号
	 * @return
	 */
	public Set<Integer> getActiveRooms(){
		Set<Integer> set = new HashSet<Integer>();
		List<Room> roomList = roomService.queryAllActiveRooms();
		//System.out.println("Active:"+roomList.size());
		for(Room room : roomList){
			set.add(room.getRoomId());
			roomNameMap.put(room.getRoomId(), room.getRoomName());
		}
		return set;
	}
	
}
