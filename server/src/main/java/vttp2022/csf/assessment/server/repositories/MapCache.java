package vttp2022.csf.assessment.server.repositories;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import vttp2022.csf.assessment.server.models.LatLng;
import vttp2022.csf.assessment.server.models.Restaurant;

@Repository
public class MapCache {

	public static final String chukURL = "http://map.chuklee.com/map";

	private String spacesBucket = "vttp-bucket";

	private String spacesEndpointUrl ="sgp1.digitaloceanspaces.com";

	private final AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard().build();

    @Autowired
    private AmazonS3 s3Client;

	
	// TODO Task 4
	// Use this method to retrieve the map
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Get Map from Chuk's link
	public byte[] getMap (Restaurant restaurant) {
		// Implmementation in here

		LatLng coord = restaurant.getCoordinates();

		//Create URL
		String url = UriComponentsBuilder.fromUriString(chukURL)
			.queryParam("lat", coord.getLatitude())
			.queryParam("lng", coord.getLongitude())
			.toUriString();

		System.out.println(">>> URL TO CHUK: " + url);

		//Create GET Request
		RequestEntity<Void> request = RequestEntity
		.get(url)
		.accept(MediaType.ALL)
		.build();

		//Make the GET request and receive the response as Byte array
		RestTemplate template = new RestTemplate();
		ResponseEntity<byte[]> response = template.exchange(request, byte[].class);
		
		byte[] imageURLInBytes = response.getBody();

		// String imageUrl = new String(imageURLInBytes, StandardCharsets.UTF_8);
		// System.out.println(">>> IMAGE URL: " + imageUrl);

		return imageURLInBytes;
	}


	//To save into S3
	public String upload(Restaurant restaurant, byte[] imageBytes) {


        //User Data
        Map<String, String> userData = new HashMap<>();
        userData.put("name", restaurant.getName());
        userData.put("restaurantId", restaurant.getRestaurantId());

        //Metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setUserMetadata(userData);

		// Create a InputStream for object upload
		ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);

        //Create a put request
        try {
            PutObjectRequest putReq = new PutObjectRequest(
                spacesBucket, //bucket name
                restaurant.getRestaurantId(), //key
                bais, //inputstream (Remember to throw IOException at the top)
                metadata);
            //Set it to be able to be read publicly
            putReq.withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putReq);

        } catch (Exception ex) {
            System.out.println("ERROR IN INSERTING INTO S3 " + ex);
        }

        String imageUrl = "https://%s.%s/%s"
        .formatted(spacesBucket, spacesEndpointUrl, restaurant.getRestaurantId());

        return imageUrl;
    }

	//To get imageUrl from S3
	public String getUrl (Restaurant restaurant) {

	    try {

			final S3Object s3Object = amazonS3Client.getObject(spacesBucket,restaurant.getRestaurantId());
        	final InputStreamReader streamReader = new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8);
        	final BufferedReader reader = new BufferedReader(streamReader);
			
			Set<String> set = reader.lines().collect(Collectors.toSet());

			String[] array = new String[set.size()];

			//Copy elements from set to string array
			int i = 0;
			for (String s: set) {
				array[i++] = s;
			}
			
			String mapUrl = array[0];

       		return mapUrl;
    		
		} catch (Exception ex) {
       		System.out.println("ERROR FINDING FILE IN S3 " + ex);

			String string = null;

        	return string;

    	}

	}

}
