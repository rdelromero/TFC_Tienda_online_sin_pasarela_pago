// src/app/models/resena.model.ts
import { Producto } from './producto.model';
import { Usuario } from './usuario.model';

export class Resena {
    idResena: number;
    producto: Producto; // Asegúrate de que la clase Producto está correctamente definida e importada
    usuario: Usuario;  // Asegúrate de que la clase Usuario está correctamente definida e importada
    valoracion: number;
    titulo: string;
    comentario: string;
    fechaCreacion: Date;

    constructor(
        idResena: number = 0,
        producto: Producto = new Producto(), // Inicializando con una instancia vacía de Producto
        usuario: Usuario = new Usuario(),   // Inicializando con una instancia vacía de Usuario
        valoracion: number = 0,
        titulo: string = '',
        comentario: string = '',
        fechaCreacion: Date = new Date()
    ) {
        this.idResena = idResena;
        this.producto = producto;
        this.usuario = usuario;
        this.valoracion = valoracion;
        this.titulo = titulo;
        this.comentario = comentario;
        this.fechaCreacion = fechaCreacion;
    }
}