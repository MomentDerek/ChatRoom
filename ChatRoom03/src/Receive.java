

import java.io.DataInputStream;
        import java.io.IOException;
        import java.net.Socket;

/**
 * @ClassName: Receive
 * @Description: 接收线程
 * @Author: Moment
 * @Date: 2020/1/22 10:38
 */
public class Receive implements Runnable{
    private Socket client;
    private DataInputStream dis;
    private boolean isRunning = true;


    public Receive(Socket client) {
        this.client = client;
        try {
            dis = new DataInputStream(this.client.getInputStream());
        } catch (IOException e) {
            System.out.println("----struct error----");
            release();
        }
    }

    private String receive(){
        String datas = "";
        try {
            datas = dis.readUTF();
        } catch (IOException e) {
            System.out.println("----receive read error----");
        }
        System.out.println("get:" + datas);
        return datas;
    }

    private void release(){
        Utils.close(dis,client);
    }

    @Override
    public void run() {
        while (isRunning){
            String msg = receive();
            if(msg.equals("exit")){
                System.out.println("----client close----");
                release();
            }
        }
    }
}
