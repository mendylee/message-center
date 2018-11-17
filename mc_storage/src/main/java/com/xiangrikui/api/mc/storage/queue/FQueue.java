/*
 *  Copyright 2011 sunli [sunli1223@gmail.com][weibo.com@sunli1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiangrikui.api.mc.storage.queue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xiangrikui.api.mc.storage.queue.fqueue.FSQueue;
import com.xiangrikui.api.mc.storage.queue.fqueue.exception.FileFormatException;

/**
 * 基于文件系统的持久化队列
 * 
 * @author sunli
 * @date 2010-8-13
 * @version $Id: FQueue.java 2 2011-07-31 12:25:36Z sunli1223@gmail.com $
 */
public class FQueue<T> extends AbstractQueue<T> implements Queue<T>, java.io.Serializable 
{
	private static final long serialVersionUID = -5960741434564940154L;
	private final static Logger LOGGER = LoggerFactory.getLogger(FQueue.class);
	
	private FSQueue fsQueue = null;
	private Kryo kryo = null;
	private Class<T> classType = null;
	
	private Lock lock = new ReentrantReadWriteLock().writeLock();

	public FQueue(String path, Class<T> t) throws Exception 
	{
		this(path, 1024 * 1024 * 300, t);
	}

	public FQueue(String path, int logsize, Class<T> t) throws Exception 
	{
		fsQueue = new FSQueue(path, logsize);
		kryo = new Kryo();
		kryo.register(t);
		classType = t;
	}

	@Override
	public Iterator<T> iterator()
	{
		throw new UnsupportedOperationException("iterator Unsupported now");
	}

	@Override
	public int size() 
	{
		return fsQueue.getQueueSize();
	}

	@Override
	public boolean offer(T e) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		Output output = new Output(baos);
		kryo.writeObject(output, e);
		output.flush();
		return offerToFQueue(baos.toByteArray());
	}
	
	private boolean offerToFQueue(byte[] be)
	{
		try
		{
			lock.lock();
			fsQueue.add(be);
			return true;
		} 
		catch (IOException e1)
		{
			LOGGER.error(e1.getMessage(), e1);
		} 
		catch (FileFormatException e1) 
		{
			LOGGER.error(e1.getMessage(), e1);
		} 
		finally 
		{
			lock.unlock();
		}
		return false;
	}

	@Override
	public T peek() 
	{
		throw new UnsupportedOperationException("peek Unsupported now");
	}

	@Override
	public T poll()
	{
		byte[] be = pollFromQueue();
		if (be == null || be.length == 0)
		{
			//空
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(be);
		Input input = new Input(bais);
		T outObj = kryo.readObject(input, classType);
		return outObj;
	}
	
	private byte[] pollFromQueue()
	{
		try 
		{
			lock.lock();
			return fsQueue.readNextAndRemove();
		} 
		catch (IOException e) 
		{
			LOGGER.error(e.getMessage(), e);
			return null;
		} 
		catch (FileFormatException e)
		{
			LOGGER.error(e.getMessage(), e);
			return null;
		} 
		finally
		{
			lock.unlock();
		}
	}

	public void close() 
	{
		if (fsQueue != null) 
		{
			fsQueue.close();
		}
	}
}
