package PM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
* @author      Yuting Chen
* @since       2016-04-10
*/



public class PMTest extends TestCase{

	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testReset()
	{
		PM.Reset();
		PM.sendPMMessage("a", 22);
		String FileName=PM.getFileName();
		File file=new File(FileName);
		PM.Reset();
		assertFalse(file.exists());
		assertTrue(PM.getMap().isEmpty());
	}

	@Test
	public void testGetFileNameNormal() {
		SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		assertEquals(target.format(new Date())+".log", PM.getFileName());
	}
	
	@Test
	public void testGetFileNameMore() {
		PM.Reset();
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		Date date = new Date();
		Calendar dar = Calendar.getInstance();
		dar.setTime(date);
		dar.add(Calendar.HOUR, 2);
		String FileName = dft.format(dar.getTime())+".log";
		String result = PM.getFileName();
		assertNotSame(FileName,result);
		//assertEquals(FileName,result);
	}

	@Test
	public void testSendPMMessage() {
		PM.Reset();
		PM.sendPMMessage("Alice", 12);
		Map<String, Integer> tempMap=PM.getMap();
		assertEquals(true,tempMap.containsKey("Alice"));
		assertEquals(new Integer(12),tempMap.get("Alice"));
		PM.Reset();
	}
	@Test
	public void testSendMorePMMessage() {
		PM.Reset();
		PM.sendPMMessage("Alice", 12);
		PM.sendPMMessage("Bob", 10);
		PM.sendPMMessage("Alice", 10);
		Map<String, Integer> tempMap=PM.getMap();
		assertEquals(true,tempMap.containsKey("Alice"));
		assertEquals(new Integer(22),tempMap.get("Alice"));
		assertEquals(true,tempMap.containsKey("Bob"));
		assertEquals(new Integer(10),tempMap.get("Bob"));
		PM.Reset();
	}
	@Test
	public void testSendErrorPMMessage() {
		PM.Reset();
		PM.sendPMMessage("error", -1);
		String FileName=PM.getFileName();
		File file=new File(FileName);
		assertFalse(file.exists());
		
		PM.sendPMMessage(null, -1);
		assertFalse(file.exists());
		
	}
	@Test
	
	public void testOutputNormal() {
		PM.Reset();
		PM.sendPMMessage("Loki", 18);
		PM.sendPMMessage("MMM", 17);
		PM.sendPMMessage("Loki", 1000);
		File target = new File("LOG//"+PM.getFileName());
		//System.out.println(target.getAbsolutePath());
		assertTrue(target.exists());
		
		try {
			BufferedReader bf=new BufferedReader(new FileReader(target));
			String FirstLine=bf.readLine();
			String SecondLine=bf.readLine();
			
			assertEquals(FirstLine, "Loki:1018");
			assertEquals(SecondLine, "MMM:17");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	@Test
	public void testMutiThread() throws InterruptedException
	{
		PM.Reset();
		int threadNum=1000;
		CountDownLatch runningThreadNum = new CountDownLatch(threadNum);
		for(int i=0;i<threadNum;i++)
		{
			new testThread(runningThreadNum).start();
		}
		runningThreadNum.await();
		
		File target = new File("LOG//"+PM.getFileName());
		//System.out.print(target.getAbsolutePath());
		assertTrue(target.exists());
		try {
			FileReader fr=new FileReader(target);
			BufferedReader bf=new BufferedReader(fr);
			String FirstLine=bf.readLine();
			assertEquals(FirstLine, "TestThread:"+threadNum*5);
			bf.close();
			fr.close();
			PM.Reset();
			assertFalse(target.exists());
			assertTrue(PM.getMap().isEmpty());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	public class testThread extends Thread
	{
		CountDownLatch runingNum;
		public testThread(CountDownLatch runingNum) {
			// TODO Auto-generated constructor stub
			this.runingNum=runingNum;
		}
		@Override
		public void run()
		{
			int times=5;
			while(times>0)
			{
				times--;
				try {
					sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				PM.sendPMMessage("TestThread", 1);
			}
			runingNum.countDown();
		}
	}
	public static void main(String[] args) {
        junit.textui.TestRunner.run(
            PMTest.class);
	}
}
