import { Injectable } from '@angular/core';
import { LineaFacturacionDto } from '../models/linea-facturacion-dto.model';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private carritoKey = 'carrito';

  constructor() { }

  obtenerCarrito(): LineaFacturacionDto[] {
    const carritoJson = sessionStorage.getItem(this.carritoKey);
    const carrito = carritoJson ? JSON.parse(carritoJson) : [];
    console.log('Obteniendo carrito:', carrito);
    return carrito;
  }

  anadirProducto(lineaFacturacion: LineaFacturacionDto): void {
    const carrito = this.obtenerCarrito();
    const productoExistente = carrito.find(item => item.idProducto === lineaFacturacion.idProducto);

    if (productoExistente) {
      productoExistente.cantidad += lineaFacturacion.cantidad;
    } else {
      carrito.push(lineaFacturacion);
    }

    sessionStorage.setItem(this.carritoKey, JSON.stringify(carrito));
    console.log('Producto añadido:', lineaFacturacion);
    console.log('Carrito actualizado:', carrito);
  }

  eliminarProducto(idProducto: number): void {
    let carrito = this.obtenerCarrito();
    carrito = carrito.filter(item => item.idProducto !== idProducto);
    sessionStorage.setItem(this.carritoKey, JSON.stringify(carrito));
    console.log('Producto eliminado:', idProducto);
    console.log('Carrito actualizado:', carrito);
  }

  limpiarCarrito(): void {
    sessionStorage.removeItem(this.carritoKey);
    console.log('Carrito limpiado');
  }
}