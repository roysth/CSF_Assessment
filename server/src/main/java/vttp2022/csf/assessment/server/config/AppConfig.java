package vttp2022.csf.assessment.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AppConfig {

    @Value("${spaces.access.key}")
    public String spacesAccessKey; 
    //DO0068DQ8X4N4EEJGH8H
    @Value("${spaces.secret.key}")
    public String spacesSecretKey;
    //fFKAdGIE3OJWOMAib2kgz/MG4PYfRCfODJl/fegb7b4

    @Bean
    public AmazonS3 createS3Client() {

        BasicAWSCredentials cred = new BasicAWSCredentials(spacesAccessKey, spacesSecretKey);

        EndpointConfiguration ep = new EndpointConfiguration("sgp1.digitaloceanspaces.com", "spg1");

        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(ep)
            .withCredentials(new AWSStaticCredentialsProvider(cred))
            .build();
    }
    
}

