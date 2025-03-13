package com.hxnry.autotyper.away;

import lombok.Setter;

public abstract class WindowInteractions extends Thread {

    @Setter
    int loop = 100;

    @Override
    public void run() {
        while(loop > -1) {
            execute();
            try {
                Thread.sleep(loop);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Window interaction thread shut down!");
    }

    public abstract void execute();


}
