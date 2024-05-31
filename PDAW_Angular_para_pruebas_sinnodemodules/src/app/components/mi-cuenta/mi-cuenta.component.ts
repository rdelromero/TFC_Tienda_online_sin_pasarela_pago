import { Component, OnInit } from '@angular/core';
import { AutenticadoService } from '../../services/autenticado.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-mi-cuenta',
  templateUrl: './mi-cuenta.component.html',
  styleUrls: ['./mi-cuenta.component.css']
})
export class MiCuentaComponent implements OnInit {
  usuario: Usuario | null = null;

  constructor(private autenticadoService: AutenticadoService) {}

  ngOnInit(): void {
    this.autenticadoService.getUsuario().subscribe(
      data => {
        this.usuario = data;
      },
      error => {
        console.error('Error fetching usuario data', error);
      }
    );
  }
}