import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription,firstValueFrom,lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit{

  @ViewChild('file') imageFile!: ElementRef;
  form!: FormGroup;
  
  constructor(private http: HttpClient, private fb: FormBuilder) { }

  ngOnInit(): void {
      this.form = this.fb.group({
        'imageFile': this.fb.control('',[Validators.required]),
        'comments': this.fb.control('comments',[Validators.required])
      })
  }

  public upload(){
    const formData = new FormData();

    formData.set('comments',this.form.get('comments')?.value)
    formData.set('file', this.imageFile.nativeElement.files[0]);

    firstValueFrom(
      this.http.post('http://localhost:8080/api/post',formData)
    ).then(
      (response)=>{
        console.info(response)
      }
    )
  }

  public uploadDigitalOcean(){

    const formData = new FormData();

    formData.set('comments',this.form.get('comments')?.value)
    formData.set('file', this.imageFile.nativeElement.files[0]);

    firstValueFrom(
      this.http.post('http://localhost:8080/api/postDigitalOcean',formData)
    ).then(
      (response)=>{
        console.info(response)
      }
    )
  }

}
