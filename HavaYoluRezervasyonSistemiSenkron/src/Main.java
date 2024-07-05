import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ReservationSystem {
    private static final int SEAT_COUNT = 1;
    private boolean[] seats = new boolean[SEAT_COUNT];
    private Lock lock = new ReentrantLock();

    public void makeReservation(int writerId) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        lock.lock();
        try {
            if (!seats[0]) {
                seats[0] = true;
                System.out.println(timeStamp + " - Writer " + writerId + " reserved the seat.");
            } else {
                System.out.println(timeStamp + " - Writer " + writerId + " failed to reserve the seat.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void queryReservation(int readerId) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        lock.lock();
        try {
            System.out.println(timeStamp + " - Reader " + readerId + " checked the seat: " + (seats[0] ? "reserved" : "free"));
        } finally {
            lock.unlock();
        }
    }
}

class Writer implements Runnable {
    private ReservationSystem system;
    private int writerId;

    public Writer(ReservationSystem system, int writerId) {
        this.system = system;
        this.writerId = writerId;
    }

    @Override
    public void run() {
        system.makeReservation(writerId);
    }
}

class Reader implements Runnable {
    private ReservationSystem system;
    private int readerId;

    public Reader(ReservationSystem system, int readerId) {
        this.system = system;
        this.readerId = readerId;
    }

    @Override
    public void run() {
        system.queryReservation(readerId);
    }
}

public class Main {
    public static void main(String[] args) {
        ReservationSystem system = new ReservationSystem();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        executor.submit(new Writer(system, 1));
        executor.submit(new Reader(system, 1));
        executor.submit(new Writer(system, 2));
        executor.submit(new Reader(system, 2));
        executor.submit(new Writer(system, 3));

        executor.shutdown();
    }
}
