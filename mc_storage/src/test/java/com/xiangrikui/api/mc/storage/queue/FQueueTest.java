package com.xiangrikui.api.mc.storage.queue;

import java.util.Queue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FQueueTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		try
		{
			Queue<TestUser> queue = new FQueue<>("E:\\FQUEUE", TestUser.class);
			TestUser testUser = new TestUser();
			testUser.setGender(1);
			testUser.setName("user");
			queue.offer(testUser);
			testUser = null;
			
			
			
			testUser = queue.poll();
			System.out.println(testUser.toString());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static class TestUser
	{
		private String name;
		private int gender;
		public String getName()
		{
			return name;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public int getGender()
		{
			return gender;
		}
		public void setGender(int gender)
		{
			this.gender = gender;
		}
		@Override
		public String toString()
		{
			return "TestUser [name=" + name + ", gender=" + gender + "]";
		}
	}
}
