import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductosPorIdfabricanteComponent } from './components/productos-por-idfabricante/productos-por-idfabricante.component';
import { UsuarioRegistroComponent } from './components/usuario-registro/usuario-registro.component';
import { LoginComponent } from './components/login/login.component';
import { MiCuentaComponent } from './components/mi-cuenta/mi-cuenta.component';
import { ProductoDetallesComponent } from './components/producto-detalles/producto-detalles.component';
import { CarritoComponent } from './components/carrito/carrito.component';
import { HomeComponent } from './components/home/home.component';

const routes: Routes = [
  //IMPORTANTE nombreFabricanteRoutingModule es el nombre del fabricante escrito dejando espacios o en kebab-case
  { path: '', component: HomeComponent },
  { path: 'fabricantes/:nombreFabricanteRoutingModule', component: ProductosPorIdfabricanteComponent },
  { path: 'registro-usuario', component: UsuarioRegistroComponent },
  { path: '', redirectTo: '/registro-usuario', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'mi-cuenta', component: MiCuentaComponent },
  { path: '', redirectTo: '/mi-cuenta', pathMatch: 'full' },
  { path: 'producto/:id', component: ProductoDetallesComponent },
  { path: '', redirectTo: '/producto/1', pathMatch: 'full' }, // Redirigir a un producto específico como ejemplo
  { path: 'mi-cuenta/carrito', component: CarritoComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
