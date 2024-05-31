// src/app/models/fabricante.model.ts
import { Producto } from './producto.model';

export class Fabricante {
    idFabricante: number;
    nombre: string;
    fechaFundacion: Date; // LocalDateTime se mapea como Date en TypeScript
    pais: string;
    paginaWeb: string;
    descripcion: string;
    imagenUrl: string;
    productos: Producto[];

    constructor(
        idFabricante: number = 0, 
        nombre: string = '', 
        fechaFundacion: Date = new Date(), 
        pais: string = '', 
        paginaWeb: string = '', 
        descripcion: string = '', 
        imagenUrl: string = '',
        productos: Producto[] = []
    ) {
        this.idFabricante = idFabricante;
        this.nombre = nombre;
        this.fechaFundacion = fechaFundacion;
        this.pais = pais;
        this.paginaWeb = paginaWeb;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.productos = productos;
    }

}