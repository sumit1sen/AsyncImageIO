import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MIMWriterThread implements RunnableFuture<ImageSaveResult> {

    private ImageSaveResult result;
    private boolean isDone;
    private BufferedImage bi;
    private String filename;

    public MIMWriterThread(BufferedImage bi, String filename)
    {
        this.bi = bi;
        this.filename = filename;
        isDone = false;
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public ImageSaveResult get() throws InterruptedException, ExecutionException {
        return result;
    }

    @Override
    public ImageSaveResult get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return result;
    }

    @Override
    public void run() {
        // save image here
        isDone=false;
        result = new ImageSaveResult();
        try {
            MIMCodec.encode(filename, bi);
        } catch (IOException e) {
            e.printStackTrace();
            result.e = e;
        }
        isDone = true;
    }
}
