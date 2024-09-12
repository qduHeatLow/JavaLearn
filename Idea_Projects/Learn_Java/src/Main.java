import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main{
    static Queue<Object> queue = new LinkedList<>();
    public static void main(String[] args) throws InterruptedException {
        new Thread(Main::add,"生产者1").start();
        new Thread(Main::add,"生产者2").start();
        new Thread(Main::take,"消费者1").start();
        new Thread(Main::take,"消费者2").start();
    }

    private static void add(){
        while (true){
            synchronized (queue){
                try {
                    Thread.sleep(300);
                    System.out.println(Thread.currentThread().getName()+"出餐了");
                    queue.offer(new Object());
                    queue.notifyAll();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void take(){
        while (true){
            try{
                synchronized (queue){
                    while (queue.isEmpty()) queue.wait();
                    queue.poll();
                    System.out.println(Thread.currentThread().getName()+"吃饭了");
                    Thread.sleep(400);

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}