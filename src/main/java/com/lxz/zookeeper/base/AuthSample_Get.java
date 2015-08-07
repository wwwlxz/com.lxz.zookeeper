package com.lxz.zookeeper.base;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class AuthSample_Get {
	final static String PATH = "/zk-book-auth_test";
	
	public static void main(String[] args) throws Exception{
		ZooKeeper zooKeeper1 = new ZooKeeper("123.103.79.120:2181", 5000, null);
		zooKeeper1.addAuthInfo("digest", "foo:true".getBytes());
		zooKeeper1.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		ZooKeeper zooKeeper2 = new ZooKeeper("123.103.79.120:2181", 5000, null);
		zooKeeper2.getData(PATH, false, null);
	}
}
