import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UploadComponent } from './components/upload.component';
import { ViewComponent } from './components/view.component';

const routes: Routes = [
  {path:'', component: UploadComponent},
  {path:'view', component: ViewComponent},
  {path: '**', redirectTo: '', pathMatch:'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
