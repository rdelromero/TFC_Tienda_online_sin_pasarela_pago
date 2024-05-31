// src/app/models/pedido.model.ts
import { Usuario } from './usuario.model';
import { LineaFacturacion } from './linea-facturacion.model';

export enum MetodoEnvio {
    Recogida_en_tienda = "Recogida en tienda",
    CTT_Express = "CTT Express",
    NACEX = "NACEX"
}

export enum EstadoPedido {
    pendiente = "pendiente",
    enviado = "enviado",
    entregado = "entregado",
    cancelado = "cancelado"
}

export class Pedido {
    idPedido: number;
    usuario: Usuario;
    nombre: string;
    apellidos: string;
    direccion: string;
    pais: string;
    ciudad: string;
    numeroTelefonoMovil: string;
    metodoEnvio: MetodoEnvio;
    estado: EstadoPedido;
    precioSubtotal: number;
    gastosEnvio: number;
    precioTotal: number;
    fechaPedido: Date;
    fechaActualizacion: Date;
    lineasFacturacion: LineaFacturacion[];

    constructor() {
        this.idPedido = 0;
        this.usuario = new Usuario(); // Asegúrate de que la clase Usuario está correctamente definida e importada
        this.nombre = '';
        this.apellidos = '';
        this.direccion = '';
        this.pais = '';
        this.ciudad = '';
        this.numeroTelefonoMovil = '';
        this.metodoEnvio = MetodoEnvio.Recogida_en_tienda;
        this.estado = EstadoPedido.pendiente;
        this.precioSubtotal = 0;
        this.gastosEnvio = 0;
        this.precioTotal = 0;
        this.fechaPedido = new Date();
        this.fechaActualizacion = new Date();
        this.lineasFacturacion = [];
    }
}