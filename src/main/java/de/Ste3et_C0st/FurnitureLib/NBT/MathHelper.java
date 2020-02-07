package de.Ste3et_C0st.FurnitureLib.NBT;

import java.util.Random;

public class MathHelper {

    private static float[] a = new float[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            a[i] = (float) Math.sin(i * 3.141592653589793D * 2.0D / 65536.0D);
        }
    }

    public static double getHighestDouble(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return Math.max(d0, d1);
    }

    public static double a(double d0, double d1, double d2) {
        return d0 < d1 ? d1 : Math.min(d0, d2);
    }

    public static float a(float f, float f1, float f2) {
        return f < f1 ? f1 : Math.min(f, f2);
    }

    public static int a(int i) {
        return i >= 0 ? i : -i;
    }

    public static int a(int i, int j, int k) {
        return i < j ? j : Math.min(i, k);
    }

    public static double a(long[] along) {
        long i = 0L;
		int j = along.length;

		for (long l : along) {
			i += l;
		}

        return (double) i / (double) along.length;
    }

    public static double a(Random random, double d0, double d1) {
        return d0 >= d1 ? d0 : random.nextDouble() * (d1 - d0) + d0;
    }

    public static float a(Random random, float f, float f1) {
        return f >= f1 ? f : random.nextFloat() * (f1 - f) + f;
    }

    public static double a(String s, double d0) {
        double d1 = d0;

        try {
            d1 = Double.parseDouble(s);
        } catch (Throwable throwable) {
		}

        return d1;
    }

    public static double a(String s, double d0, double d1) {
        double d2 = d0;

        try {
            d2 = Double.parseDouble(s);
        } catch (Throwable throwable) {
		}

        if (d2 < d1) {
            d2 = d1;
        }

        return d2;
    }

    public static int parseInteger(String raw, int i) {
        int j = i;
        try {
            j = Integer.parseInt(raw);
        } catch (Throwable throwable) {
		}

        return j;
    }

    public static int parseInteger(String s, int default_value, int min) {
        int value = default_value;

        try {
            value = Integer.parseInt(s);
        } catch (Throwable throwable) {
        }

        if (value < min) {
            value = min;
        }

        return value;
    }

    public static float abs(float f) {
        return f >= 0.0F ? f : -f;
    }

    public static double b(double d0, double d1, double d2) {
        return d2 < 0.0D ? d0 : d2 > 1.0D ? d1 : d0 + (d1 - d0) * d2;
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }

    public static float cos(float f) {
        return a[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static long d(double d0) {
        long i = (long) d0;

        return d0 < i ? i - 1L : i;
    }

    public static int d(float f) {
        int i = (int) f;

        return f < i ? i - 1 : i;
    }

    public static int f(double d0) {
        int i = (int) d0;

        return d0 > i ? i + 1 : i;
    }

    public static int f(float f) {
        int i = (int) f;

        return f > i ? i + 1 : i;
    }

    public static int floor(double d0) {
        int i = (int) d0;

        return d0 < i ? i - 1 : i;
    }

    public static double g(double d0) {
        d0 %= 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        if (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return d0;
    }

    public static float g(float f) {
        f %= 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static float sin(float f) {
        return a[(int) (f * 10430.378F) & '\uffff'];
    }

    public static float sqrt(double d0) {
        return (float) Math.sqrt(d0);
    }
}