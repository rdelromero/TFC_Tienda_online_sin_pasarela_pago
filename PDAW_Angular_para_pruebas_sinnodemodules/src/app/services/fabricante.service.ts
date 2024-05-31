import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fabricante } from '../models/fabricante.model';
import { Producto } from '../models/producto.model';  // Asegúrate de tener este modelo definido

@Injectable({
  providedIn: 'root'
})
export class FabricanteService {

  private apiUrl = 'http://localhost:8080/api/fabricantes';  // URL de tu API

  constructor(private http: HttpClient) { }

  getFabricantePorNombre(nombre: string): Observable<Fabricante> {
    // Asegúrate de que la ruta coincide con la definida en el backend
    return this.http.get<Fabricante>(`${this.apiUrl}/nombre/${nombre}`);
  }

  getProductosPorIdFabricante(idFabricante: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.apiUrl}/${idFabricante}/productos`);
  }

}
