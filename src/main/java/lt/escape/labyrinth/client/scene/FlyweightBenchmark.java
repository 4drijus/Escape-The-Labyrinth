package lt.escape.labyrinth.client.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class FlyweightBenchmark {

    // keeps the measured objects reachable during memory measurement
    private static volatile List<SceneDecoration> retained;

    private static List<SceneDecoration> createDecorations(
            int count,
            boolean shared
    ) {
        DecorationFlyweightFactory factory =
                new DecorationFlyweightFactory();

        List<SceneDecoration> decorations = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            DecorationFlyweight flyweight;

            if (shared) {
                flyweight = factory.getRock();
            } else {
                flyweight = new DecorationFlyweight(
                        DecorationFlyweightFactory.createRockImage()
                );
            }

            decorations.add(new DecorationSprite(
                    flyweight,
                    (i % 100) * 10,
                    (i / 100) * 10,
                    40,
                    24
            ));
        }

        return decorations;
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void settleMemory() throws InterruptedException {
        // GC requests are best-effort; heap measurements are approximate
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(100);
        }
    }

    private static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2
                || (!args[0].equals("shared")
                && !args[0].equals("unshared"))) {
            System.out.println(
                    "Usage: FlyweightBenchmark shared|unshared count"
            );
            return;
        }

        boolean shared = args[0].equals("shared");
        int count = Integer.parseInt(args[1]);

        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        // warm up both paths before collecting results
        for (int i = 0; i < 5; i++) {
            retained = createDecorations(1_000, false);
            retained = createDecorations(1_000, true);
        }

        retained = null;
        settleMemory();

        int rounds = 5;
        double[] times = new double[rounds];
        double[] memory = new double[rounds];

        for (int round = 0; round < rounds; round++) {
            retained = null;
            settleMemory();

            long memoryBefore = usedMemory();

            long start = System.nanoTime();
            retained = createDecorations(count, shared);
            long elapsed = System.nanoTime() - start;

            settleMemory();
            long memoryAfter = usedMemory();

            times[round] = elapsed / 1_000_000.0;
            memory[round] =
                    (memoryAfter - memoryBefore) / (1024.0 * 1024.0);

            System.out.printf(
                    Locale.ROOT,
                    "Round %d: creation=%.3f ms, heap delta=%.3f MiB%n",
                    round + 1,
                    times[round],
                    memory[round]
            );
        }

        System.out.printf(
                Locale.ROOT,
                "%nMode=%s, decorations=%d%n"
                        + "Median creation time: %.3f ms%n"
                        + "Median heap delta: %.3f MiB%n",
                args[0],
                retained.size(),
                median(times),
                median(memory)
        );
    }
}