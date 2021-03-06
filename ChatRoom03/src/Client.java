import java.io.*;
import java.net.Socket;

/**
 * 在线聊天室：客户端
 *
 * @author Moment
 */
public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("----Client-----");
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        //1.使用Socket建立连接，连接服务的地址和端口
        Socket client = new Socket("localhost", 8888);
        //2.客户端接受信息线程
        new Thread(new Receive(client)).start();
        //3.客户端发送消息线程
        new Thread(new Send(client)).start();
    }


}
