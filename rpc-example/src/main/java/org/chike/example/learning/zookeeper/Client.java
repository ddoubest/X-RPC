package org.chike.example.learning.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Client {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory
                .builder()
                .connectString("140.210.200.195:21810")
                .retryPolicy(retryPolicy)
                .build();

        zkClient.start();

        String result = zkClient
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/node1/00001", "java".getBytes());

        System.out.println(result);

        byte[] data = zkClient.getData().forPath("/node1/00001");
        System.out.println(new String(data));

        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
    }
}
