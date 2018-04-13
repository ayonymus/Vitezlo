package org.szvsszke.vitezlo2018.gpslogger;

import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;

public interface IGpsLoggerServiceMessages {
	
	public Messenger getGpsLoggerServiceMessenger();
	public void sendRequest(Bundle data, Handler replyTo);
	public void requestStatus(Handler replyTo);
	public void requestStartService(String logName, long base, Handler replyTo);
	public void requestStopService(Handler replyTo);
	public void restartService();
}
