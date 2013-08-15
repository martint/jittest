package com.facebook.jittest;

import io.airlift.stats.DecayCounter;
import io.airlift.stats.ExponentialDecay;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args)
    {
        final DecayCounter counter = new DecayCounter(ExponentialDecay.seconds(5));

        Thread stats = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                MemoryPoolMXBean codeCache = null;
                MemoryPoolMXBean permGen = null;
                for (MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
                    if (bean.getName().equals("Code Cache")) {
                        codeCache = bean;
                    }
                    if (bean.getName().equals("PS Perm Gen") || bean.getName().equals("CMS Perm Gen")) {
                        permGen = bean;
                    }
                    if (bean.getName().equals("Metaspace")) { // java8 doesn't have a perm gen
                        permGen = bean;
                    }
                }

                System.out.println("Timestamp\tCompilation Time\tCode Cache Size\tPerm Gen/Metaspace Size\tInvocations/s");
                while (!Thread.currentThread().isInterrupted()) {
                    if (counter.getCount() > 0) {
                        System.out.println(String.format("%d\t%d\t%d\t%d\t%.2f",
                                System.currentTimeMillis(),
                                ManagementFactory.getCompilationMXBean().getTotalCompilationTime(),
                                codeCache.getUsage().getUsed(),
                                permGen.getUsage().getUsed(),
                                counter.getRate()
                        ));
                    }
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        stats.start();

        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                1,
                TimeUnit.DAYS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        final Compiler compiler = new Compiler();
        for (int i = 0; i < 1_000_000; i++) {
            // run a bunch in parallel to fill up the code cache faster
            executor.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    Runnable instance = compiler.compile();
                    instance.run();
                    counter.add(1);
                }
            });
        }

        stats.interrupt();
        executor.shutdownNow();
    }
}
