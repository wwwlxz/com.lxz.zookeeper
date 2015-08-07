package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeper_Create_API_Sync_Usage implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException{
		ZooKeeper zooKeeper = new ZooKeeper("123.103.79.120:2181",
					5000,
					new ZooKeeper_Create_API_Sync_Usage());
		connectedSemaphore.await();
		String path1 = zooKeeper.create("/zk-test-ephemeral-", 
					"".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL);//创建一个临时节点
		System.out.println("Success create znode: " + path1);
		
		String path2 = zooKeeper.create("/zk-test-ephemeral-", 
					"".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL_SEQUENTIAL);//创建一个临时顺序节点
		System.out.println("Success create znode: " + path2);
	}
	
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			connectedSemaphore.countDown();
		}
	}
	
}
