package ink.anyway.component.common.s3.top;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.io.InputStream;

public interface OssClient {

    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws AmazonClientException;

    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata) throws AmazonClientException;

    public void deleteObject(String bucketName, String objectKey) throws AmazonClientException;

    public void setBucketPolicy(String bucketName, String policy);

}
