package com.hw.device_ir;

public class hardware {
	static {
		System.loadLibrary("hwcommonV1.0");
	}
	
	
	// hw control
	public native int lib_version_print();
	
	/*
	 *  index 0 ---------> ir
	 */
	public native int device_power_on  (int index, int flag);
	public native int device_power_down(int index, int flag);

	public native int serialport_open(byte[]  target, int length, int baud);
	public native int serialport_close(int fd);
	public native int isReady(int fd, int sec , int usec);
	public native int serialport_readn(int fd, byte[]  target, int getNum, int maxTime);
	public native int serialport_writen(int fd, byte[] target, int write_num);
	public native int serialprot_clean(int fd);
}
