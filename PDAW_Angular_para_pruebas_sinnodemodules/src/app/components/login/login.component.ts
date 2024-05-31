import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AutenticacionService } from '../../services/autenticacion.service';

interface ValidationMessages {
  [key: string]: { [key: string]: string };
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  validationMessages: ValidationMessages = {
    username: {
      required: 'Username is required.',
      maxlength: 'Username cannot be more than 100 characters.'
    },
    password: {
      required: 'Password is required.',
      maxlength: 'Password cannot be more than 100 characters.'
    }
  };

  constructor(private fb: FormBuilder, private authService: AutenticacionService, private router: Router) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.maxLength(100)]]
    });
  }

  get username() {
    return this.loginForm.get('username');
  }

  get password() {
    return this.loginForm.get('password');
  }

  getErrorMessage(field: string): string {
    const control = this.loginForm.get(field);
    if (control && control.touched && control.errors) {
      const errors = control.errors;
      const messages = this.validationMessages[field];
      for (const error in errors) {
        if (errors.hasOwnProperty(error)) {
          return messages[error];
        }
      }
    }
    return '';
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe(
        response => {
          console.log('Login exitoso', response);
          this.authService.setToken(response.JWT);
          this.router.navigate(['/mi-cuenta']);
        },
        error => {
          console.error('Error en el login', error);
          this.errorMessage = error.error.Mensaje || 'Error en el login';
        }
      );
    }
  }
}