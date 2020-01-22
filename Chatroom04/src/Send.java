import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @ClassName: Send
 * @Description: 发送线程
 * @Author: Moment
 * @Date: 2020/1/22 10:40
 */
public class Send implements Runnable {
    private String name = "";
    private Socket client;
    private BufferedReader console;
    private DataOutputStream dos;
    private boolean isRunning = true;

    @Override
    public void run() {
        while (isRunning) {
            String msg = getStrFromConsole();
            if(!msg.equals("")){
                send(msg);
            }
            else if (msg.equals("exit")){
                release();
            }
        }
    }

    public Send(Socket client,String name) {
        this.client = client;
        this.name = name;
        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            dos = new DataOutputStream(this.client.getOutputStream());
            dos.writeUTF(this.name);
        } catch (IOException e) {
            System.out.println("------struct is error--------");
            release();
        }
    }

    private void send(String msg){
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            System.out.println("--------send is error-------");
            release();
        }
    }

    /**
     * 从控制台获取消息
     */
    private String getStrFromConsole(){
        String msg = "";
        try {
            msg =  console.readLine();
        } catch (IOException e) {
            System.out.println("--------get from console is error-------");
            release();
        }
        return msg;
    }

    private void release() {
        this.isRunning = false;
        Utils.close(dos, console,client);
    }

}
