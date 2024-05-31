import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AutenticadoService {
  private apiUrl = 'http://localhost:8080/apiprivada'; // Asegúrate de que el puerto es correcto

  constructor(private http: HttpClient) {}


  logout(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
    return this.http.post<any>(`${this.apiUrl}/logout`, {}, { headers });
  }

  getUsuario(): Observable<Usuario> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
    return this.http.get<Usuario>(`${this.apiUrl}/datos-usuario`, { headers });
  }

  private getToken(): string {
    return localStorage.getItem('JWT') || '';
  }

  setToken(token: string): void {
    localStorage.setItem('JWT', token);
  }

  clearToken(): void {
    localStorage.removeItem('JWT');
  }
}