export class Usuario {
    idUsuario: number;
    username: string;
    password: string;
    nombre: string;
    apellido1: string;
    apellido2?: string; // Campo opcional
    role: Role;
    active: boolean;
    fechaCreacion: Date;
    fechaActualizacion: Date;

    constructor();
    constructor(
      idUsuario: number,
      username: string,
      password: string,
      nombre: string,
      apellido1: string,
      apellido2: string | undefined,
      role: Role,
      active: boolean,
      fechaCreacion: Date,
      fechaActualizacion: Date
    );
    constructor(
      idUsuario?: number,
      username?: string,
      password?: string,
      nombre?: string,
      apellido1?: string,
      apellido2?: string,
      role?: Role,
      active?: boolean,
      fechaCreacion?: Date,
      fechaActualizacion?: Date
    ) {
        this.idUsuario = idUsuario || 0;
        this.username = username || '';
        this.password = password || '';
        this.nombre = nombre || '';
        this.apellido1 = apellido1 || '';
        this.apellido2 = apellido2;
        this.role = role || Role.ROLE_USER;
        this.active = active || false;
        this.fechaCreacion = fechaCreacion || new Date();
        this.fechaActualizacion = fechaActualizacion || new Date();
    }
}

export enum Role {
  ROLE_ADMIN = 'ROLE_ADMIN',
  ROLE_USER = 'ROLE_USER'
}