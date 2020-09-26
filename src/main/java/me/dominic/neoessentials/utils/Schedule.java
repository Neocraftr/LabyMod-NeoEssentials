package me.dominic.neoessentials.utils;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class Schedule {

    public static ArrayList<Schedule> schedules = new ArrayList<>();

    private long nextUpdate = -1;
    private long interval;
    private boolean repeat;
    private String message;

    public static void updateSchedules() {
        getSchedules().forEach(schedule -> {
            schedule.update();
        });
    }

    public Schedule(long interval, boolean repeat, String message) {
        setInterval(interval);
        setRepeat(repeat);
        setMessage(message);
        getSchedules().add(this);
    }

    private void update() {
        if(getNextUpdate() != -1 && System.currentTimeMillis() >= nextUpdate) {
            if(isRepeat()) {
                setNextUpdate(System.currentTimeMillis() + getInterval());
            } else {
                stop();
            }
            getMC().thePlayer.sendChatMessage(getMessage());
        }
    }

    public void stop() {
        setNextUpdate(-1);
    }

    public void destroy() {
        getSchedules().remove(this);
    }

    public void start() {
        setNextUpdate(System.currentTimeMillis() + getInterval());
    }

    public static ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public long getInterval() {
        return interval;
    }
    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }
    public void setNextUpdate(long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public boolean isRepeat() {
        return repeat;
    }
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRunning() {
        return getNextUpdate() != -1;
    }

    private Minecraft getMC() {
        return Minecraft.getMinecraft();
    }
}
