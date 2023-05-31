import { Component, OnInit } from '@angular/core';
import { AccountService } from '../services/account.service';


@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  checkAdmin=false;
  constructor(private accountService:AccountService) {
  }

  ngOnInit() {
    this.accountService.checkAdmin().subscribe((data)=>{
      if(data && data == true) this.checkAdmin=true;
    })
  }


}
