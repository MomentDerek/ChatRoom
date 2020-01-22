import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmConstraints;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 在线聊天室：服务端
 * 加入容器实现群聊
 * @author Moment
 */
public class Chat {
    private static CopyOnWriteArrayList<Channel> allChannel = new CopyOnWriteArrayList<>();
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        System.out.println("-------Server--------");
        //1.使用ServerSocket创建服务器
        ServerSocket server = new ServerSocket(8888);
        while (true) {
            //阻塞式等待连接
            Socket client = server.accept();
            Channel channel = new Channel(client);
            allChannel.add(channel);
            new Thread(channel).start();
        }
    }

    //一个客户代表一个channel
    static class Channel implements Runnable {
        private String  name = "";
        private Socket client;
        private DataOutputStream dos;
        private DataInputStream dis;
        private boolean isRunning = true;

        @Override
        public void run() {
            getJoinNotice();
            while (isRunning) {
                String msg = receive();
                if (msg.equals("exit")){
                    release();
                } else if (!msg.equals("")) {
                    sendAll(msg);
                }
            }
        }

        public Channel(Socket client) {
            this.client = client;
            try {
                dos = new DataOutputStream(client.getOutputStream());
                dis = new DataInputStream(client.getInputStream());
                this.name = receive();
                System.out.println(name+" join the room");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("----client struct error----");
                release();
            }
        }

        private void getJoinNotice(){
            for(Channel all:allChannel){
                all.send(this.name+" join the room");
            }
        }
        private void getLeaveNotice(){
            for(Channel all:allChannel){
                all.send(this.name+" has left");
            }
        }

        //发送消息
        private void send(String msg) {
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("----send error----");
                release();
            }
        }

        //全体广播
        private void sendAll(String msg) {
            for(Channel all:allChannel){
                all.send(this.name+":"+msg);
            }
        }

        //发送消息给其他人
        private void sendOthers(String msg) {
            for(Channel other:allChannel){
                if (other == this){
                    continue;
                }
                other.send(this.name+":"+msg);
            }
        }

        //接受消息
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("----receive error----");
                release();
            }
            return msg;
        }

        //释放资源
        private void release() {
            this.isRunning = false;
            getLeaveNotice();
            send("exit");
            System.out.println(name+" has left");
            allChannel.remove(this);
            Utils.close(dis,dos,client);
        }

    }

}
