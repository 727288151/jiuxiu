/**
 * 
 */
package com.hualing365.jiuxiu.controller;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hualing365.jiuxiu.entity.User;
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

	@RequestMapping("/queryuser/{uid}")
	public User queryUserById(@PathVariable int uid){
		return userService.queryUserById(uid);
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
            ClassPathResource rc = new ClassPathResource("user.txt", this.getClass());
            EncodedResource er = new EncodedResource(rc, "utf-8");
            ScriptUtils.executeSqlScript(conn, er, true, true, "--", null, "/*", "*/");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
