
export class Categoria {
    idCategoria: number;
    nombre: string;
    descripcion: string;
    imagenUrl: string;

    constructor(
        idCategoria: number = 0, 
        nombre: string = '', 
        descripcion: string = '', 
        imagenUrl: string = ''
    ) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }
}