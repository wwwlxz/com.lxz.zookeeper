package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeper_GetChildren_API_Sync_Usage implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zooKeeper = null;
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
		zooKeeper = new ZooKeeper("123.103.79.120:2181",
				5000,
				new ZooKeeper_GetChildren_API_Sync_Usage());
		connectedSemaphore.await();
		String path = "/zk-book";
		zooKeeper.create(path, 
				"".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, 
				CreateMode.PERSISTENT);//持久节点，创建父节点
		
		zooKeeper.create(path + "/c1",
				"".getBytes(),
				Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);//临时节点，创建子节点
		
		List<String> childrenList = zooKeeper.getChildren(path, true);
		System.out.println(childrenList);
		
		zooKeeper.create(path + "/c2", 
				"".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, 
				CreateMode.EPHEMERAL);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}else if(event.getType() == EventType.NodeChildrenChanged){
				try {
					System.out.println("ReGet Child: " + zooKeeper.getChildren(event.getPath(), true));
				} catch (Exception e) {
				}
			}
		}
	}

}
