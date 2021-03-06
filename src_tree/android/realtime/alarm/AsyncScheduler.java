package android.realtime.alarm;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.PriorityParameters;
import javax.realtime.RealtimeThread;

import com.fiji.fivm.Time;

public class AsyncScheduler extends AlarmScheduler {

	private static AlarmRunnableWrapper run = null;
	
    public AsyncScheduler() {
        super();
    }

    public void schdulerAlarm(RealtimeAlarm alarm) {
    	run = new AlarmRunnableWrapper(alarm);
	RealtimeThread thread = new RealtimeThread(run);
	thread.start();
    }

    public class AlarmRunnableWrapper implements Runnable {
    	private RealtimeAlarm alarm;
        
        public AlarmRunnableWrapper(RealtimeAlarm alarm) {
            this.alarm = alarm;
        }
        
        public void setAlarm(RealtimeAlarm alarm){
        	this.alarm = alarm;
        }

        public void run() {
        	RealtimeAlarm alarm = this.alarm;
        	long timestamp = alarm.getTimeStamp();
        	long repeatInterval = alarm.getRepeatInterval();
        	int priority = alarm.getPriority();
        	PendingIntent activity = alarm.getActivity();
            RealtimeThread currThread = RealtimeThread.currentRealtimeThread();
            currThread.setSchedulingParameters(new PriorityParameters(priority));
            AsyncEventHandler handler = new AsyncEventHandler(activity.getRunnable());
            handler.setSchedulingParameters(new PriorityParameters(priority));
            handler.setDaemon(false);
            AsyncEvent event = new AsyncEvent();
            event.addHandler(handler);
            Time.sleepAbsolute(timestamp);
            event.fire();
            RealtimeAlarm.recycle(alarm);
            if (alarm.isRepeatable()) {
                schedulingThread.setAlarm(System.currentTimeMillis() + repeatInterval, priority, activity);
            }
        }
    }
}
