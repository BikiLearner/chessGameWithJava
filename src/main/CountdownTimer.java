package main;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * CountdownTimer: countdown in seconds, restartable, thread-safe.
 * - call start() to start (no-op if already running)
 * - call reset(seconds) to set timeLeft immediately (keeps running state)
 * - call stop() to stop (can be restarted)
 * - addTickListener to observe every second (runs on scheduler thread; UI should use SwingUtilities.invokeLater)
 * - addFinishListener to be notified when reaches 0
 */
public class CountdownTimer {
    private final int initialSeconds;
    private final AtomicInteger timeLeft;
    private final List<Consumer<Integer>> tickListeners = new CopyOnWriteArrayList<>();
    private final List<Runnable> finishListeners = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "countdown-timer");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> future;
    private final Object lock = new Object();

    public CountdownTimer(int seconds) {
        if (seconds < 0) throw new IllegalArgumentException("seconds >= 0");
        this.initialSeconds = seconds;
        this.timeLeft = new AtomicInteger(seconds);
    }

    public void start() {
        synchronized (lock) {
            if (future != null && !future.isDone() && !future.isCancelled()) return;
            // schedule task that runs every 1 second
            future = executor.scheduleAtFixedRate(() -> tick(), 0, 1, TimeUnit.SECONDS);
        }
        // notify immediate value
        notifyTick(timeLeft.get());
    }

    public void stop() {
        synchronized (lock) {
            if (future != null) {
                future.cancel(false);
                future = null;
            }
        }
    }

    /** Reset the remaining time (keeps running if it was running). If seconds == 0 triggers finish listeners. */
    public void reset(int seconds) {
        if (seconds < 0) seconds = 0;
        timeLeft.set(seconds);
        notifyTick(seconds);
        if (seconds == 0) notifyFinish();
    }

    public int getTimeLeft() {
        return timeLeft.get();
    }

    public void addTickListener(Consumer<Integer> l) { tickListeners.add(l); }
    public void removeTickListener(Consumer<Integer> l) { tickListeners.remove(l); }
    public void addFinishListener(Runnable r) { finishListeners.add(r); }
    public void removeFinishListener(Runnable r) { finishListeners.remove(r); }

    private void tick() {
        int left = timeLeft.get();
        if (left <= 0) {
            // already finished
            notifyTick(0);
            notifyFinish();
            stop();
            return;
        }
        int newVal = timeLeft.decrementAndGet();
        notifyTick(newVal);
        if (newVal <= 0) {
            notifyFinish();
            stop();
        }
    }

    private void notifyTick(int sec) {
        for (Consumer<Integer> c : tickListeners) {
            try { c.accept(sec); } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    private void notifyFinish() {
        for (Runnable r : finishListeners) {
            try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
        }
    }

    /** Completely shut down executor when application exits. Call this on app close. */
    public void shutdown() {
        stop();
        executor.shutdownNow();
    }
}
