import { Component, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-cuisine-list',
  templateUrl: './cuisine-list.component.html',
  styleUrls: ['./cuisine-list.component.css']
})
export class CuisineListComponent implements OnInit {

  listOfCuisines: String[] = []


  constructor(private restaurantService: RestaurantService){}
	// TODO Task 2
	// For View 1
  ngOnInit(): void {

    this.restaurantService.getCuisineList()
      .then(results => {
        this.listOfCuisines = results
      })
      .catch (error => {
        console.log('>>>> Error: ', error)
      })
      
  }
}
