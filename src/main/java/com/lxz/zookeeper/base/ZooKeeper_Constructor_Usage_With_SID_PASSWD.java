package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, InterruptedException{
		ZooKeeper zooKeeper = new ZooKeeper("123.103.79.120:2181",
						5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
		connectedSemaphore.await();
		long sessionId = zooKeeper.getSessionId();
		byte[] passwd = zooKeeper.getSessionPasswd();
		
		//Use illegal sessionId and sessionPasswd
		zooKeeper = new ZooKeeper("123.103.79.120:2181",
						5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),
						1l,
						"test".getBytes());
		
		//Use correct sessionId and sessionPasswd
		zooKeeper = new ZooKeeper("123.103.79.120:2181",
				5000, new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),
				sessionId,
				passwd);/**/
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			connectedSemaphore.countDown();
		}
	}

}
