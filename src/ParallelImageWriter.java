import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.*;


public class ParallelImageWriter {
    private static final int NUM_JOBS = 10;
    private static final int NTHREADS = 10;
    public static void main(String[] args) throws Exception {

        BufferedImage[] biArray = new BufferedImage[NUM_JOBS];
        for (int i =0; i<NUM_JOBS;i++)
        {
            biArray[i] = generateImage();
        }
        long t1 = System.nanoTime();
        saveJpegInParallel(biArray);
        long t2 = System.nanoTime();
        System.out.println("Jpeg parallel saving finished in " + (t2-t1)*1e-6 + " milliseconds");

        t1 = System.nanoTime();
        saveJpegSerially(biArray);
        t2 = System.nanoTime();
        System.out.println("Jpeg serial saving finished in " + (t2-t1)*1e-6 + " milliseconds");

        t1 = System.nanoTime();
        saveMimInParallel(biArray);
        t2 = System.nanoTime();
        System.out.println("MIM parallel saving finished in " + (t2-t1)*1e-6 + " milliseconds");

        t1 = System.nanoTime();
        saveMimSerially(biArray);
        t2 = System.nanoTime();
        System.out.println("MIM serial saving finished in " + (t2-t1)*1e-6 + " milliseconds");


    }
    private static void saveMimSerially(BufferedImage[] biArray) {
        for (int i =0; i<NUM_JOBS;i++)
        {
            Runnable task = new MIMWriterThread(biArray[i], "D:\\Deepak\\ImgSer"+i+".mim");
            task.run();
        }

    }
    private static void saveMimInParallel(BufferedImage[] biArray) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        Queue<Future<ImageSaveResult>> futureQueue = new ConcurrentLinkedQueue<Future<ImageSaveResult>>();

        for (int i =0; i<NUM_JOBS;i++)
        {
            Runnable task = new MIMWriterThread(biArray[i], "D:\\Deepak\\ImgPar"+i+".mim");
            Future<ImageSaveResult> submittedTask = executor.submit(task, new ImageSaveResult());
            futureQueue.add(submittedTask);
        }

        executor.awaitTermination(1, TimeUnit.SECONDS);


    }
    private static void saveJpegSerially(BufferedImage[] biArray) {
        for (int i =0; i<NUM_JOBS;i++)
        {
            Runnable task = new ImageWriterThread(biArray[i], "D:\\Deepak\\ImgSer"+i+".jpeg");
            task.run();
        }

    }
    private static void saveJpegInParallel(BufferedImage[] biArray) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        Queue<Future<ImageSaveResult>> futureQueue = new ConcurrentLinkedQueue<Future<ImageSaveResult>>();
        for (int i =0; i<NUM_JOBS;i++)
        {
            Runnable task = new ImageWriterThread(biArray[i], "D:\\Deepak\\ImgPar"+i+".jpeg");
            Future<ImageSaveResult> submittedTask = executor.submit(task, new ImageSaveResult());
            futureQueue.add(submittedTask);
        }

        executor.awaitTermination(1, TimeUnit.SECONDS);

    }
    public static BufferedImage generateImage() {

        int x, y = 0;

        // image block size in pixels, 1 is 1px, use smaller values for

        // greater granularity

        int PIX_SIZE = 5;

        // image size in pixel blocks

        int X = 1000;

        int Y = 1000;

        BufferedImage bi = new BufferedImage(PIX_SIZE * X, PIX_SIZE * Y,

                BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g = (Graphics2D) bi.getGraphics();

        for (int i = 0; i < X; i++) {

            for (int j = 0; j < Y; j++) {

                x = i * PIX_SIZE;

                y = j * PIX_SIZE;

                // this is a writing condition, my choice here is purely random

                // just to generate some pattern

                // this condition

                if ((i * j) % 6 == 0) {

                    g.setColor(Color.GRAY);

                } else if ((i + j) % 5 == 0) {

                    g.setColor(Color.BLUE);

                } else {

                    g.setColor(Color.WHITE);

                }// end else

                // fil the rectangles with the pixel blocks in chosen color

                g.fillRect(y, x, PIX_SIZE, PIX_SIZE);

            }// end for j

        }// end for i

        g.dispose();

        return bi;

    }
}
