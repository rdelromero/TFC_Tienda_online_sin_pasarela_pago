// src/app/models/subcategoria.model.ts
import { Categoria } from './categoria.model'; // Importa la clase Categoria

export class Subcategoria {
    idSubcategoria: number;
    categoria: Categoria; // Referencia a la clase Categoria
    nombre: string;
    descripcion: string;
    imagenUrl: string;

    constructor(
        idSubcategoria: number = 0,
        categoria: Categoria = new Categoria(), // Inicializando con una instancia vacía de Categoria
        nombre: string = '',
        descripcion: string = '',
        imagenUrl: string = ''
    ) {
        this.idSubcategoria = idSubcategoria;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }
}