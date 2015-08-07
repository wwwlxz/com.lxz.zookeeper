package com.lxz.zookeeper.javatest;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class FIFOZookeeper {
	public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
		if(args.length == 0){
			doOne();
		}else{
			doAction(Integer.parseInt(args[0]));
		}
	}
	
	public static void doOne() throws IOException, KeeperException, InterruptedException{
		String host1 = "192.168.192.101:2181";
		ZooKeeper zk = connection(host1);
		initQueue(zk);
		produce(zk, 1);
		produce(zk, 2);
		cosume(zk);
		cosume(zk);
		cosume(zk);
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
			produce(zk, 1);
			break;
		case 2:
			zk = connection(host2);
			initQueue(zk);
			produce(zk, 2);
			break;
		case 3:
			zk = connection(host3);
			initQueue(zk);
			cosume(zk);
			cosume(zk);
			cosume(zk);
			break;
		}
	}
	
	//create a connection to the server
	public static ZooKeeper connection(String host) throws IOException{
		return new ZooKeeper(host, 60000, new Watcher(){
			public void process(WatchedEvent event) {
				
			}
		});
	}
	
	public static void initQueue(ZooKeeper zk) throws KeeperException, InterruptedException{
		if(zk.exists("/queue-fifo",  false) == null){
			System.out.println("create /queue-fifo task-queue-fifo");
			zk.create("/queue-fifo", "task-queue-fifo".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}else{
			System.out.println("/queue-fifo is exist!");
		}
	}
	
	public static void produce(ZooKeeper zk, int x) throws KeeperException, InterruptedException{
		System.out.println("create /queue-fifo/x" + x + " x" + x);
		zk.create("/queue-fifo/x" + x, ("x" + x).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	public static void cosume(ZooKeeper zk) throws KeeperException, InterruptedException{
		List<String> list = zk.getChildren("/queue-fifo", true);
		if(list.size() > 0){
			long min = Long.MAX_VALUE;
			for(String num : list){
				if(min > Long.parseLong(num.substring(1))){
					min = Long.parseLong(num.substring(1));
				}
			}
			System.out.println("delete /queue/x" + min);
			zk.delete("/queue-fifo/x" + min, 0);
		}else{
			System.out.println("No node to cosume.");
		}
	}
}
