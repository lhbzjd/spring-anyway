package ink.anyway.component.common.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import ink.anyway.component.common.Constants;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    protected static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    public static synchronized void syncMkdirs(File dic){
        if(dic!=null)
            dic.mkdirs();
    }

    /**
     * Get MD5 of a file (lower case)
     * @return empty string if I/O error when get MD5
     */
    public static String getFileHash(File file, Constants.HASH_ALGORITHM algorithm) {
        FileInputStream in = null;
        FileChannel ch = null;
        String res = null;
        try{
            in = new FileInputStream(file);
            ch = in.getChannel();
            ByteBuffer buffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            res = MD5(buffer, algorithm);
            ((MappedByteBuffer) buffer).force();
            Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
            if (cleaner != null) {
                cleaner.clean();
            }
        }catch (Exception e){
            logger.error("get file hash code error", e);
        }finally {
            try {
                ch.close();
            } catch (IOException e) {e.printStackTrace();}
            closeIO(in);
        }
        return res;
    }

    /**
     * 计算MD5校验
     * @param buffer
     * @return 空串，如果无法获得 MessageDigest实例
     */
    private static String MD5(ByteBuffer buffer, Constants.HASH_ALGORITHM algorithm) {
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.value);
            md.update(buffer);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>>,
                // 逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * convert dicom file to JEPG file
     *
     * @param srcDicomFile
     * @param destJPEGFile
     */
    public static void dicom2JPEG(File srcDicomFile, File destJPEGFile) {
        BufferedImage myJpegImage = null;
        Iterator<ImageReader> iterator = ImageIO
                .getImageReadersByFormatName("DICOM");
        while (iterator.hasNext()) {
            ImageReader imageReader = iterator.next();
            DicomImageReadParam dicomImageReadParam = (DicomImageReadParam) imageReader
                    .getDefaultReadParam();
            ImageInputStream iis = null;
            try {
                iis = ImageIO
                        .createImageInputStream(srcDicomFile);
                imageReader.setInput(iis, false);
                myJpegImage = imageReader.read(0, dicomImageReadParam);
                if (myJpegImage == null) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                closeIO(iis);
            }
            OutputStream outputStream = null;
            try {
                if(!destJPEGFile.getParentFile().exists()||!destJPEGFile.getParentFile().isDirectory())
                    destJPEGFile.getParentFile().mkdirs();

                if(!destJPEGFile.exists()||!destJPEGFile.isFile())
                    destJPEGFile.createNewFile();

                outputStream = new BufferedOutputStream(
                        new FileOutputStream(destJPEGFile));
                JPEGImageEncoder encoder = JPEGCodec
                        .createJPEGEncoder(outputStream);
                encoder.encode(myJpegImage);
                closeIO(outputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
                closeIO(outputStream);
            }
        }
    }

    public static void closeIO(Object obj){
        if(obj==null)
            return;

        if(obj instanceof Flushable){
            try{
                ((Flushable) obj).flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(obj instanceof Closeable){
            try{
                ((Closeable) obj).close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void renameTo(File from, File dest)
            throws IOException {
        dest.getParentFile().mkdirs();
        if (!from.renameTo(dest))
            throw new IOException("Failed to rename " + from + " to " + dest);
    }

    public static boolean zipCompressFile(File srcFile, File targetFile) {

        if(srcFile==null||!srcFile.exists()||!srcFile.isFile()){
            return false;
        }

        if(targetFile==null){
            return false;
        }

        OutputStream os = null;
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        InputStream is = null;

        try{
            if(!targetFile.exists()||!targetFile.isFile()){
                if(!targetFile.getParentFile().exists()||!targetFile.getParentFile().isDirectory()){
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
            }

            os = new FileOutputStream(targetFile);
            cos = new CheckedOutputStream(os, new CRC32());
            zos = new ZipOutputStream(cos);

            ZipEntry zipEntry = new ZipEntry(srcFile.getName());
            zos.putNextEntry(zipEntry);

            is=new FileInputStream(srcFile);

            int length = 0;
            byte[] b = new byte[8192];
            while ((length = is.read(b)) != -1) {
                zos.write(b, 0, length);
            }

            closeIO(is);
            closeIO(zos);
            closeIO(cos);
            closeIO(os);

            return true;
        }catch (Exception ex){
            logger.error("zip compress file error ...", ex);
            closeIO(is);
            closeIO(zos);
            closeIO(cos);
            closeIO(os);
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
//        AwsClient awsClient = AwsClient.builder().setAccessKey("VKH2MQ8987P15IGIOWGP").setSecretKey("T11431q3pJQdOPlVPnrrv7vPYtYDcSkOvZPYVtnQ").setEndpoint("http://172.16.140.2").build();
//
//        List<S3ObjectSummary> list = awsClient.listAllFiles("dicom", "02400165/2018/20181107/");

//        File f = new File("D:\\thumbnail\\fdsfsdfasdfasdfas.bz2");
//        if(!f.exists())
//            f.createNewFile();
//        awsClient.getObject("dicom", list.get(0).getKey(), f);

//        System.out.println(list.get(0).getETag());
//        FileInputStream is = new FileInputStream(f);
//        System.out.println("19638d452820bf0f14e65b0a779725f9");
//        System.out.println(getFileHash(is, Constants.HASH_ALGORITHM.MD5));

//        File src = new File("D:\\thumbnail\\dicom_04210003_S_89458\\04210003_KC_IPEX_P001\\20190110\\aa3ec0ec-146a-11e9-9c5d-f48e38ff6ef4_04210003_89458_274725_6467134");
//
//        File target = new File("D:\\thumbnail\\dicom_04210003_S_89458\\04210003_KC_IPEX_P001\\20190110\\sdafasdfsad.zip");
//
//        zipCompressFile(src, target);
//
//        System.out.println(getFileHash(target, Constants.HASH_ALGORITHM.MD5));
//
//        target.delete();
    }

}
