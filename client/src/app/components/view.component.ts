import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subscription,firstValueFrom,lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css']
})
export class ViewComponent implements OnInit{

  form!: FormGroup
  imageData: any
  comments!: String

  constructor(private http: HttpClient, private fb: FormBuilder) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      'index': this.fb.control(''),
      
    })
  }

  public getPost(){
    const formData = new FormData();

    const index: String = this.form.get('index')?.value

    firstValueFrom(
      this.http.get<any>('http://localhost:8080/api/get/' + index)
    ).then(
      (response)=>{
        console.info(response);
        this.imageData = response.image
      }
    )
  }

  public getDigitalOcean(){
    const formData = new FormData();

    const params = new HttpParams().set("key", this.form.get('index')?.value)
    
    firstValueFrom(
      this.http.get<any>('http://localhost:8080/api/getDigitalOcean',{params})
    ).then(
      (response)=>{
        //console.info(response);
        this.imageData = response.image
        this.comments = response.comments
      }
    )


  }

}
