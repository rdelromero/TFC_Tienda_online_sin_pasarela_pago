import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CarritoService } from '../../services/carrito.service';
import { PedidoService } from '../../services/pedido.service';
import { LineaFacturacionDto } from '../../models/linea-facturacion-dto.model';

@Component({
  selector: 'app-carrito',
  templateUrl: './carrito.component.html',
  styleUrls: ['./carrito.component.css']
})
export class CarritoComponent implements OnInit {
  carrito: LineaFacturacionDto[] = [];
  errorMessage: string = '';
  nombre: string = '';
  apellidos: string = '';
  direccion: string = '';
  pais: string = '';
  ciudad: string = '';
  numeroTelefonoMovil: string = '';
  metodoEnvio: string = '';

  constructor(
    private carritoService: CarritoService,
    private pedidoService: PedidoService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carrito = this.carritoService.obtenerCarrito();
    console.log('Carrito en vista:', this.carrito);
  }

  eliminarProducto(idProducto: number): void {
    this.carritoService.eliminarProducto(idProducto);
    this.carrito = this.carritoService.obtenerCarrito();
  }

  limpiarCarrito(): void {
    this.carritoService.limpiarCarrito();
    this.carrito = [];
  }

  realizarPedido(): void {
    const pedido = {
      lineasFacturacionDto: this.carrito,
      nombre: this.nombre,
      apellidos: this.apellidos,
      direccion: this.direccion,
      pais: this.pais,
      ciudad: this.ciudad,
      numeroTelefonoMovil: this.numeroTelefonoMovil,
      metodoEnvio: this.metodoEnvio
    };

    this.pedidoService.crearPedido(pedido).subscribe(
      response => {
        console.log('Pedido realizado con éxito:', response);
        this.carritoService.limpiarCarrito();
        //this.router.navigate(['/mi-cuenta/pedidos']);
      },
      error => {
        console.error('Error al realizar el pedido', error);
        this.errorMessage = 'No se pudo realizar el pedido. Inténtalo de nuevo más tarde.';
      }
    );
  }
}