import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * 在线聊天室：服务端
 *
 * @author Moment
 */
public class Chat {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        System.out.println("-------Server--------");
        //1.使用ServerSocket创建服务器
        ServerSocket server = new ServerSocket(8888);
        while (true) {
            Socket client = server.accept();
            System.out.println("connet is ok");
            new Thread(new Channel(client)).start();
        }
    }

    //一个客户代表一个channel
    static class Channel implements Runnable {
        private Socket client;
        private DataOutputStream dos;
        private DataInputStream dis;
        private boolean isRunning = true;

        public Channel(Socket client) {
            this.client = client;
            try {
                dos = new DataOutputStream(client.getOutputStream());
                dis = new DataInputStream(client.getInputStream());
            } catch (IOException e) {
                System.out.println("----client struct error----");
                release();
            }
        }

        //发送消息
        private void send(String msg) {
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                System.out.println("----send error----");
                release();
            }
        }

        //接受消息
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
                if (msg.equals("exit"))
                    isRunning = false;
            } catch (IOException e) {
                System.out.println("----receive error----");
                release();
            }
            return msg;
        }

        //释放资源
        private void release() {
            this.isRunning = false;
            Utils.close();
        }

        @Override
        public void run() {
            while (isRunning) {
                String msg = receive();
                if (!msg.equals("")) {
                    send(msg);
                }
            }
        }
    }

}
