package vttp2022.csf.assessment.server;

import java.io.StringReader;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.LatLng;
import vttp2022.csf.assessment.server.models.Restaurant;

public class Utilities {

    // For Task 4:

    /*
    MONGO RESULTS:
    {
        "restaurant_id" : "40827287",
        "name" : "Ajisen Ramen",
        "cuisine" : "Asian",
        "address" : "14 Mott Street, 10013, Manhattan",
        "coordinates" : [
            -73.9985052,
            40.7141563
        ]
    }
   */
    //Create Restaurant object from Mongo doc (Used in RestaurantRepo)
    public static Restaurant createRestaurant (Document doc) {

        Restaurant restaurant = new Restaurant();
        LatLng coord = new LatLng();
        restaurant.setRestaurantId(doc.getString("restaurant_id"));
        restaurant.setName(doc.getString("name"));
        restaurant.setCuisine(doc.getString("cuisine"));
        restaurant.setAddress(doc.getString("address"));

        
        List<String> listlist = doc.getList("coordinates", String.class);

        coord.setLatitude(Float.parseFloat(listlist.get(1)));
        coord.setLongitude(Float.parseFloat(listlist.get(0)));

        restaurant.setCoordinates(coord);

        return restaurant;
    }

    //Create Restaurant Json Object to be sent to client
    public static JsonObject restauranToJson (Restaurant restaurant) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("name", restaurant.getName());
        objectBuilder.add("restaurantId", restaurant.getRestaurantId());
        objectBuilder.add("cuisine", restaurant.getCuisine());
        objectBuilder.add("address", restaurant.getAddress());
        objectBuilder.add("mapUrl", restaurant.getMapURL());

        JsonObject object = objectBuilder.build();

        return object;
    }


    //FOR TASK 5: (Used in Controller)
    
    //Read the Json Object sent over by Client
    public static JsonObject toJson (String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        return jsonReader.readObject();
    }

    //Form the Comment object from Json Object sent by client
    public static Comment toComment (JsonObject json) {
        Comment comment = new Comment();
        comment.setName(json.getString("name"));
        comment.setRating(json.getInt("rating"));
        comment.setRestaurantId(json.getString("restaurantId"));
        comment.setText(json.getString("text"));

        return comment;
    }
    
}
