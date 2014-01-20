package com.zookeeper.test;

import java.io.IOException;

import javax.mail.Folder;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zookeeper.pool.ZooKeeperGenericObjectPool;
import com.zookeeper.pool.ZookeeperPoolableObjectFactory;

public class ZookeeperTest {
    private static Logger logger = LoggerFactory.getLogger(ZookeeperTest.class);

    static {
        // BasicConfigurator.configure();
    }

    public static void main(String[] args) throws Exception {

         testGenericPoolTime(10000, 10);
        // testStackPoolTime(10000 , 10);

        // testCommon(1000,1);
        
        //System.out.println((9.5547/0.0251));
        
        
    }

    public static void testGenericPoolTime(int loopTime, int avgTime) throws Exception {

        // PoolableObjectFactory<ZooKeeper> factory = new ZookeeperPoolableObjectFactory("localhost:2181");

        ZooKeeperGenericObjectPool pool = new ZooKeeperGenericObjectPool();
        // GenericObjectPool<ZooKeeper> pool = new GenericObjectPool<ZooKeeper>(factory);

        try {
            logger.info("==================== First Test start============================");
            double[] array = new double[avgTime];
            for (int i = 0; i < avgTime; i++) {
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < loopTime; j++) {
                    ZooKeeper zookeeper = null;
                    try {
                        zookeeper = pool.borrowObject();
                    } catch (Exception ex) {
                        pool.invalidateObject(zookeeper);
                    } finally {
                        pool.returnObject(zookeeper);
                    }
                }
                logger.info("==================== First Test end==============================");
                long endTime = System.currentTimeMillis();
                double useTime = ((endTime - startTime) / 1000.00);
                System.out.println(useTime + "s");
                array[i] = useTime;
            }

            double sum = 0d;
            for (int i = 0; i < avgTime; i++) {
                sum += array[i];
            }
            System.out.println("Pool: crate connection " + loopTime + ", AVG time: " + sum / avgTime);

        } finally {
            try {
                // pool.closeZooKeeper();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * test pool time when create object
     * 
     * @param loopTime
     * @param avgTime
     * @throws Exception
     */
    public static void testStackPoolTime(int loopTime, int avgTime) throws Exception {
        PoolableObjectFactory factory = new ZookeeperPoolableObjectFactory("localhost:2181");
        ObjectPool pool = new StackObjectPool(factory);

        try {
            logger.info("==================== First Test start============================");
            double[] array = new double[avgTime];
            for (int i = 0; i < avgTime; i++) {
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < loopTime; j++) {
                    ZooKeeper zookeeper = (ZooKeeper)pool.borrowObject();
                    try {
                        // System.out.println("ZooKeeper SeessionId: "+zookeeper.getSessionId());
                    } catch (Exception ex) {
                        pool.invalidateObject(zookeeper);
                    } finally {
                        pool.returnObject(zookeeper);
                    }
                }
                logger.info("==================== First Test end==============================");
                long endTime = System.currentTimeMillis();
                double useTime = ((endTime - startTime) / 1000.00);
                System.out.println(useTime + "s");
                array[i] = useTime;
            }

            double sum = 0d;
            for (int i = 0; i < avgTime; i++) {
                sum += array[i];
            }
            System.out.println("Pool: crate connection " + loopTime + ", AVG time: " + sum / avgTime);

        } finally {
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * pool to cache and creaqte multiple object
     * 
     * @param loopTime
     * @throws Exception
     */
    public void multipleTest(int loopTime) throws Exception {

        PoolableObjectFactory factory = new ZookeeperPoolableObjectFactory("localhost:2181");
        ObjectPool pool = new StackObjectPool(factory);

        logger.info("********************** Second test start *************************");
        for (int i = 0; i < loopTime; i++) {

            ZooKeeper zookeeper1 = (ZooKeeper)pool.borrowObject();
            ZooKeeper zookeeper2 = (ZooKeeper)pool.borrowObject();

            try {
                System.out.println("ZooKeeper1 SeessionId: " + zookeeper1.getSessionId());
            } catch (Exception ex) {
                pool.invalidateObject(zookeeper1);
            } finally {
                pool.returnObject(zookeeper1);
            }

            try {
                System.out.println("ZooKeeper2 SeessionId: " + zookeeper2.getSessionId());
            } catch (Exception ex) {
                pool.invalidateObject(zookeeper2);
            } finally {
                pool.returnObject(zookeeper2);
            }

            pool.returnObject(zookeeper1);
            pool.returnObject(zookeeper2);

        }
        logger.info("********************** Second test end *************************");
    }

    public static void testCommon(int loopTime, int avgTime) {
        String connectionString = "localhost:2181";

        double[] array = new double[avgTime];

        for (int i = 0; i < avgTime; i++) {
            long startTime = System.currentTimeMillis();
            for (int j = 0; j < loopTime; j++) {

                try {
                    ZooKeeper zoo = new ZooKeeper(connectionString, 3000, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long endTime = System.currentTimeMillis();
            double useTime = ((endTime - startTime) / 1000.00);
            System.out.println(useTime + "s");
            array[i] = useTime;
        }

        double sum = 0d;
        for (int i = 0; i < avgTime; i++) {
            sum += array[i];
        }
        System.out.println(" Common: crate connection " + loopTime + ", avg use time: " + sum / avgTime);

    }

}