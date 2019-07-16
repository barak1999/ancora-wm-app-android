package com.hw.device_ir;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private hardware hwSerialport  = new hardware();
	private final    String TAG  = "Android-D";
	
	// 界面上的一些控件
	private Button   		button_setting;
	private Button   		button_scan;
	private TextView       	curDisplay = null;

	
	private int fd   = 0;
	private int baud = 115200;
	
	private byte[] read_buffer = new byte[1024];
	private byte[] write_buffer= new byte[1024];
	
	
	private read_thread   mreadTh;
	private int  sum_scan = 0;
	
	// 读显 锁
	private static Object rxlock   = new Object();
	private Queue<String> rx_queue = new LinkedList<String>();
	private String head_queue = null;
	private Handler mHandler  = null;
	private final int Message_display = 0x0101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 界面上的一些空件
		button_setting = (Button)findViewById(R.id.button_setting);
		button_scan    = (Button)findViewById(R.id.button_scan);
		curDisplay 	   = (TextView)findViewById(R.id.display_rx);
		
		// 线程消息接收处理------------------------------------------------>
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) 
				{
					case Message_display:
							 synchronized (rxlock) {
									 head_queue = rx_queue.poll();
							 }
							 if(head_queue != null) {
								 if(sum_scan >= 20) {
									 sum_scan = 0;
									 curDisplay.setText("");
								 } else 
									 sum_scan++;
								 
								 curDisplay.append(head_queue + "\n");
							 }
					break;							
						default:
							Log.d(TAG,"----------->message error:"+msg.what);
				}
			}
		};
				
		mreadTh = new read_thread();
		mreadTh.start();
		
		fd = hwSerialport.serialport_open("/dev/ttyHSL1".getBytes(), 12, baud);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void do_it(View v)
	{
		if(v.equals(button_setting)) {
			/*			
			Intent intent = new Intent(this, ir_setting.class);
			startActivity(intent);
			*/
			
		} else if(v.equals(button_scan)) {
			
			/*
			 *  软件触发扫描
			 * 
			 * 
			byte[]  write_cmd = new byte[]{22, 84, 13};  
			int ret = hwSerialport.serialport_writen(fd, write_cmd, 3);
			Log.d(TAG, "write num:"+ret);
			*/
		}
	}
	
	@Override  
    protected void onResume() {  
        super.onResume();  
        Log.e(TAG, "start onResume~~~");  
    }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        Log.e(TAG, "start onPause~~~");  
    }  
    @Override  
    protected void onStop() {  
        super.onStop();  
        Log.e(TAG, "start onStop~~~");  
    }  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        hwSerialport.serialport_close(fd);
        fd = -1;
        Log.e(TAG, "start onDestroy~~~");  
    }  
	
	// 数据读取线程 ------------------------------------------------------->
		// read serialport data thread 
		public class read_thread extends Thread {
			private int       ret_receive = 0;
			
			@Override
			public void run() {
			 while (!Thread.currentThread().isInterrupted()) {  
				 	
				 if((fd > 0) && (hwSerialport.isReady(fd, 0, 1000*10) > 0)) {
						 ret_receive = hwSerialport.serialport_readn(fd, read_buffer, 100, 10*1000 );// 10ms read
					 	 if (ret_receive > 0) { 
					 		String insert_data = null;
			
								try {
									insert_data = new String(read_buffer, 0, ret_receive, "ISO-8859-1");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						
							Log.d(TAG, "---------->" + insert_data);
					 	
					 		synchronized (rxlock) {
					 			rx_queue.offer(insert_data);
					 		}
					 		
					 		Message msg = new Message();  
					 		msg.what = Message_display;  
					 		mHandler.sendMessage(msg);	
					 	}
				 }
		        try {  
		            Thread.sleep(10);  
		        } catch (InterruptedException e) {  
		            e.printStackTrace();
		        }
			 }  
			}
		}
}
