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
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        DataInputStream dis = new DataInputStream(client.getInputStream());
        boolean isRunning = true;
        while (isRunning) {
            //2.客户端发送消息
            String msg = console.readLine();
            dos.writeUTF(msg);
            dos.flush();
            //3.获取返回的消息
            String datas = dis.readUTF();
            System.out.println("get:" + datas);
            if (datas.equals("exit")) {
                isRunning = false;
            }
        }


        dis.close();
        dos.close();
        client.close();
    }

}
