package vttp2022.csf.assessment.server.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.services.RestaurantService;

import static vttp2022.csf.assessment.server.Utilities.*;

@Controller
@RequestMapping (path="/api")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping (path="/cuisines")
    @ResponseBody
    public ResponseEntity<String> getCuisines () {

        List<String> cuisinesList = restaurantService.getCuisines();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        for(String c: cuisinesList)
            arrayBuilder.add(c);

        JsonArray results = arrayBuilder.build();

        return ResponseEntity 
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(results.toString());
    }

    @GetMapping (path="/${cuisine}/restaurants")
    @ResponseBody
    public ResponseEntity<String> getRestaurantsByCuisine (String cuisine) {

        List<String> restaurantList = restaurantService.getRestaurantsByCuisine(cuisine);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        for(String r: restaurantList)
            arrayBuilder.add(r);

        JsonArray results = arrayBuilder.build();

        return ResponseEntity 
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(results.toString());
    }

    @GetMapping (path="/${restaurant}/details")
    @ResponseBody
    //RETURNS A RESTAURANT OBJECT
    public ResponseEntity<String> getRestaurantDetails (String restaurant) {

        Optional<Restaurant> restOpt = restaurantService.getRestaurant(restaurant);

        if (restOpt.isEmpty()) {

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("Error", "Restaurant Object not found!");

            JsonObject error = objectBuilder.build();

            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(error.toString());
        }

        Restaurant restObject = restOpt.get();
        
        JsonObject results = restauranToJson(restObject);

        return ResponseEntity.ok(results.toString());

    }

    @PostMapping (path="/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> postComments (@RequestBody String payload) {

        //Read the incoming paylaod and store as Json Object
        JsonObject jsonObject = toJson(payload);

        //Create the Comment object
        Comment comment = toComment(jsonObject);

        //Create the document and insert into Mongo
        restaurantService.addComment(comment);

        String name = comment.getName();

        if (!restaurantService.checkComment(name)) {

            JsonObject obj = Json
            .createObjectBuilder()
            .add("Error", "Comment not added")
            .build();

            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(obj.toString());

        } else {

            JsonObject resp = Json
            .createObjectBuilder()
            .add("message", "Comment posted")
            .build();

            return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(resp.toString());
        }  
    }



    
}
