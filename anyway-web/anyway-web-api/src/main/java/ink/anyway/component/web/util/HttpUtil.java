package ink.anyway.component.web.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpUtil {

    public static HttpHeaders createShortHeaders(MediaType type){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        return headers;
    }

    public static String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            }
        }

        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public boolean downResponse(File file, HttpServletResponse response, String fileName){
        boolean res = false;

        if(file==null||response==null)
            return res;

        String lastFileName = null;

        if(fileName!=null&&!"".equals(fileName.trim())){
            lastFileName = fileName.trim();
        }else{
            lastFileName = "unknow-file";
        }

        if(file.exists()&&file.isFile()){
            InputStream is = null;
            OutputStream os = null;
            try{
                response.setHeader("Content-disposition", "attachment; filename=" + new String(lastFileName.getBytes("GBK"), "ISO8859_1"));
                is = new FileInputStream(file);
                os = response.getOutputStream();
                byte buf[] = new byte[8192];
                int read;
                while ((read = is.read(buf)) != -1) {
                    os.write(buf, 0, read);
                }
                res = true;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    os.flush();
                }catch (Exception e){}

                try{
                    os.close();
                }catch (Exception e){}

                try{
                    is.close();
                }catch (Exception e){}
            }
        }
        return res;
    }
}
