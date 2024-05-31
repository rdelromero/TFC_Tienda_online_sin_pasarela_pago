import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AutenticacionService {
  private apiUrl = 'http://localhost:8080/api'; // Asegúrate de que el puerto es correcto

  constructor(private http: HttpClient) {}

  login(usuarioDtoLogin: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, usuarioDtoLogin);
  }

  logout(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
    return this.http.post<any>(`${this.apiUrl}/logout`, {}, { headers });
  }

  getUsuario(): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`
    });
    return this.http.get<any>(`${this.apiUrl}/datos-usuario`, { headers });
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