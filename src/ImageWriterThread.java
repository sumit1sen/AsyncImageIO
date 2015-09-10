import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ImageWriterThread implements RunnableFuture<ImageSaveResult>{

    private ImageSaveResult result;
    private boolean isDone;
    private BufferedImage bi;
    private String filename;

    public ImageWriterThread(BufferedImage bi, String filename)
    {
        this.bi = bi;
        this.filename = filename;
        isDone = false;
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return isDone;
    }

    @Override
    public ImageSaveResult get() throws InterruptedException, ExecutionException {
        // TODO Auto-generated method stub
        return result;
    }

    @Override
    public ImageSaveResult get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        // TODO Auto-generated method stub
        return result;
    }

    @Override
    public void run() {
        // save image here
        isDone=false;
        try {
            ImageIO.write(bi, "jpeg", new File(filename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // add exception to returned object
            e.printStackTrace();
        }
        isDone = true;
    }

}
