import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clock class responsible for maintaining and providing
 * the current time and date.
 */
class Clock {
    // Shared variable holding the current time
    private volatile LocalDateTime currentTime;

    // Formatter for printing time and date
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

    public Clock() {
        this.currentTime = LocalDateTime.now();
    }

    /**
     * Updates the currentTime field to the system's current time.
     */
    public void updateTime() {
        currentTime = LocalDateTime.now();
    }

    /**
     * Returns the formatted current time string.
     * 
     * @return formatted time and date
     */
    public String getFormattedTime() {
        return currentTime.format(FORMATTER);
    }
}

/**
 * Thread that updates the Clock's time in the background.
 */
class TimeUpdater extends Thread {
    private final Clock clock;
    private volatile boolean running = true;

    public TimeUpdater(Clock clock) {
        this.clock = clock;
        setName("TimeUpdater");
        setPriority(Thread.MIN_PRIORITY); // Lower priority
    }

    @Override
    public void run() {
        try {
            while (running) {
                clock.updateTime();
                // Update as frequently as reasonable (e.g., every 200 ms)
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            // Graceful shutdown on interruption
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }
}

/**
 * Thread that prints the Clock's time to the console.
 */
class TimePrinter extends Thread {
    private final Clock clock;
    private volatile boolean running = true;

    public TimePrinter(Clock clock) {
        this.clock = clock;
        setName("TimePrinter");
        setPriority(Thread.MAX_PRIORITY); // Higher priority
    }

    @Override
    public void run() {
        try {
            while (running) {
                System.out.println(clock.getFormattedTime());
                // Print once per second
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            // Graceful shutdown on interruption
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }
}

/**
 * Main class to launch the simple clock application.
 */
public class SimpleClockApp {
    public static void main(String[] args) {
        Clock clock = new Clock();
        TimeUpdater updater = new TimeUpdater(clock);
        TimePrinter printer = new TimePrinter(clock);

        // Start both threads
        updater.start();
        printer.start();

        // Let the clock run for 10 seconds as a demonstration
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Shutdown threads cleanly
        updater.shutdown();
        printer.shutdown();
        
        System.out.println("Clock application has terminated.");
    }
}

