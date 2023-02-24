import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-cuisine',
  templateUrl: './restaurant-cuisine.component.html',
  styleUrls: ['./restaurant-cuisine.component.css']
})
export class RestaurantCuisineComponent implements OnInit {

  cuisine!: string

  listOfRestaurants: String[] = []

  constructor (private activatedRoute: ActivatedRoute, private restaurantService: RestaurantService,
    private router: Router) {}
	
	// TODO Task 3
	// For View 2
  ngOnInit(): void {
    //Once onInit, cuisine will be set to this
    this.cuisine = this.activatedRoute.snapshot.params["cuisine"]

    this.restaurantService.getRestaurantsByCuisine(this.cuisine)
      .then(results => {
        this.listOfRestaurants = results
        console.log('>>>> POST RESULTS: ', results)
      })
      .catch (error => {
        console.log('>>>> Error: ', error)
      })
  }



}
