package com.hualing365.jiuxiu;

import javax.servlet.ServletContext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.hualing365.jiuxiu.scheduler.thread.AsyncScanRoomTask;

@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer{
	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		//return super.configure(builder);
		return builder.sources(MainApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
	
	@Override
	protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
		WebApplicationContext webCtx = super.createRootApplicationContext(servletContext);
		
		handler(webCtx);
		
		return webCtx;
	}
	
	private void handler(WebApplicationContext webAppCtx){
		
		/*ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) webCtx.getBean("taskExecutor");
		taskExecutor.*/
		
		AsyncScanRoomTask scanRoom = webAppCtx.getBean(AsyncScanRoomTask.class);
		scanRoom.executeAsyncTask(webAppCtx);
		
	}
}
