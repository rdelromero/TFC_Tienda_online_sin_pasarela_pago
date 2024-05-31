import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = 'http://localhost:8080/apiprivada/pedidos'; // Asegúrate de que el puerto y la ruta son correctos

  constructor(private http: HttpClient) {}

  crearPedido(pedido: any): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getToken()}`,
      'Content-Type': 'application/json'
    });
    return this.http.post<any>(this.apiUrl, pedido, { headers });
  }

  private getToken(): string {
    return localStorage.getItem('JWT') || '';
  }
}