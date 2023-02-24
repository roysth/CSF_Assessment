import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { CuisineListComponent } from './components/cuisine-list.component';
import { RestaurantCuisineComponent } from './components/restaurant-cuisine.component';
import { RestaurantDetailsComponent } from './components/restaurant-details.component';
import { RestaurantService } from './restaurant-service';

const appRoutes: Routes = [
  {path:'', component: CuisineListComponent}, //view 1
  {path:'restaurant/:cuisine', component: RestaurantCuisineComponent}, //view 2
  {path:'details/:restaurant', component: RestaurantDetailsComponent}, //view 3
  {path: '**', redirectTo: '/', pathMatch: 'full'}
]



@NgModule({
  declarations: [
    AppComponent,
    CuisineListComponent,
    RestaurantCuisineComponent,
    RestaurantDetailsComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes, {useHash: true})
  ],
  providers: [ RestaurantService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
