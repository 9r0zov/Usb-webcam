package com.pony101.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DataTransfer<T> {

    private final ReentrantLock getLock = new ReentrantLock();
    private final Condition getCondition = getLock.newCondition();

    private final ReentrantLock setLock = new ReentrantLock();

    private T data;

    public T getData() throws InterruptedException {
        try {
            getLock.lockInterruptibly();
            while (data == null) {
                getCondition.await();
            }

            return data;
        } finally {
            getLock.unlock();
        }
    }

    public void setData(T data) throws InterruptedException {
        try {
            setLock.lockInterruptibly();
            this.data = data;
        } finally {
            setLock.unlock();
        }

    }

}
