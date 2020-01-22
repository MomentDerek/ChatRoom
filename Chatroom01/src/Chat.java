import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 在线聊天室：服务端
 * @author Moment
 *
 */
public class Chat {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception{
        System.out.println("-------Server--------");
        //1.使用ServerSocket创建服务器
        ServerSocket server = new ServerSocket(8888);
        Socket client = server.accept();
        DataInputStream dis = new DataInputStream(client.getInputStream());
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        boolean isRunning = true;
        while(isRunning) {
            //2.阻塞式等待accept
            System.out.println("connet is ok");
            //3.接收消息
            String datas = dis.readUTF();
            //4.返回消息
            dos.writeUTF(datas);
            dos.flush();
            if(datas.equals("exit")) {
                isRunning = false;
            }
        }

        dis.close();
        dos.close();
        client.close();
    }

}
