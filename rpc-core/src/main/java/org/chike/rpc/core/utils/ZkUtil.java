package org.chike.rpc.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.chike.rpc.core.config.RpcConfig;
import org.chike.rpc.core.enums.RpcConfigEnum;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ZkUtil {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_THRESHOLD = 10; // 10s
    private static final String DEFAULT_ZK_SERVER = "127.0.0.1:2181";
    private static final Set<String> CACHED_EXIST_NODES = ConcurrentHashMap.newKeySet();
    private static final Map<String, List<String>> CACHED_CHILD_NODES = new ConcurrentHashMap<>();

    public static final String ZK_REGISTER_ROOT_PATH = "/xprc";

    private static CuratorFramework baseClient;

    public static CuratorFramework getZkClient() {
        // if zkClient has been started, return directly
        if (baseClient != null && baseClient.getState() == CuratorFrameworkState.STARTED) {
            return baseClient;
        }

        // check if user has set zk address
        String zookeeperAddress = RpcConfig.getProperty(
                RpcConfigEnum.ZK_ADDRESS.getPropertyValue(),
                DEFAULT_ZK_SERVER
        );

        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        baseClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();

        baseClient.start();

        try {
            // wait 10s until connect to the zookeeper
            if (!baseClient.blockUntilConnected(TIMEOUT_THRESHOLD, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return baseClient;
    }

    public static boolean checkPathExisted(String path) throws Exception {
        CuratorFramework zkClient = getZkClient();
        return CACHED_EXIST_NODES.contains(path) || zkClient.checkExists().forPath(path) != null;
    }

    public static boolean createPersistentNode(String path) {
        CuratorFramework zkClient = getZkClient();
        try {
            if (checkPathExisted(path)) {
                log.info("The node already exists. The node is:[{}]", path);
                return false;
            }
            // eg: "/xrpc/xxxService/124.213.23.1:1998"
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            log.info("The node was created successfully. The node is:[{}]", path);
            CACHED_EXIST_NODES.add(path);
            return true;
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
            return false;
        }
    }

    public static List<String> getChildrenNodes(String path) {
        return CACHED_CHILD_NODES.computeIfAbsent(path, notUsed -> {
            CuratorFramework zkClient = getZkClient();
            List<String> result = null;
            try {
                if (!checkPathExisted(path)) {
                    log.error("node for path [{}] is not existed", path);
                }
                result = zkClient.getChildren().forPath(path);
                // 设置 Watcher，监控变化
                registerWatcher(path);
            } catch (Exception e) {
                log.error("get children nodes for path [{}] fail", path);
            }
            return result;
        });
    }

    private static void registerWatcher(String path) throws Exception {
        CuratorFramework zkClient = getZkClient();
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(path);
            CACHED_CHILD_NODES.put(path, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public static void clearRegistry(InetSocketAddress inetSocketAddress) {
        CuratorFramework zkClient = getZkClient();
        CACHED_EXIST_NODES.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", CACHED_EXIST_NODES.toString());
    }
}
