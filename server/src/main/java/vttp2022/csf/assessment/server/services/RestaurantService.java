package vttp2022.csf.assessment.server.services;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.repositories.MapCache;
import vttp2022.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService {

	@Autowired
	private RestaurantRepository restaurantRepo;

	@Autowired
	private MapCache mapCache;

	// TODO Task 2 
	// Use the following method to get a list of cuisines 
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public List<String> getCuisines() {
		// Implmementation in here
		return restaurantRepo.getCuisines();
		
	}

	// TODO Task 3 
	// Use the following method to get a list of restaurants by cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public List<String> getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here
		return restaurantRepo.getRestaurantsByCuisine(cuisine);
		
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public Optional<Restaurant> getRestaurant (String restaurant) {
		// Implmementation in here

		//Get data out from Mongo first
		Optional<Restaurant> restOpt = restaurantRepo.getRestaurant(restaurant);

		if (restOpt.isEmpty()) {
			return Optional.empty();
		}
		Restaurant restObject = restOpt.get();


		//Retrieve map from S3
		String mapUrl = mapCache.getUrl(restObject);

		//If map is not in Spaces, get from map API and cache map in S3
		if (mapUrl == null || mapUrl.isEmpty()) {

			//Get the Map from Chuk using coordinates from Mongo
			byte[] map = mapCache.getMap(restObject);

			//Store map in S3
			String url = mapCache.upload(restObject, map);

			//Set the mapURL attribute in Resraurant Object
			restObject.setMapURL(url);


		} else {

			// If the map is present in S3, set the attribude directly
			restObject.setMapURL(mapUrl);
		}

		return Optional.of(restObject);
	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public void addComment(Comment comment) {
		// Implmementation in here
		restaurantRepo.addComment(comment);
	}
	
	// You may add other methods to this class

	//Continuataion of Task 5
	public Boolean checkComment (String name) {
		return restaurantRepo.checkComment(name);
	}
}
