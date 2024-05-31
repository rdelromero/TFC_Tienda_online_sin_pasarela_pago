import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';

@Component({
  selector: 'app-usuario-registro',
  templateUrl: './usuario-registro.component.html',
  styleUrls: ['./usuario-registro.component.css']
})
export class UsuarioRegistroComponent {
  registroForm: FormGroup;

  constructor(private fb: FormBuilder, private usuarioService: UsuarioService) {
    this.registroForm = this.fb.group({
      username: ['', [Validators.required, Validators.maxLength(100)]],
      password: ['', [Validators.required, Validators.maxLength(100)]],
      nombre: ['', [Validators.required, Validators.maxLength(30)]],
      apellido1: ['', [Validators.required, Validators.maxLength(40)]],
      apellido2: ['', [Validators.maxLength(40)]],
      role: ['ROLE_USER', Validators.required],
      active: [true]
    });
  }

  onSubmit() {
    if (this.registroForm.valid) {
      this.usuarioService.crearUsuario(this.registroForm.value).subscribe(
        response => {
          console.log('Usuario creado exitosamente', response);
          // Aquí puedes añadir código para redirigir al usuario o mostrar un mensaje de éxito
        },
        error => {
          console.error('Error al crear el usuario', error);
          // Aquí puedes añadir código para mostrar un mensaje de error
        }
      );
    }
  }
}