package ink.anyway.component.common.s3.aws;

import com.amazonaws.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import ink.anyway.component.common.s3.top.OssClient;
import ink.anyway.component.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 支持亚马逊云对象存储S3协议，或者使用minio等第三方工具搭建的私有单机版对象存储S3协议
 */
public class AwsClient implements OssClient, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AmazonS3 conn;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String accessKey;

        private String secretKey;

        private String endpoint;

        private boolean isDomain = false;

        private int maxErrorRetry = 3;
        private int connectionTimeout = 30*1000;
        private int socketTimeout = 30*1000;
        private String signerOverride = "S3SignerType";

        private Builder() {}

        public AwsClient build() {
            return new AwsClient(accessKey, secretKey,
                    endpoint, isDomain, maxErrorRetry, connectionTimeout, socketTimeout, signerOverride);
        }

        public Builder setAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder setSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder setDomain(boolean domain) {
            this.isDomain = domain;
            return this;
        }

        public Builder setMaxErrorRetry(int maxErrorRetry) {
            this.maxErrorRetry = maxErrorRetry;
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setSignerOverride(String signerOverride) {
            this.signerOverride = signerOverride;
            return this;
        }
    }

    private AwsClient(String accessKey, String secretKey, String endpoint, boolean isDomain, int maxErrorRetry, int connectionTimeout, int socketTimeout, String signerOverride) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setMaxErrorRetry(maxErrorRetry);
        clientConfig.setConnectionTimeout(connectionTimeout);
        clientConfig.setSocketTimeout(socketTimeout);
        clientConfig.setSignerOverride(signerOverride);
        if(endpoint.startsWith("https")){
            clientConfig.setProtocol(Protocol.HTTPS);
        }else{
            clientConfig.setProtocol(Protocol.HTTP);
        }

        AmazonS3ClientBuilder awsBuilder = AmazonS3ClientBuilder.standard()
                .withClientConfiguration(clientConfig)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, ""))
                .withPathStyleAccessEnabled(true);
        if(isDomain){
            awsBuilder.enablePathStyleAccess();
        }
        conn = awsBuilder.build();
    }

    public String getLimitedUrl(String bucketName, String key, int expirationMinutes){
        URL url = conn.generatePresignedUrl(bucketName, key, new Date(System.currentTimeMillis()+(expirationMinutes*60*1000)), HttpMethod.GET);
        if(url!=null)
            return url.toString();
        else
            return null;
    }

    public PutObjectResult putObject(String bucketName, String objectKey, File file) throws AmazonClientException {
        try{
            return this.putObject(
                    bucketName,
                    objectKey,
                    new FileInputStream(file));
        }catch (IOException e){
            logger.error("Aws PutObject have io errors...", e);
        }
        return null;
    }

    public PutObjectResult putObject(String bucketName, String key, InputStream input) throws AmazonClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        try{
            metadata.setContentLength(input.available());
        }catch (IOException e){
            logger.warn("Aws PutObject have io errors...", e);
        }
        metadata.setHeader("x-amz-acl", "public-read");
        return this.putObject(
                bucketName,
                key,
                input,
                metadata);
    }

    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata) throws AmazonClientException {
        return this.conn.putObject(
                bucketName,
                objectKey,
                input, metadata);
    }

    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws SdkClientException{
        return this.conn.putObject(putObjectRequest);
    }

    public S3Object getObject(String bucketName, String key) throws AmazonClientException {
        return this.conn.getObject(
                new GetObjectRequest(bucketName, key)
        );
    }

    public ObjectMetadata getObject(String bucketName, String key, File destinationFile) throws AmazonClientException {
        return this.conn.getObject(
                new GetObjectRequest(bucketName, key),
                destinationFile
        );
    }

    public void deleteObject(String bucketName, String objectKey) throws AmazonClientException {
        this.conn.deleteObject(bucketName, objectKey);
    }

    public List<S3ObjectSummary> listAllFiles(String bucketName, String prefix){
        List<S3ObjectSummary> result = new ArrayList<>();
        ObjectListing objectListing = this.conn.listObjects(new ListObjectsRequest(bucketName, prefix, null, null, 1000));
        while(objectListing!=null){
            result.addAll(objectListing.getObjectSummaries());
            String nextMarker = objectListing.getNextMarker();
            objectListing = null;
            if(StringUtil.isValid(nextMarker)){
                objectListing = this.conn.listObjects(new ListObjectsRequest(bucketName, prefix, nextMarker, null, 1000));
            }
        }
        return result;
    }

    public void setBucketPolicy(String bucketName, String policy){
        this.conn.setBucketPolicy(bucketName, policy);
    }

    public void setBucketPolicy(final SetBucketPolicyRequest request){
        this.conn.setBucketPolicy(request);
    }

    public void setBucketAcl(String bucketName, AccessControlList acl)
            throws SdkClientException, AmazonServiceException{
        this.conn.setBucketAcl(bucketName, acl);
    }

    public void setBucketAcl(String bucketName, CannedAccessControlList acl)
            throws SdkClientException, AmazonServiceException{
        this.conn.setBucketAcl(bucketName, acl);
    }

    public List<S3ObjectSummary> listPhaseFiles(String bucketName, String prefix, String marker, String delimiter, Integer maxKeys){
        ObjectListing objectListing = this.conn.listObjects(new ListObjectsRequest(bucketName, prefix, marker, delimiter, maxKeys));
        if(objectListing!=null){
            return objectListing.getObjectSummaries();
        }else{
            return new ArrayList<>();
        }
    }

    public List<Bucket> listBuckets(){
        return this.conn.listBuckets();
    }

    @Override
    public void destroy() throws Exception {
        this.conn.shutdown();
    }
}
