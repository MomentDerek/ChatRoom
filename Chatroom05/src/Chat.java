import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;

/**
 * 在线聊天室：服务端
 * 实现私聊
 * 实现主动退出
 * 实现管理员主动关闭
 * todo:问题：管理员关闭时会有bug
 * @author Moment
 */
public class Chat {
    private static CopyOnWriteArrayList<Channel> allChannel = new CopyOnWriteArrayList<>();
    private static boolean isRunning = true;
    private static ServerSocket server;
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        System.out.println("-------Server--------");
        server = new ServerSocket(8888);
        new Thread(()->{
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while(isRunning){
                try {
                    if(console.readLine().equals("close the chat")){
                        close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //1.使用ServerSocket创建服务器
        while (isRunning) {
            //阻塞式等待连接
            Socket client = server.accept();
            Channel channel = new Channel(client);
            allChannel.add(channel);
            new Thread(channel).start();
        }
    }

    private static void close(){
        for (Channel channel:allChannel){
            allChannel.remove(channel);
            channel.send("System:the chat room is closed");
            channel.send("exit");
            Utils.close(channel.dis,channel.dos,channel.client);
            channel.isRunning = false;

        }
        System.out.println("the chat room is closing");
        isRunning = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("the chat room is closed");
            }
        },1500);
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
                    closeChannel();
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
                if (all == this){
                    continue;
                }
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
            if(msg.startsWith("@")){
                int idx = msg.indexOf(":");
                String targetName = msg.substring(1,idx);
                msg = msg.substring(idx+1);
                for(Channel target:allChannel){
                    if(target.name.equals(targetName)) {
                        target.send(this.name + ":" + msg);
                        send(this.name + "@"+targetName+":" + msg);
                        return;
                    }
                }
            }
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

        private void closeChannel(){
            send("exit");
            getLeaveNotice();
            release();
        }

        //释放资源
        private void release() {
            this.isRunning = false;
            System.out.println(name+" has left");
            allChannel.remove(this);
            Utils.close(dis,dos,client);
        }

    }

}
