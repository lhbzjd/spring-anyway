package ink.anyway.component.common.s3.simulate;

import com.alibaba.fastjson.JSON;
import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import ink.anyway.component.common.s3.top.OssClient;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Service
@ConditionalOnProperty(prefix = "oss.simulate", name = "endpoint")
public class SimulateClient implements OssClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${oss.simulate.endpoint}")
    private String endpoint;

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void init() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(builder.build());

        /** 配置同时支持 HTTP 和 HTPPS */
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslCsf).build();

        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        /**
         此处解释下MaxtTotal和DefaultMaxPerRoute的区别：
         1、MaxTotal是整个池子的大小；
         2、DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
             MaxTotal=400 DefaultMaxPerRoute=200
             而我只连接到http://www.abc.com时，到这个主机的并发最多只有200；而不是400; 而我连接到http://www.bac.com 和 http://www.ccd.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute
         */
        poolConnManager.setMaxTotal(640);
        poolConnManager.setDefaultMaxPerRoute(320);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).build();
        this.httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(config)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(2, false)).build();
    }

    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws AmazonClientException {

        CloseableHttpResponse httpResponse = null;
        PutObjectResult putObjectResult = null;

        try {
            HttpPost httpPost = new HttpPost(endpoint);

            StringBody bucketNameBody = new StringBody(bucketName,
                    ContentType.create("text/plain", Consts.UTF_8));
            StringBody objectKeyBody = new StringBody(objectKey, ContentType.create(
                    "text/plain", Consts.UTF_8));

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addBinaryBody("inputFile", input, ContentType.MULTIPART_FORM_DATA, "temp-put-file")
                    .addPart("bucketName", bucketNameBody)
                    .addPart("objectKey", objectKeyBody)
                    .setCharset(Consts.UTF_8)
                    .build();

            if (reqEntity != null)
                httpPost.setEntity(reqEntity);

            httpResponse = httpClient.execute(httpPost);

            int rec = httpResponse.getStatusLine().getStatusCode();

            if (rec == HttpStatus.SC_OK && httpResponse.getEntity()!=null) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                putObjectResult = JSON.parseObject(result, PutObjectResult.class);
                logger.info("http push file to storage success.");
            }else{
                logger.error("http push file to storage error["+rec+httpResponse.toString()+"]!");
            }
        } catch (IOException e) {
            logger.error("post http request error!", e);
        } finally {
            if (httpResponse != null)
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error("close httpResponse error!", e);
                }
        }

        return putObjectResult;
    }

    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata) throws AmazonClientException {
        return this.putObject(bucketName, objectKey, input);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException {
        return this.putObject(putObjectRequest.getBucketName(), putObjectRequest.getKey(), putObjectRequest.getInputStream());
    }

    @Override
    public void deleteObject(String bucketName, String objectKey) throws AmazonClientException {
        CloseableHttpResponse httpResponse = null;

        try {
            HttpPost httpPost = new HttpPost(endpoint);

            StringBody bucketNameBody = new StringBody(bucketName,
                    ContentType.create("text/plain", Consts.UTF_8));
            StringBody objectKeyBody = new StringBody(objectKey, ContentType.create(
                    "text/plain", Consts.UTF_8));

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart("bucketName", bucketNameBody)
                    .addPart("objectKey", objectKeyBody)
                    .setCharset(Consts.UTF_8)
                    .build();

            if (reqEntity != null)
                httpPost.setEntity(reqEntity);

            httpResponse = httpClient.execute(httpPost);

            int rec = httpResponse.getStatusLine().getStatusCode();

            if (rec == HttpStatus.SC_OK && httpResponse.getEntity()!=null) {
                logger.info("http delete file in storage success.");
            }else{
                logger.error("http delete file in storage error ["+rec+httpResponse.toString()+"]!");
            }
        } catch (IOException e) {
            logger.error("post http request error!", e);
        } finally {
            if (httpResponse != null)
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error("close httpResponse error!", e);
                }
        }
    }

}
