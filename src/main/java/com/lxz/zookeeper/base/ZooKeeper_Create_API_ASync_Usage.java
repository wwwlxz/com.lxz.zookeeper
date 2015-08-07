package com.lxz.zookeeper.base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZooKeeper_Create_API_ASync_Usage implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, InterruptedException{
		ZooKeeper zooKeeper = new ZooKeeper("123.103.79.120:2181",
					5000,
					new ZooKeeper_Create_API_ASync_Usage());
		connectedSemaphore.await();
		
		zooKeeper.create("/zk-test-ephemeral-", 
					"".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL,
					new IStringCallback(),
					"I am context.");
		
		zooKeeper.create("/zk-test-ephemeral-", 
					"".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL,
					new IStringCallback(),
					new Person("Lxz"));//重复创建临时节点，会产生异常
		
		zooKeeper.create("/zk-test-ephemeral-", 
					"".getBytes(), 
					Ids.OPEN_ACL_UNSAFE, 
					CreateMode.EPHEMERAL_SEQUENTIAL,
					new IStringCallback(),
					"I am context.");
		
		Thread.sleep(Integer.MAX_VALUE);
		
	}
	
	public void process(WatchedEvent event) {
		System.out.println("Receive watched event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			connectedSemaphore.countDown();
		}
	}

}

class Person{
	private String name;
	
	public Person(String name){
		this.name = name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return "Person [name=" + name +"]";
	}
}

class IStringCallback implements AsyncCallback.StringCallback{
	
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("Create path result: [" + rc + ", " + path + ", "
						+ ctx + ", real path name: " + name);
	}
	
}
