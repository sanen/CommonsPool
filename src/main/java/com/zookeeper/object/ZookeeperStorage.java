package com.zookeeper.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperStorage{

    private static Logger logger = LoggerFactory.getLogger(ZookeeperStorage.class);

    private String name;
    private boolean connected;

    public ZookeeperStorage(String name) {
        this.name = name;
    }

    public void connect() {
        this.connected = true;
        logger.info(name + ": " + connected);
    }

    public void close() {
        this.connected = false;
        logger.info(name + ": " + connected);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getName() {
        return this.name;
    }

    public void print() {
        logger.info(this.name);
    }
}