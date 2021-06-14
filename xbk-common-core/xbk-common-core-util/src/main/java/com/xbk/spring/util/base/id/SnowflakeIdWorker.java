package com.xbk.spring.util.base.id;

/**
 * 雪花算法
 */
public class SnowflakeIdWorker {

    private static SnowflakeIdWorker instance = null;

    public synchronized static SnowflakeIdWorker getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (instance == null) {
                    try{
                        instance = new SnowflakeIdWorker();
                        instance.initParam();
                    }catch (Exception e){
                        return null;
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 节点 ID 默认取1
     */
    private long workerId = 1;

    /**
     * 数据中心的ID 默认取1
     */
    private long datacenterId = 1;

    /**
     * 序列id 默认取1
     */
    private long sequence = 1;

    /**
     * 起始纪元
     */
    private long idepoch = 1379314066953L;

    /**
     * 机器标识位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据中心标识位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 机器ID最大值
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 数据中心ID最大值
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 毫秒内自增位
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID偏左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据中心ID左移17位
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间毫秒左移22位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits
            + datacenterIdBits;

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private void initParam() {
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("workerId is illegal: "
                    + workerId);
        }
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException("datacenterId is illegal: "
                    + workerId);
        }
    }

    private long getDatacenterId() {
        return datacenterId;
    }

    private long getWorkerId() {
        return workerId;
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    public long getId() {
        long id = nextId();
        return id;
    }

    /**
     * 获取id 线程安全
     *
     * @return
     */
    private synchronized long nextId() {
        long timestamp = timeGen();
        // 时间错误
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards.");
        }
        // 当前毫秒内，则+1
        if (lastTimestamp == timestamp) {
            // 当前毫秒内计数满了，则等待下一秒
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        long id = ((timestamp - idepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
        return id;
    }

    /**
     * 等待下一个毫秒的到来
     *
     * @param lastTimestamp
     * @return
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}       
