package com.lxz.zookeeper.base;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class AuthSample {
	final static String PATH = "/zk-book-auth_test";
	
	public static void main(String[] args) throws Exception{
		ZooKeeper zooKeeper = new ZooKeeper("123.103.79.120:2181", 5000, null);
		zooKeeper.addAuthInfo("digest", "foo:true".getBytes());
		zooKeeper.create(PATH, "init".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		Thread.sleep(Integer.MAX_VALUE);
	}
}
