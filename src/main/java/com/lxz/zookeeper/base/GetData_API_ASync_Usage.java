package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class GetData_API_ASync_Usage implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
		String path = "/zk-book";
		zk = new ZooKeeper("123.103.79.120:2181",
				5000, new GetData_API_ASync_Usage());
		connectedSemaphore.await();
		
		zk.create(path, 
				"123".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, 
				CreateMode.EPHEMERAL);
		zk.getData(path, true, new IDataCallback(), null);
		zk.setData(path, "123".getBytes(), -1);
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	public void process(WatchedEvent event) {
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}else if(event.getType() == EventType.NodeDataChanged){
				try {
					zk.getData(event.getPath(), true, new IDataCallback(), null);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
	}

}

class IDataCallback implements AsyncCallback.DataCallback{

	public void processResult(int rc, String path, Object ctx, byte[] data,
			Stat stat) {
		System.out.println(rc + "," + path + "," + new String(data));
		System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," + 
						stat.getVersion());
	}
	
}