package ink.anyway.component.common.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.*;
import java.security.Key;

public class EncDecUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncDecUtil.class);

    /**
     * 加密
     * @param content
     * @param output
     * @param encryptKeyIs
     * @throws Exception
     */
    public static void encryptRSA(InputStream content, OutputStream output, InputStream encryptKeyIs) throws Exception {
        ObjectInputStream oisKey = new ObjectInputStream(encryptKeyIs);
        Key encryptKey = (Key) oisKey.readObject();
        oisKey.close();
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, encryptKey);

        int length;
        byte[] b = new byte[112];
        while ((length = content.read(b)) != -1) {
            output.write(encryptCipher.doFinal(ArrayUtils.subarray(b, 0, length)));
            output.flush();
        }
    }

    /**
     * 加密
     * @param content
     * @param output
     * @param encryptKeyIs
     * @throws Exception
     */
    public static byte[] encryptRSA(byte[] content, InputStream encryptKeyIs) throws Exception {
        ObjectInputStream oisKey = new ObjectInputStream(encryptKeyIs);
        Key encryptKey = (Key) oisKey.readObject();
        oisKey.close();
        return encryptRSA(content, encryptKey);
    }

    public static byte[] encryptRSA(byte[] content, Key encryptKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, encryptKey);

        byte[] b = new byte[0];
        for(int start=0, end = 0;end<content.length;start+=112){
            end = (start+112)<=content.length?(start+112):content.length;
            b = ArrayUtils.addAll(b, encryptCipher.doFinal(ArrayUtils.subarray(content, start, end)));
        }
        return b;
    }

    /**
     * 解密
     * @param content
     * @param output
     * @param decryptKeyIs
     * @throws Exception
     */
    public static void decryptRSA(InputStream content, OutputStream output, InputStream decryptKeyIs) throws Exception {
        ObjectInputStream oisKey = new ObjectInputStream(decryptKeyIs);
        Key decryptKey = (Key) oisKey.readObject();
        oisKey.close();
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, decryptKey);

        int length;
        byte[] b = new byte[128];
        while ((length = content.read(b)) != -1) {
            output.write(decryptCipher.doFinal(ArrayUtils.subarray(b, 0, length)));
            output.flush();
        }
    }

    /**
     * 解密
     * @param content
     * @param output
     * @param decryptKeyIs
     * @throws Exception
     */
    public static byte[] decryptRSA(byte[] content, InputStream decryptKeyIs) throws Exception {
        ObjectInputStream oisKey = new ObjectInputStream(decryptKeyIs);
        Key decryptKey = (Key) oisKey.readObject();
        oisKey.close();
        return decryptRSA(content, decryptKey);
    }

    public static byte[] decryptRSA(byte[] content, Key decryptKey) throws Exception {
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, decryptKey);

        byte[] b = new byte[0];
        int end = 0;
        for(int start=0;end<content.length;start+=128){
            end = (start+128)<=content.length?(start+128):content.length;
            b = ArrayUtils.addAll(b, decryptCipher.doFinal(ArrayUtils.subarray(content, start, end)));
        }
        return b;
    }

    public static String encodeBase64(byte[] input){
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(input);
    }

    public static byte[] decodeBase64(String input) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(input);
        if(bytes!=null){
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
        }
        return bytes;
    }


    public static void main(String[] args) throws Exception {

//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//        keyPairGen.initialize(1024);
//
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//
//        ObjectOutputStream pubOos = new ObjectOutputStream(new FileOutputStream("D:\\thumbnail\\encrypt-key\\pubSecret.key"));
//        ObjectOutputStream priOos = new ObjectOutputStream(new FileOutputStream("D:\\thumbnail\\encrypt-key\\priSecret.key"));
//
//        pubOos.writeObject(publicKey);
//        priOos.writeObject(privateKey);
//
//        FileUtil.closeIO(priOos);
//        FileUtil.closeIO(pubOos);


//        InputStream is = new FileInputStream("D:\\thumbnail\\dicom_02400028_S_100135.bz2");
//        InputStream is = new FileInputStream("D:\\thumbnail\\encCache.bz2");
//        OutputStream encOs = new FileOutputStream("D:\\thumbnail\\encCache.bz2");
//        OutputStream decOs = new FileOutputStream("D:\\thumbnail\\decCache.bz2");


//        InputStream priKeyIs = new FileInputStream("D:\\javaworkspace\\micia-parent\\encrypt-key\\priSecret.key");
//        InputStream pubKeyIs = new FileInputStream("D:\\javaworkspace\\micia-parent\\encrypt-key\\pubSecret.key");
//
//        byte[] con = encryptRSA("测试点".getBytes("UTF-8"), priKeyIs);
//        System.out.println(new String(decryptRSA(con, pubKeyIs), "UTF-8"));



//        int[] arr = new int[100];
//        for(int i=0;i<100;i++){
//            arr[i]=i+1;
//        }
//
//        for(int mo:ArrayUtils.subarray(arr, 1, 100)){
//            System.out.print(mo+"-");
//        }

//        encrypt(is, encOs, pubKeyIs);
//        decrypt(is, decOs, priKeyIs);

//        encOs.flush();
//        encOs.close();

//        decOs.flush();
//        decOs.close();
    }
}
