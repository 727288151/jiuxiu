/**
 * 
 */
package com.hualing365.jiuxiu.controller;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hualing365.jiuxiu.entity.User;
import com.hualing365.jiuxiu.entity.UserLog;
import com.hualing365.jiuxiu.service.IUserLogService;
import com.hualing365.jiuxiu.service.IUserService;

/**
 * 
 * @author im.harley.lee@qq.com
 * @since Mar 10, 2018 6:44:17 PM
 */
@RestController
public class UserController {
	
	@Autowired
	IUserService userService;
	
	@Autowired
	IUserLogService userLogService;

	@RequestMapping("/queryuser/{uid}")
	public User queryUserById(@PathVariable int uid){
		return userService.queryUserById(uid);
	}
	
	@RequestMapping("/l/{roomId}/{count}")
	public String queryHistory(@PathVariable int roomId, @PathVariable Integer count){
		if(count == null || count == 0){
			count = 20;
		}
		List<UserLog> userLogList = userLogService.queryUserLog(roomId, count);
		List<UserLog> userLogListOnline = userLogService.queryAllUserOnline(roomId);
		userLogList.addAll(userLogListOnline);
		StringBuilder result = new StringBuilder();
		for(int i=0; i<userLogList.size(); i++){
			UserLog ul = userLogList.get(i);
			result.append(ul.getWealthLevel()).append(".")
				.append(ul.getNickName()).append("(").append(ul.getUid()).append("-").append(ul.getAccountId()).append(")")
				.append(ul.isHide()?"(隐)[":"[").append(ul.getOs()).append("]:<br/>")
				.append(ul.getLoginDateTime().substring(11)).append("-")
				.append(ul.getLogoutDateTime()==null?"":ul.getLogoutDateTime().substring(11)).append("<br/>");
		}
		return result.toString();
	}
	
	@Autowired
	DataSource dataSource;

	@RequestMapping("/init_user")
	public void testIns(){
		/*String sql = "insert into t_user (uid,accountid,nickname,headimage,familybadge,datetime,remark)values(9999,9999,'bbbb','','','','')";
		try {
			Connection conn = dataSource.getConnection();
			conn.setAutoCommit(true);
			boolean result = conn.prepareStatement(sql).execute(sql);
			System.out.println(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
            Connection conn = dataSource.getConnection();
            ClassPathResource rc = new ClassPathResource("user.sql");
            EncodedResource er = new EncodedResource(rc, "utf-8");
            ScriptUtils.executeSqlScript(conn, er, false, true, "--", null, "/*", "*/");
            rc = new ClassPathResource("user_history.sql");
            er = new EncodedResource(rc, "utf-8");
            ScriptUtils.executeSqlScript(conn, er, false, true, "--", null, "/*", "*/");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
