/**
 * 
 */
package com.hualing365.jiuxiu.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hualing365.jiuxiu.entity.UserLog;
import com.hualing365.jiuxiu.service.IRoomService;
import com.hualing365.jiuxiu.service.IUserLogService;
import com.hualing365.jiuxiu.wx.SignUtil;

/**
 * 微信验证入口
 * @author im.harley.lee@qq.com
 * @since Mar 12, 2018 7:36:38 PM
 */
@RestController
public class WxController {
	
	final Logger logger = LoggerFactory.getLogger(WxController.class);
	
	@Autowired
	IUserLogService userLogService;
	
	@Autowired
	IRoomService roomService;

	@GetMapping("/wx")
	public String doGet(String signature, String timestamp, String nonce, String echostr){
		// 1、微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。   
        // 2、时间戳   
        // 3、随机数   
        // 4、随机字符串
		System.out.println("come in verify...");
		if(SignUtil.checkSignature(signature,timestamp, nonce)){
			System.out.println("come in verify...pass");
			return echostr;
		}else{
			System.out.println("come in verify...failed");
		}
		return null;  
	}
	
	@PostMapping("/wx")
	public String doPost(HttpServletRequest request){
		StringBuilder result = new StringBuilder();
		String fromUserName = null;
		String toUserName = null;
		try {
			//String data = readStream(request.getInputStream());
			
			Map<String, String> map = parseXml(request.getInputStream());
			String msgType = map.get("MsgType");
			if("text".equals(msgType)){
				fromUserName = map.get("FromUserName");
				toUserName = map.get("ToUserName");
				String content = map.get("Content");
				String[] arr = content.split("-");
				List<UserLog> userLogList = new ArrayList<UserLog>();
				try{
					if(arr.length == 1) {
						userLogList = userLogService.queryAllUserOnline(Integer.valueOf(arr[0]));
						
					} else if(arr.length == 2){
						//on-off
						if(arr[1].equals("on")){
							roomService.updateRoomOnOff(Integer.valueOf(arr[0]), 1);
						}else if(arr[1].equals("off")){
							roomService.updateRoomOnOff(Integer.valueOf(arr[0]), 0);
						}else if(arr[1].equals("add")){
							
						}
						userLogList = userLogService.queryUserLog(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]));
					
					} else if(arr.length == 3){
						
						userLogList = userLogService.queryUserLog(Integer.valueOf(arr[0]), Integer.valueOf(arr[1]), Integer.valueOf(arr[2]));
					}
				}catch(Exception e){
					//ignore
				}
				for(int i=userLogList.size()-1; i>=0; i--){
					UserLog ul = userLogList.get(i);
					result.append(i).append(".")
						.append(ul.getNickName()).append("(").append(ul.getWealthLevel()).append(")")
						.append(ul.isHide()?"(隐)":"").append(":").append("\n")
						.append(ul.getLoginDateTime().substring(11)).append("-")
						.append(ul.getLogoutDateTime()==null?"":ul.getLogoutDateTime().substring(11)).append("\n-----------------\n");
				}
			}
			if(result.length()==0){
				result.append(msgType);
			}
		} catch (IOException e) {
			//e.printStackTrace();
			//System.err.println(e.getMessage());
			logger.error(e.getMessage());
			result.append("Error:").append(e.getMessage());
		}

		String respData = "<xml><ToUserName><![CDATA["+fromUserName+"]]></ToUserName>"+
				"<FromUserName><![CDATA["+toUserName+"]]></FromUserName>"+
				"<CreateTime>"+new Date().getTime()+"</CreateTime>"+
				"<MsgType><![CDATA[text]]></MsgType>"+
				"<Content><![CDATA["+result.toString()+"]]></Content>"+
				"<MsgId></MsgId>"+
				"<AgentID>2</AgentID></xml>";
		logger.info("resp:"+respData);
		return respData;
	}
	
	
	/**
	 * 从输入流读取post参数  
	 * @param in
	 * @return
	 */
    public String readStream(InputStream in){  
        StringBuilder buffer = new StringBuilder();  
        BufferedReader reader=null;  
        try{  
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));  
            String line=null;  
            while((line = reader.readLine())!=null){  
                buffer.append(line);  
            }  
        }catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            if(null!=reader){  
                try {  
                    reader.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return buffer.toString();  
    }
    
    public Map<String, String> parseXml(InputStream in){
    	Map<String, String> map = new HashMap<String, String>();
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(in);
			Element root = doc.getDocumentElement();
			NodeList nodeList = root.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				map.put(node.getNodeName(), node.getTextContent());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
		return map;
    }
	
}
