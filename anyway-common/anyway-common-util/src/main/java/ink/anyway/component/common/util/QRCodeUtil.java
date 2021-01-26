package ink.anyway.component.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;

public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    public static byte[] createImage(String content, URL imgURL,
                                     boolean needCompress, int qrcodeSize, int logoSize) throws Exception {

        if(qrcodeSize<=0)
            qrcodeSize = QRCODE_SIZE;

        if(logoSize<=0)
            logoSize = WIDTH;

        if(logoSize>=qrcodeSize)
            logoSize = qrcodeSize/2;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, qrcodeSize, qrcodeSize, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgURL!=null&&!"".equals(imgURL)) {
            Image src = ImageIO.read(imgURL);
            if(src!=null){
                // 插入图片
                QRCodeUtil.insertImage(image, src, qrcodeSize, needCompress, logoSize, logoSize);
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, os);
        return os.toByteArray();
    }

    public static byte[] createImage(String content, URL imgURL,
                                            boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgURL!=null&&!"".equals(imgURL)) {
            Image src = ImageIO.read(imgURL);
            if(src!=null){
                // 插入图片
                QRCodeUtil.insertImage(image, src, QRCODE_SIZE, needCompress, WIDTH, HEIGHT);
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, os);
        return os.toByteArray();
    }

    /**
     * 插入LOGO
     *
     * @param source
     *            二维码图片
     * @param src
     *            LOGO图片地址
     * @param needCompress
     *            是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, Image src, int qrcodeSize,
                                    boolean needCompress, int compressMaxWidth, int compressMaxHeight) throws Exception {
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > compressMaxWidth) {
                width = compressMaxWidth;
            }
            if (height > compressMaxHeight) {
                height = compressMaxHeight;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (qrcodeSize - width) / 2;
        int y = (qrcodeSize - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

}
