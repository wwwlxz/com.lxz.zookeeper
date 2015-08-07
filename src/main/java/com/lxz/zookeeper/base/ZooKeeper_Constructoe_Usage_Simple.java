package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

//创建一个最基本的ZooKeeper会话实例
public class ZooKeeper_Constructoe_Usage_Simple implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);//锁存器
	
	public static void main(String[] args) throws IOException{
		ZooKeeper zooKeeper = new ZooKeeper("123.103.79.120:2181",
				5000, new ZooKeeper_Constructoe_Usage_Simple());
		System.out.println(zooKeeper.getState());
		try {
			connectedSemaphore.await();//等待WatchedEvent事件被触发
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("ZooKeeper session established.");
	}
	
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			connectedSemaphore.countDown();
		}
	}

}
