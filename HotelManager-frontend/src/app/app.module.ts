import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import {
  BrowserModule,
  provideClientHydration,
} from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NavbarComponent } from './component/navbar/navbar.component';
import { HomeComponent } from './component/home/home.component';
import { AboutComponent } from './component/about/about.component';
import { ContactComponent } from './component/contact/contact.component';
import { LoginComponent } from './component/login/login.component';
import { AuthInterceptor } from './auth/auth-interceptor';
import { UserInfoComponent } from './component/user-info/user-info.component';
import { RoomsComponent } from './component/rooms/rooms.component';
import { RegisterComponent } from './component/register/register.component';
import { MakeReservationComponent } from './component/make-reservation/make-reservation.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { MyReservationsComponent } from './component/my-reservations/my-reservations.component';
import { FooterComponent } from './component/footer/footer.component';
import { StarRatingComponent } from './component/star-rating/star-rating.component';
import { NgxSliderModule } from '@angular-slider/ngx-slider';
import { MatPaginatorModule } from '@angular/material/paginator';
import { AddRoomDialogComponent } from './component/add-room-dialog/add-room-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { ConfirmDialogComponent } from './component/confirm-dialog/confirm-dialog.component';
import { ErrorDialogComponent } from './component/error-dialog/error-dialog.component';
import { SuccessDialogComponent } from './component/success-dialog/success-dialog.component';
import { MyMessagesComponent } from './component/my-messages/my-messages.component';
import { MessageDetailComponent } from './component/message-detail/message-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    AboutComponent,
    ContactComponent,
    LoginComponent,
    UserInfoComponent,
    RoomsComponent,
    RegisterComponent,
    MakeReservationComponent,
    MyReservationsComponent,
    FooterComponent,
    StarRatingComponent,
    AddRoomDialogComponent,
    ConfirmDialogComponent,
    ErrorDialogComponent,
    SuccessDialogComponent,
    MyMessagesComponent,
    MessageDetailComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
    NgxSliderModule,
    MatPaginatorModule,
    MatDialogModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    provideAnimationsAsync(),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
