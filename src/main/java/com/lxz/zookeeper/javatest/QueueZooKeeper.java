package com.lxz.zookeeper.javatest;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class QueueZooKeeper {
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
		/*if(args.length == 0){
			doOne();
		}else{
			doAction(Integer.parseInt(args[0]));
		}*/
//		doAction(Integer.parseInt("1"));
//		doAction(Integer.parseInt("2"));
		doAction(Integer.parseInt("3"));
	}
	
	//模拟app1，通过zk1，提交三个请求
	public static void doOne() throws IOException, KeeperException, InterruptedException{
		String host1 = "192.168.192.101:2181";
		ZooKeeper zk = connection(host1);
		initQueue(zk);
		joinQueue(zk, 1);
		joinQueue(zk, 2);
		joinQueue(zk, 3);
		zk.close();
	}
	
	
	public static void doAction(int client) throws IOException, KeeperException, InterruptedException{
		String host1 = "192.168.192.101:2181";
		String host2 = "192.168.192.102:2181";
		String host3 = "192.168.192.103:2181";
		
		ZooKeeper zk = null;
		switch(client){
		case 1:
			zk = connection(host1);
			initQueue(zk);
			joinQueue(zk, 1);
			break;
		case 2:
			zk = connection(host2);
			initQueue(zk);
			joinQueue(zk, 2);
			break;
		case 3:
			zk = connection(host3);
			initQueue(zk);
			joinQueue(zk, 3);
			break;
		}
	}
	
	//创建一个与服务器的连接
	public static ZooKeeper connection(String host) throws IOException{
		ZooKeeper zk = new ZooKeeper(host, 60000, new Watcher(){
			//监控所有被触发的事件
			public void process(WatchedEvent event) {
				if(event.getType() == Event.EventType.NodeCreated && event.getPath().equals("/queue/start")){
					System.out.println("Queue has Completed. Finish testing !!!");
				}
			}
		});
		return zk;
	}

	public static void initQueue(ZooKeeper zk) throws KeeperException, InterruptedException{
		System.out.println("WATCH => /queue/start ");
		zk.exists("/queue/start", true);
		
		if(zk.exists("/queue", false) == null){
			System.out.println("create /queue task-queue");
			zk.create("/queue", "task-queue".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}else{
			System.out.println("/queue is exist!");
		}
	}
	
	//增加队列节点
	public static void joinQueue(ZooKeeper zk, int x) throws KeeperException, InterruptedException{
		System.out.println("create /queue/x " + x + " x" + x);
		zk.create("/queue/x" + x, ("x" + x).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		isCompleted(zk);
	}
	
	//检查队列是否完整
	public static void isCompleted(ZooKeeper zk) throws KeeperException, InterruptedException{
		int size = 3;
		int length = zk.getChildren("/queue", true).size();
		
		System.out.println("Queue Complete: " + length + "/" + size);
		if(length >= size){
			System.out.println("create /queue/start start");
			zk.create("/queue/start", "start".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		}
	}
}
