import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutComponent } from './component/about/about.component';
import { HomeComponent } from './component/home/home.component';
import { ContactComponent } from './component/contact/contact.component';
import { LoginComponent } from './component/login/login.component';
import { UserInfoComponent } from './component/user-info/user-info.component';
import { AuthGuard } from './guard/auth.guard';
import { RoomsComponent } from './component/rooms/rooms.component';
import { RegisterComponent } from './component/register/register.component';
import { MakeReservationComponent } from './component/make-reservation/make-reservation.component';
import { MyReservationsComponent } from './component/my-reservations/my-reservations.component';
import { MyMessagesComponent } from './component/my-messages/my-messages.component';
import { PendingReservationsComponent } from './component/pending-reservations/pending-reservations.component';
import { AdminGuard } from './guard/admin.guard';
import { AccessDeniedComponent } from './component/access-denied/access-denied.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: RegisterComponent },
  { path: 'rooms', component: RoomsComponent },
  {
    path: 'make-reservation/:roomId',
    component: MakeReservationComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'my-reservations',
    component: MyReservationsComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'user-info',
    component: UserInfoComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'my-messages',
    component: MyMessagesComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'pending-reservations',
    component: PendingReservationsComponent,
    canActivate: [AdminGuard],
  },
  {
    path: 'access-denied',
    component: AccessDeniedComponent,
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
