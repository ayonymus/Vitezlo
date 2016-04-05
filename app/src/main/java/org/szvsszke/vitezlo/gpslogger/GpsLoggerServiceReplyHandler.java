package org.szvsszke.vitezlo.gpslogger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Helper class for encapsulating the outboxing of data Bundle from a message. 
 * */

public class GpsLoggerServiceReplyHandler extends Handler{
	
	private HandleReply mHandler;
		
	public GpsLoggerServiceReplyHandler(HandleReply handler) {
		mHandler = handler;
	}
	
	@Override
	public void handleMessage(Message reply) {
		super.handleMessage(reply);
		Bundle data = reply.getData();			
		int stateCode = data.getInt(GpsLoggerService.LOGGER_STATE);
		String logName = data.getString(GpsLoggerService.LOG_NAME);
		long baseTime = data.getLong(GpsLoggerService.BASE_TIME);
		mHandler.handleReply(stateCode, logName, baseTime);		
	}
	
	public interface HandleReply {
		public void handleReply(int stateCode, 
				String logName, long chronoBaseTime);
	}
	
	
}
