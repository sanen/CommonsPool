package com.zookeeper.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperPoolableObjectFactory implements PoolableObjectFactory<ZooKeeper> {

    /**
     * define logger root
     */
    private static Logger logger = LoggerFactory.getLogger(ZookeeperPoolableObjectFactory.class);

    /**
     * define zookeeper connection address when create zooleeper object
     */
    private String connectionString;
    private int sessionTimeOut=3000;
    
    /**
     * define some variable when create PoolableObjectFactory
     */
    public ZookeeperPoolableObjectFactory(String connectionString) {
        this.connectionString = connectionString;
    }
    
    public ZookeeperPoolableObjectFactory(String connectionString,int sessionTimeOut) {
        this.connectionString = connectionString;
        this.sessionTimeOut=sessionTimeOut;
    }

    public ZooKeeper makeObject() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(connectionString, sessionTimeOut, null);
        return zooKeeper;
    }

    public void activateObject(ZooKeeper obj) throws Exception {
        logger.info("activateObject ZooKeeper ");
    }

    @SuppressWarnings("unused")
    public void passivateObject(ZooKeeper obj) throws Exception {
        ZooKeeper zooKeeper = (ZooKeeper)obj;
        logger.info("passivateObject ZooKeeper ");
    }

    public boolean validateObject(ZooKeeper obj) {
        logger.info("validateObject ZooKeeper ");
        return true;
    }

    public void destroyObject(ZooKeeper obj) throws Exception {
        if (obj instanceof ZooKeeper) {
            ((ZooKeeper)obj).close();
            logger.info("destroyObject ZooKeeper ");
        }
        
    }
}
