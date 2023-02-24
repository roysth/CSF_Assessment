import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Restaurant, Comment } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.css']
})
export class RestaurantDetailsComponent {

  restaurant!: string
  rest!: Restaurant

  mapUrl!: string
  

  form!: FormGroup

  constructor (private activatedRoute: ActivatedRoute, private restaurantService: RestaurantService,
    private router: Router, private fb: FormBuilder) {}
	
	// TODO Task 4 and Task 5
	// For View 3

  ngOnInit(): void {
    
    this.restaurant = this.activatedRoute.snapshot.params["restaurnt"]

    this.form = this.createForm()

    this.restaurantService.getRestaurant(this.restaurant)
      .then(results => {
        this.mapUrl = results.mapUrl
        this.rest = results as Restaurant
      })
      .catch (error => {
        console.log('>>>> Error: ', error)
      })

  }

  submit() {
    const comment = this.form.value as Comment
    comment.restaurantId = this.rest.restaurantId
    console.info('>>>> Comment Results: ', comment)
    this.restaurantService.postComment(comment)
      .then( results => {
        console.log('>> COMMENTS POSTED,', results)
      })
      .catch (error => {
        console.log('>>>> Error: ', error)
      })
    this.router.navigate(['/']);

  }


  createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control<string>('', [Validators.required, Validators.minLength(3)]),
      rating: this.fb.control<number>(1, [Validators.required, Validators.min(1), Validators.max(5)]),
      text: this.fb.control<string>('', Validators.required)
    })

  }

}
