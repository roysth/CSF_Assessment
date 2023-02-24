package vttp2022.csf.assessment.server.repositories;

import java.util.Optional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;

import static vttp2022.csf.assessment.server.Utilities.*;

@Repository
public class RestaurantRepository {

	private static final String C_RESTAURANTS = "restaurants";
	private static final String C_COMMENTS = "comments";

	@Autowired
    private MongoTemplate mongoTemplate;

	// TODO Task 2
	// Use this method to retrive a list of cuisines from the restaurant collection
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	/*
		MONGO QUERY:
		db.restaurants.distinct("cuisine")

		RESULTS:

		[
			"Afghan",
			"African",
			"American ",
			"Armenian",
			"Asian",
			"Bagels/Pretzels",
			"Bakery",
			"Bangladeshi",
			"Barbecue",
			"Bottled beverages, including water, sodas, juices, etc.",
			"Brazilian",
			"CafÃ©/Coffee/Tea",
		]
	*/
	public List<String> getCuisines() {
		// Implmementation in here
		List<String> unprocessed = mongoTemplate.findDistinct(new Query(), "cuisine", C_RESTAURANTS, String.class);
	
		List<String> processed = new LinkedList<>();

		for (String s: unprocessed) {
			processed.add(s.replace("/", "_"));
		}
		return processed;
	}



	// TODO Task 3
	// Use this method to retrive a all restaurants for a particular cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	/*
		MONGO QUERY:
		db.restaurants.find({"cuisine":"Asian"}, {_id:0, name:1})
	*/
	public List<String> getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here
		
		Query query = Query.query(Criteria.where("cuisine").regex(cuisine));

		query.fields().exclude("_id").include("name");

		List<Document> docs = mongoTemplate.find(query, Document.class, C_RESTAURANTS);

		List<String> listOfRestaurant = new ArrayList<>();

			for (Document doc : docs) {
				String jsonString = doc.toJson();
				listOfRestaurant.add(jsonString);
			}
		return listOfRestaurant;
		
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	/*
	MONGO QUERY: (Using Ajisen Ramen as an example)

		db.restaurants.aggregate([
		{
			$match: {name: "Ajisen Ramen"
			}},
		{
			$project: {
			_id: 0,
			restaurant_id: 1,
			name: 1,
			cuisine: 1,
			address: {
				$concat: [
				"$address.building",
				", ",
				"$address.street",
				", ",
				"$address.zipcode",
				", ",
				"$borough"
				]
			},
			coordinates: "$address.coord"
			}
		}
		])

	*/

	public Optional<Restaurant> getRestaurant(String restaurant) {
		// Implmementation in here

		MatchOperation matchGid = Aggregation.match(Criteria.where("name").is(restaurant));

		ProjectionOperation projectFields = Aggregation
			.project("restaurant_id", "name", "cuisine")
			.and("address.coord").as("coordinates")
			.and(StringOperators.Concat.valueOf("address.building")
				.concat(", ").concatValueOf("address.street")
				.concat(", ").concatValueOf("address.zipcode")
				.concat(", ").concatValueOf("borough"))
				.as("address")
			.andExclude("_id");
		
		Aggregation pipeline = Aggregation.newAggregation(matchGid, projectFields);

		AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, C_RESTAURANTS, Document.class);

		Document doc = results.iterator().next();
        Restaurant res = createRestaurant(doc);

		return Optional.of(res);

	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	/*
		MONGO QUERY:
		db.comments.insertOne({
			name: "roy",
			rating: 3,
			text: "delicious food, but poor service",
			restaurant_id: "40827287",
		});
	*/
	public void addComment(Comment comment) {
		// Implmementation in here
		Document doc = new Document();
		doc.put("name", comment.getName());
		doc.put("rating", comment.getRating());
		doc.put("text", comment.getText());
		doc.put("restaurant_id", comment.getRestaurantId());

		mongoTemplate.insert(doc, C_COMMENTS);
		
	}
	// You may add other methods to this class


	//To check if Comment is added
	/*
		MONGO QUERY:
		db.comments.find({name: "roy"}, {_id: 0, name: 1, rating: 1, text: 1, restaurant_id: 1}).count
	*/
	public Boolean checkComment (String name) {

		Query query = Query.query(Criteria.where("name").is(name));

        Integer count = (int) mongoTemplate.count(query, Document.class, C_COMMENTS);

        return count > 0;

	}
	
	

}
