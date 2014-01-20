package com.zookeeper.pool.demo;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zookeeper.pool.ZookeeperPoolableObjectFactory;

public class ZooKeeperGenericObjectPoolDemo {

    private int maxActive = 50;
    private int minIdle = 1;
    private int maxIdle = 50;
    private int maxWait = 1000;

    private GenericObjectPool<ZooKeeper> sockPool = null;
    private static Logger logger = LoggerFactory.getLogger(ZooKeeperGenericObjectPoolDemo.class);
    private static String connectionString="localhost:2181";
    public ZooKeeperGenericObjectPoolDemo() {
        
        sockPool = new GenericObjectPool<ZooKeeper>();
        sockPool.setMaxActive(maxActive);
        sockPool.setMaxIdle(maxIdle);
        sockPool.setMinIdle(minIdle);
        sockPool.setMaxWait(maxWait);
        sockPool.setTestOnBorrow(false);
        sockPool.setTestOnReturn(false);
        sockPool.setTimeBetweenEvictionRunsMillis(10 * 1000);
        sockPool.setNumTestsPerEvictionRun(maxActive + maxIdle);
        sockPool.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        sockPool.setTestWhileIdle(true);
        
        // set PoolableObjectFactory, right now we use the ZookeeperPoolableObjectFactory
        sockPool.setFactory(new ZookeeperPoolableObjectFactory(connectionString));
        
        
    }

    @SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
    public ZooKeeperGenericObjectPoolDemo(String connectionString) {
        sockPool = new GenericObjectPool();
        sockPool.setMaxActive(maxActive);
        sockPool.setMaxIdle(maxIdle);
        sockPool.setMinIdle(minIdle);
        sockPool.setMaxWait(maxWait);
        sockPool.setTestOnBorrow(false);
        sockPool.setTestOnReturn(false);
        sockPool.setTimeBetweenEvictionRunsMillis(10 * 1000);
        sockPool.setNumTestsPerEvictionRun(maxActive + maxIdle);
        sockPool.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        sockPool.setTestWhileIdle(true);
        // set PoolableObjectFactory, right now we use the ZookeeperPoolableObjectFactory
        sockPool.setFactory(new ZookeeperPoolableObjectFactory(connectionString));

    }

    public ZooKeeper createSocket() {
        assert sockPool != null;
        try {
            return (ZooKeeper)sockPool.borrowObject();
        } catch (Exception e) {
            logger.error("get zookeeper connection throw the error from zookeeper connection pool, error message is {}",
                         e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        ZooKeeperGenericObjectPoolDemo pool = new ZooKeeperGenericObjectPoolDemo("localhost:2181");
        ZooKeeper zooKeeper = pool.createSocket();
        System.out.println(zooKeeper);
    }

}
