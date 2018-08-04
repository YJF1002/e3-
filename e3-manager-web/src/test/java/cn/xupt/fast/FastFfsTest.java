package cn.xupt.fast;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.xupt.common.utils.FastDFSClient;

public class FastFfsTest {

	@Test
	public void testUpload() throws Exception{
		//创建一个配置文件，文件名任意，内容就是tracker服务器的地址
		//使用全局对象加载配置文件
		ClientGlobal.init("E:/JAVAEE/items/e3-manager-web/src/main/resources/conf/client.conf");
		//创建一个TrackClient对象
		TrackerClient trackerClient = new TrackerClient();
		//通过TrackClient获得一个TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建一个StrorageServer的引用，可以是null；
		StorageServer storageServer = null;
		//创建一个StorageClient，参数需要TrakerServer和StrorageServer
		StorageClient storageClient = new StorageClient();
		//使用StorageClient上传文件
		String[] strings = storageClient.upload_file("C:/Users/YJF/Pictures/2.jpg", "jpg", null);
		for(String string : strings){
			System.out.println(string);
		}
	}
	@Test
	public void testFastDfsClient() throws Exception{
		
		FastDFSClient fastDFSClient  = new FastDFSClient("E:/JAVAEE/items/e3-manager-web/src/main/resources/conf/client.conf");
		String string = fastDFSClient.uploadFile("C:/Users/YJF/Pictures/22.jpg");
		System.out.println(string);
	}
}
