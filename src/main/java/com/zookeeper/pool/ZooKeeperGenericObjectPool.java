package com.zookeeper.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZooKeeperGenericObjectPool extends GenericObjectPool<ZooKeeper>{

    private ZooKeeperGenericObjectPool zooKeeperPool = null;
    private static Properties zookeeperConfigProperties = null;
    private static String configPropertiesString = "zookeeperPool.properties";
    private static Logger logger = LoggerFactory.getLogger(ZooKeeperGenericObjectPool.class);
    
 
    @SuppressWarnings({ "deprecation" })
    public ZooKeeperGenericObjectPool() {
        ZooKeeperGenericObjectPool(null,null);

        try {

            zookeeperConfigProperties = new Properties();

            InputStream inputStream = ZooKeeperGenericObjectPool.class.getClassLoader()
                                                                      .getResourceAsStream(configPropertiesString);

            zookeeperConfigProperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxActive = Integer.parseInt(zookeeperConfigProperties.getProperty("maxActive"));
        logger.debug("zookeeper pool maxActive is {} ", maxActive);

        int minIdle = Integer.parseInt(zookeeperConfigProperties.getProperty("minIdle"));
        logger.debug("zookeeper pool minIdle is {} ", minIdle);

        int maxIdle = Integer.parseInt(zookeeperConfigProperties.getProperty("maxIdle"));
        logger.debug("zookeeper pool maxIdle is {} ", maxIdle);

        int maxWait = Integer.parseInt(zookeeperConfigProperties.getProperty("maxWait"));
        logger.debug("zookeeper pool maxWait is {} ", maxWait);

        String connectionString = zookeeperConfigProperties.getProperty("zookeeper_server");
        logger.debug("zookeeper pool zookeeper_server is {} ", connectionString);
        
        int sessionTimeOut=Integer.parseInt(zookeeperConfigProperties.getProperty("sessionTimeOut"));

        this.setMaxActive(maxActive);
        this.setMaxIdle(maxIdle);
        this.setMinIdle(minIdle);
        this.setMaxWait(maxWait);
        this.setTestOnBorrow(false);
        this.setTestOnReturn(false);
        this.setTimeBetweenEvictionRunsMillis(10 * 1000);
        this.setNumTestsPerEvictionRun(maxActive + maxIdle);
        this.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        this.setTestWhileIdle(true);
        // set PoolableObjectFactory, right now we use the ZookeeperPoolableObjectFactory
        this.setFactory(new ZookeeperPoolableObjectFactory(connectionString,sessionTimeOut));

    }

    private void ZooKeeperGenericObjectPool(Object object, Object object2) {
        // TODO Auto-generated method stub
        
    }

    public ZooKeeper createSocket() {
        assert zooKeeperPool != null;
        try {
            return (ZooKeeper)zooKeeperPool.borrowObject();
        } catch (Exception e) {
            logger.error("get zookeeper connection throw the error from zookeeper connection pool, error message is {}",
                         e.getMessage());
        }
        return null;
    }

    public void closeZooKeeper() {
        assert zooKeeperPool != null;
        try {
            zooKeeperPool.close();
        } catch (Exception e) {
            logger.error(" throw the error when close the zookeeper pool, error message is {}", e.getMessage());
        }
    }

    /*public static void main(String[] args) {
        ZooKeeperGenericObjectPool pool = new ZooKeeperGenericObjectPool();
        for (int i = 0; i < 10; i++) {
            ZooKeeper zooKeeper = pool.createSocket();
            System.out.println("ZooKeeper SeessionId: " + zooKeeper.getSessionId());
        }
        pool.closeZooKeeper();
    }*/

}
