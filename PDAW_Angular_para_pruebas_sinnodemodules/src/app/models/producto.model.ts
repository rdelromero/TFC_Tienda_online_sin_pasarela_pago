// src/app/models/producto.model.ts
import { Fabricante } from './fabricante.model';
import { Subcategoria } from './subcategoria.model';
import { Imagen } from './imagen.model';
import { LineaFacturacion } from './linea-facturacion.model';  // Asumiendo que tienes una clase para LineaFacturacion

export enum TipoDescuento {
    sin_descuento = "sin_descuento",
    porcentual = "porcentual",
    absoluto = "absoluto"
}

export class Producto {
    idProducto: number;
    fabricante: Fabricante;
    subcategoria: Subcategoria;
    lineasFacturacion: LineaFacturacion[];  // Tipo adecuado para LineaFacturacion
    nombre: string;
    descripcion: string;
    detalles: string;
    precio: number;
    stock: number;
    novedad: boolean;
    tipoDescuento: TipoDescuento;
    descuento: number;
    precioFinal: number;
    numeroValoraciones: number;
    valoracionMedia: number;
    fechaCreacion: Date;
    fechaActualizacion: Date;
    imagenes: Imagen[];

    constructor() {
        this.idProducto = 0;
        this.fabricante = new Fabricante();
        this.subcategoria = new Subcategoria();
        this.lineasFacturacion = [];
        this.nombre = '';
        this.descripcion = '';
        this.detalles = '';
        this.precio = 0;
        this.stock = 0;
        this.novedad = false;
        this.tipoDescuento = TipoDescuento.sin_descuento;
        this.descuento = 0;
        this.precioFinal = 0;
        this.numeroValoraciones = 0;
        this.valoracionMedia = 0;
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
        this.imagenes = [];
    }
}