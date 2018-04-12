package com.hualing365.jiuxiu.scheduler.thread;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hualing365.jiuxiu.entity.Room;
import com.hualing365.jiuxiu.entity.User;
import com.hualing365.jiuxiu.entity.UserLog;
import com.hualing365.jiuxiu.service.IRoomService;
import com.hualing365.jiuxiu.service.IUserLogService;
import com.hualing365.jiuxiu.service.IUserService;
import com.hualing365.jiuxiu.util.CommonUtil;

@Scope("prototype")
@Component
public class MyRunnable implements Runnable {
	
	final Logger logger = LoggerFactory.getLogger(MyRunnable.class);
	
	@Autowired
	IUserService userService;
	
	@Autowired
	IUserLogService userLogService;
	
	@Autowired
	IRoomService roomService;
	
	private boolean isActive = true;
	private int roomId = 0;
	private String roomName = null;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String urlStr = "http://www.9xiu.com/room/userlist/getRoomUserList?type=1&page=0&pagesize=50&rid=";

	@Override
	public void run() {
		logger.info(prefix() + "started!");
		while(true){
			try{
				URL url = new URL(urlStr + "&v=" + System.currentTimeMillis());
				InputStream is = url.openConnection().getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] buffer = new byte[1024];
				StringBuilder s = new StringBuilder(512);
				int length = 0;
				while((length = bis.read(buffer)) != -1 ){
					s.append(new String(buffer, 0, length));
				}
				bis.close();
				is.close();
				logger.debug("receive json:"+s.toString());
				handle(s.toString().trim());
				
				//如果isActive是false，则终止线程
				if(!isActive){
					logger.info(prefix() + "stopped!");
					return;
				}
			}catch (IOException e){
				//e.printStackTrace();
				logger.error(e.getMessage());
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void handle(String str){
		JSONObject jsonObj = JSONObject.parseObject(str);
		//如果返回结果不正常，则退出不进行下一步操作
		if(jsonObj == null || jsonObj.getIntValue("code") != 200){
			logger.error("Stoped! RoomId is wrong-"+roomName + "("+roomId+")");
			setActive(false);
			return ;
		}
		
		//取出data数据
		JSONObject dataObj = jsonObj.getJSONObject("data");
		//data数据包含user数组
		JSONArray userList = dataObj.getJSONArray("user");
		
		Set<Integer> set = getAllUserOnline();
		String before = null;
		String after = null;
		String beforeUid = null;
		Set<Integer> hideMan = new HashSet<Integer>();
		for(int i=0;i<userList.size();i++){
			JSONObject userData = userList.getJSONObject(i);
			JSONObject userObj = userData.getJSONObject("user");
			int uid = userObj.getIntValue("uid");
			String uidStr = String.valueOf(uid);
			
			before = userData.getString("before");
			//如果后面人的before不等于前面人的uid，说明中间有神秘人，神秘人的uid即before
			if(!StringUtils.isEmpty(before) && !before.equals("null") && !before.equals(beforeUid)){
				hideMan.add(Integer.valueOf(before));
			}
			//设置当前uid为beforeUid，为下一次循环使用
			beforeUid = uidStr;
			//如果前面人的after不等于后面人的uid，说明中间有神秘人，神秘人的uid即after
			if(!StringUtils.isEmpty(after) && !after.equals("null")&& !after.equals(uidStr)){
				hideMan.add(Integer.valueOf(after));
			}
			after =  userData.getString("after");
			//如果只有一个人，但它的after有值，说明有神秘人
			if(userList.size() == 1 && !StringUtils.isEmpty(after) && !after.equals("null")){
				hideMan.add(Integer.valueOf(after));
			}
			
			//如果uid不包含在已登陆用户列表内，则进行上线操作
			if(!set.contains(uid)){
				int accountId = userObj.getIntValue("accountid");
				String nickName = userObj.getString("nickname");
				int wealthlevel = userObj.getIntValue("wealthlevel");
				String headImage = userObj.getString("headimage120");
				String familyBadge = userObj.getString("familyBadge");
				int os = userObj.getIntValue("os");
				int hide = 0;
				if(nickName.equals("神秘人")){
					hide = 1;
				}
				insert(uid, accountId, nickName, wealthlevel, headImage, familyBadge, os, hide);
			}
			//如果uid已包含，则先删除，set中剩余的就是已经下线的
			else{
				set.remove(uid);
			}
		}
		//从在线用户中移除已存在的神秘人，剩下的神秘人下线
		Iterator<Integer> iteratorHideMan = hideMan.iterator();
		while(iteratorHideMan.hasNext()){
			int uid = iteratorHideMan.next();
			if(set.contains(uid)){
				set.remove(uid);
				iteratorHideMan.remove();
			}
		}
		
		//神秘人上线操作
		insertHideMan(hideMan);
		
		//剩余的用户已不在线，进行下线操作
		for(int uid : set){
			offline(uid);
		}
		
		//计算房间空号用户
		int roomCount = dataObj.getIntValue("roomcount");
		int realCount = dataObj.getIntValue("realcount");
		int adminCount = dataObj.getIntValue("admincount");
		int robotCount = dataObj.getIntValue("robotcount");
		handleBlankUser(roomCount, realCount, adminCount, robotCount);
		
	}
	
	/**
	 * 获取当前直播间所有在线用户
	 * @return
	 */
	public Set<Integer> getAllUserOnline(){
		Set<Integer> set = new HashSet<Integer>();
		List<UserLog> list = userLogService.queryAllUserOnline(roomId);
		for(UserLog userLog : list){
			set.add(userLog.getUid());
		}
		return set;
	}
	
	/**
	 * 计算房间空号用户数
	 */
	public void handleBlankUser(int roomCount, int realCount, int adminCount, int robotCount){
		int count = (roomCount - realCount - realCount*50 - robotCount)/51;
		updateBlankCount(roomCount, realCount, adminCount, robotCount, count);
	}
	
	/**
	 * 上线用户记录
	 */
	public void insert(int uid, int accountId, String nickName, int wealthLevel, String headImage, String familyBadge, int os, int hide){
		String dateTime = CommonUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		
		//如果用户表不存在，则插入用户
		User user = userService.queryUserById(uid);
		if(user == null){
			user = new User();
			user.setUid(uid);
			user.setAccountId(accountId);
			user.setNickName(nickName);
			user.setHeadImage(headImage);
			user.setFamilyBadge(familyBadge);
			user.setDateTime(dateTime);
			userService.addUser(user);
		}
		//如果已存在，比较是否修改了昵称，头像，家族等，如果修改了，更改用户表，并把之前的放入历史表user_history
		else{
			if( !user.getNickName().equals(nickName) && !nickName.equals("神秘人")){
				//如果原来不是神秘人，才会把修改记录到历史表，否则只改名字
				if(!user.getNickName().equals("神秘人")){
					userService.addUserHistory(user);
				}
				user = new User();
				user.setUid(uid);
				user.setNickName(nickName);
				user.setHeadImage(headImage);
				user.setFamilyBadge(familyBadge);
				user.setDateTime(dateTime);
				userService.updateUser(user);
			}else if(!user.getHeadImage().equals(headImage)){
				User userUpd = new User();
				userUpd.setUid(uid);
				userUpd.setNickName(nickName.equals("神秘人") ? user.getNickName() : nickName);
				userUpd.setHeadImage(headImage);
				userUpd.setFamilyBadge(familyBadge);
				userUpd.setDateTime(dateTime);
				userService.updateUser(userUpd);
			}
			nickName = user.getNickName();
		}
		
		//插入用户出入日志表
		UserLog userLog = new UserLog();
		userLog.setRoomId(roomId);
		userLog.setUid(uid);
		userLog.setWealthLevel(wealthLevel);
		userLog.setWealthLevelDesc(CommonUtil.getWealthLevelDesc(wealthLevel));
		userLog.setLoginDateTime(dateTime);
		userLog.setOnline(true);
		userLog.setHide(hide == 0 ? false : true);
		userLog.setOs(os);
		userLogService.addUserLog(userLog);
		
		logger.info(prefix() + nickName+"("+uid+")" + (hide==1?"[hidden]":"") + " came in!");
		
	}

	/**
	 * 神秘人上线操作
	 * @param uId
	 */
	public void insertHideMan(Set<Integer> hideMan){
		for(int uid : hideMan){
			//插入用户出入日志表
			UserLog userLog = new UserLog();
			userLog.setRoomId(roomId);
			userLog.setUid(uid);
			userLog.setWealthLevel(0);
			userLog.setWealthLevelDesc(null);
			userLog.setLoginDateTime(CommonUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
			userLog.setOnline(true);
			userLog.setHide(true);
			userLog.setOs(-1);
			userLogService.addUserLog(userLog);
			
			//查询用户表
			User user = userService.queryUserById(uid);
			String nickName = "";
			if(user != null){
				nickName = user.getNickName();
			}
			logger.info(prefix() + nickName +"("+ uid + ")[hidden] came in!");					
		}
	}
	
	/**
	 * 下线用户记录
	 */
	public void offline(int uid){
		Date curDate = new Date();
		String dateTime = CommonUtil.formatDate(curDate, "yyyy-MM-dd HH:mm:ss");
		
		UserLog userLog = userLogService.queryLatestUserLogOnline(roomId, uid);
		if(userLog != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String loginDateTime = userLog.getLoginDateTime();
				long loginTime = 0L;
				try {
					loginTime = sdf.parse(loginDateTime).getTime();
				} catch (ParseException e) {
				}
				long logoutTime = curDate.getTime();
				String duration = CommonUtil.formatDuration(logoutTime - loginTime);
				
				userLog.setLogoutDateTime(dateTime);
				userLog.setDuration(duration);
				userLogService.offline(userLog);
				
				//查询用户表
				User user = userService.queryUserById(uid);
				String nickName = "";
				if(user != null){
					nickName = user.getNickName();
				}
				logger.info(prefix() + "----------------" + nickName +"("+ uid + (userLog.isHide() ? ")[hidden]" : ")") + " went out!");
		}
		
		// FIXME
		
	}
	
	/**
	 * 更新当前空号数
	 */
	public void updateBlankCount(int totalCount, int realCount, int adminCount, int robotCount, int blankCount){
		Room room = new Room();
		room.setRoomId(roomId);
		room.setTotalCount(totalCount);
		room.setRealCount(realCount);
		room.setAdminCount(adminCount);
		room.setRobotCount(robotCount);
		room.setBlankCount(blankCount);
		roomService.updateRoom(room);
	}

	/**
	 * 前缀
	 * @return
	 */
	public String prefix(){
		return "[" + roomId +"]["+roomName + "]"+ "--";
	}

	public void setActive(boolean isActive){
		this.isActive = isActive;
	}
	
	public void setRoomId(int roomId) {
		this.roomId = roomId;
		urlStr += roomId;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

}
