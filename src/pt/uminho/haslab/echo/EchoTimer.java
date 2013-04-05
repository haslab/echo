package pt.uminho.haslab.echo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class EchoTimer {
	private long startTime;
	private long currentTime;
	
	private Map<String,BigInteger> times = new HashMap<String,BigInteger>();
	
	public EchoTimer() {
		startTime = System.currentTimeMillis();
		currentTime = startTime;
	}
	
	public long setTime(String p) {
		long now = System.currentTimeMillis();
		long time = now - currentTime;
		times.put(p, BigInteger.valueOf(time));
		currentTime = now;
		return time;
	}
	
	public long getTime(String p) {
		return times.get(p).longValue();
	}
	
	/** Return total running time */
	public long getTime() {
		return System.currentTimeMillis() - startTime;
	}
	
}
