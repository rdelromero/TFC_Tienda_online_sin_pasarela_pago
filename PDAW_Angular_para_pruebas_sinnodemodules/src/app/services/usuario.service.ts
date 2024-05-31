import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';  // Asegúrate de que esta URL coincida con tu configuración

  constructor(private http: HttpClient) { }

  crearUsuario(usuario: any): Observable<any> {
    return this.http.post(this.apiUrl, usuario, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    console.error('An error occurred', error);
    return throwError(error.message || error);
  }
}