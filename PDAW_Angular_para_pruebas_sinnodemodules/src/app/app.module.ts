import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { HttpClientModule } from '@angular/common/http' //Añadido
import { ProductosPorIdfabricanteComponent } from './components/productos-por-idfabricante/productos-por-idfabricante.component';
import { UsuarioRegistroComponent } from './components/usuario-registro/usuario-registro.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // Añadido
import { UsuarioService } from './services/usuario.service';
import { LoginComponent } from './components/login/login.component';
import { MiCuentaComponent } from './components/mi-cuenta/mi-cuenta.component';
import { AutenticadoService } from './services/autenticado.service';
import { ProductoDetallesComponent } from './components/producto-detalles/producto-detalles.component';
import { CarritoComponent } from './components/carrito/carrito.component';
import { ProductoService } from './services/producto.service';
import { CarritoService } from './services/carrito.service';
import { HomeComponent } from './components/home/home.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductosPorIdfabricanteComponent,
    UsuarioRegistroComponent,
    LoginComponent,
    MiCuentaComponent,
    ProductoDetallesComponent,
    CarritoComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
  ],
  providers: [UsuarioService, AutenticadoService, CarritoService],
  bootstrap: [AppComponent],

})
export class AppModule { }
