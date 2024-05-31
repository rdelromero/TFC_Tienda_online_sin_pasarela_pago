// src/app/models/imagen.model.ts
import { Producto } from './producto.model';  // Asegúrate de importar la clase Producto si vas a referenciarla

export class Imagen {
    idImagen: number;
    producto: Producto; // Referencia al Producto, asumiendo que ya has definido ese modelo
    imagenUrl: string;

    constructor(
        idImagen: number = 0,
        producto: Producto = new Producto(), // Inicializando con una instancia vacía de Producto
        imagenUrl: string = ''
    ) {
        this.idImagen = idImagen;
        this.producto = producto;
        this.imagenUrl = imagenUrl;
    }
}