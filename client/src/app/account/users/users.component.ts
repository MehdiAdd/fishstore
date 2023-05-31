import { Component, OnInit } from '@angular/core';
import { AccountService } from 'src/app/services/account.service';
import { User } from 'src/app/store/model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  constructor(private accountService:AccountService, private router: Router) { }
  innerLoading = true;
  users:User[]=[];

  ngOnInit(): void {
    this.fetchUsers();

    
  }

  fetchUsers(){
    this.accountService.getAllUser().subscribe((data)=>{
      this.users=data;
      this.innerLoading=false;
    });
  }

  deleteUser(email:string){
    this.innerLoading = true;
    this.accountService.deleteUser(email).subscribe(data=>{
      //this.fetchUsers();
      this.users=data;
      this.innerLoading=false;
      //this.router.navigate(['/account/users']);
    });
  }

  acivateInactivateUser(emailVerified:number,email:string){
    this.innerLoading = true;
    this.accountService.AcivateInactivateUser(emailVerified,email).subscribe(data=>{
      //this.fetchUsers();
      this.users=data;
      this.innerLoading=false;
      //this.router.navigate(['/account/users']);
    });
  }

}
