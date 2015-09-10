import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.*;


public class MIMCodec
{

    // Number of Bits per pixel
    // TODO: REMOVE UNUSED private final static int BITS_PER_PIXEL_SIZE = 16;
    // TODO: REMOVE UNUSED private static int wordCounter = 0;
    private static final int MIM_LIMIT = 65535;


    /**
     * This method writes gray scale image to MIM file.
     * @param filePath This is a absolute path of file where image is to be saved.
     * @param image The image that is to be saved.
     */
    public static void encode(String filePath, BufferedImage image) throws FileNotFoundException, IOException
    {
        encode(filePath, image, true);
    }

    /*
     * This method writes gray scale image to MIM file. Based on the flag
     * (useCopyOfRasterDataToWrite) this method would either create a copy of raster data before
     * writing the raster data to the disc or it would directly use the raster data to write the
     * image to the disc. Its advisable not to use this method with the useCopyOfRasterDataToWrite
     * flag set to true and modify the image while writing. This can end up in undesired result.
     * 
     * @param filePath - This is a absolute path of file where image is to be saved.
     * @param image - The image that is to be saved.
     * @param useCopyOfRasterDataToWrite - Flag to decide whether to use raster data in memory or
     * create a copy of it and use it for writing.
     */
    public static void encode(String filePath, BufferedImage image,
                              boolean useCopyOfRasterDataToWrite) throws FileNotFoundException,IOException
    {

        // create a data input stream
        DataOutputStream dsInfo = null;
        try
        {
            int height = image.getHeight();
            int width = image.getWidth();


            dsInfo = new DataOutputStream(new FileOutputStream(filePath));
            writeShort(dsInfo, height);
            writeShort(dsInfo, width);

            Raster raster = image.getRaster();

            if(useCopyOfRasterDataToWrite)
            {
                raster = image.getData();
            }

            DataBuffer dataBuffer = raster.getDataBuffer();

            byte[] cacheBuffer = new byte[width * 2];
            byte byte0;
            byte byte1;
            int value;
            int index = 0;

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    value = dataBuffer.getElem(x + (y*width));
                    byte0 = (byte)( value & 0x000000ff);
                    byte1 = (byte)((value & 0x0000ff00) >> 8);
                    cacheBuffer[index] = byte0;
                    cacheBuffer[index + 1] = byte1;
                    index += 2;
                }
                index = 0;
                dsInfo.write(cacheBuffer);
            }
        }
        finally
        {
            if(dsInfo != null)
                dsInfo.close();
        }
    }


    /**
     * This method reads gray scale image from MIM file.
     * @param filePath This is a absolute path of file from where gray scale image
     * is to be read.
     * @return CougarGrayScaleImage This is actual image.
     */
//    public static CougarGrayScaleImage decode(String filePath) throws Exception
//    {
//        ParamUtil.verifyParamNotNull(filePath, "filePath", MIMCodec.class, "decode");
//
//        BufferedImage  bufferedImage  = decodeForBufferedImage(filePath);
//        CougarGrayScaleImage cougarGrayScaleImage = null;
//        int bitsPerPixel = bufferedImage.getColorModel().getPixelSize();
//
//        if(bitsPerPixel > 8)
//            cougarGrayScaleImage = (CougarGrayScaleImageShort)
//                              (CougarGrayScaleImageShort.create(bufferedImage));
//        else
//            cougarGrayScaleImage = (CougarGrayScaleImageByte)
//                               (CougarGrayScaleImageByte.create(bufferedImage));
//        return cougarGrayScaleImage;
//    }

    /**
     * This method reads gray scale image from MIM file.
     * @param filePath This is a absolute path of file from where gray scale image
     * is to be read.
     * @return BufferedImage This is actual image.
     */
    public static BufferedImage decodeForBufferedImage(String filePath) throws Exception
    {
        // create a data input stream
        DataInputStream dsInfo = new DataInputStream(new FileInputStream(filePath));
        BufferedImage bi = null;
        try
        {
            // TODO: REMOVE UNUSED wordCounter = 0;
            int height = readShort(dsInfo);
            int width = readShort(dsInfo);
            Dimension imageSize = new Dimension(width, height);
            bi = new BufferedImage(imageSize.width, imageSize.height,
                    BufferedImage.TYPE_USHORT_GRAY);
            Raster raster = bi.getRaster();
            DataBuffer dataBuffer = raster.getDataBuffer();

            byte[] cachedData = new byte[width  * 2];
            int index = 0;
            short byte0;
            short byte1;
            int grayValue;

            for (int y = 0; y < height; y++)
            {
                dsInfo.read(cachedData, 0, cachedData.length);
                index = 0;

                for (int x = 0; x < width; x++)
                {
                    byte0 = (short)(cachedData[index] & 0x00ff);
                    byte1 = (short)((cachedData[index+1] & 0x00ff) << 8);
                    grayValue = ( byte1  | byte0);
                    dataBuffer.setElem(x + (y*width), grayValue);
                    index += 2;
                }
            }
            bi.setData(raster);
        }
        finally
        {
            if(dsInfo!=null)
                dsInfo.close();
        }
        return bi;
    }

    private static int readShort(DataInputStream din)
            throws IOException
    {
        int lower = din.readUnsignedByte() & 0x00ff;
        int upper = din.readUnsignedByte() & 0x00ff;
        int data = (( upper << 8 ) | lower);

        return data;
    }

    private static void writeShort(DataOutputStream dout, int value)
            throws IOException
    {
        int finalLSB = (value & 0xff00) >> 8;
        int finalMSB = (value & 0xff) << 8;
        int finalValue = finalMSB | finalLSB;
        dout.writeShort(finalValue);
    }

}
