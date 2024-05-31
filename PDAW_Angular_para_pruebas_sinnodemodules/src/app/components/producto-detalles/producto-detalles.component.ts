import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductoService } from '../../services/producto.service';
import { CarritoService } from '../../services/carrito.service';
import { Producto } from '../../models/producto.model';
import { LineaFacturacionDto } from '../../models/linea-facturacion-dto.model';

@Component({
  selector: 'app-producto-detalles',
  templateUrl: './producto-detalles.component.html',
  styleUrls: ['./producto-detalles.component.css']
})
export class ProductoDetallesComponent implements OnInit {

  producto: Producto | null = null;
  errorMessage: string = '';
  cantidad: number = 1;

  constructor(
    private route: ActivatedRoute,
    private productoService: ProductoService,
    private carritoService: CarritoService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productoService.getProducto(id).subscribe(
      data => {
        this.producto = data;
      },
      error => {
        console.error('Error fetching producto data', error);
        this.errorMessage = 'No se pudo cargar los datos del producto.';
      }
    );
  }

  incrementarCantidad(): void {
    this.cantidad++;
  }

  decrementarCantidad(): void {
    if (this.cantidad > 1) {
      this.cantidad--;
    }
  }

  anadirAlCarrito(): void {
    if (this.producto) {
      const lineaFacturacion = new LineaFacturacionDto(this.producto.idProducto, this.cantidad);
      this.carritoService.anadirProducto(lineaFacturacion);
      console.log('Producto añadido al carrito:', lineaFacturacion);
    }
  }
}