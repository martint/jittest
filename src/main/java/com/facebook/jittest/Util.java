package com.facebook.jittest;

public class Util
{
    private static volatile int blackhole;

    public static void consume(int value)
    {
        blackhole = value;
    }

    public static boolean belowLimit(int value)
    {
        return value < 1_000_000;
    }

    public static int add(int a, int b)
    {
        return a + b;
    }

    public static int pow10(int x)
    {
        int result = 1;
        for (int i = 0; i < 10; i++) {
            result *= x;
        }

        return result;
    }
}
