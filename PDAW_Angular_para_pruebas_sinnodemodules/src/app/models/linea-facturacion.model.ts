// src/app/models/linea-facturacion.model.ts
import { Producto } from './producto.model';
// Asume que la clase Pedido ya está definida en otro lugar
import { Pedido } from './pedido.model';

export enum EstadoLineaFacturacion {
    activo = "activo",
    cancelado = "cancelado"
}

export class LineaFacturacion {
    idLineaFacturacion: number;
    pedido: Pedido;
    producto: Producto;
    cantidad: number;
    precioUnitario: number;
    precioLinea: number;
    estado: EstadoLineaFacturacion;

    constructor(
        idLineaFacturacion: number = 0,
        pedido: Pedido = new Pedido(), // Inicializando con una instancia vacía de Pedido
        producto: Producto = new Producto(), // Inicializando con una instancia vacía de Producto
        cantidad: number = 0,
        precioUnitario: number = 0.0,
        precioLinea: number = 0.0,
        estado: EstadoLineaFacturacion = EstadoLineaFacturacion.activo
    ) {
        this.idLineaFacturacion = idLineaFacturacion;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.precioLinea = precioLinea;
        this.estado = estado;
    }
}