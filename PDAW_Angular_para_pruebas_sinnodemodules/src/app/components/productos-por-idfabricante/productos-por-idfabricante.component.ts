// src/app/components/productos-por-id-fabricante/productos-por-id-fabricante.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Fabricante } from '../../models/fabricante.model';
import { FabricanteService } from '../../services/fabricante.service';
import { Producto } from '../../models/producto.model';


@Component({
  selector: 'app-productos-por-idfabricante',
  templateUrl: './productos-por-idfabricante.component.html',
  styleUrls: ['./productos-por-idfabricante.component.css']
})

export class ProductosPorIdfabricanteComponent implements OnInit {

  fabricanteHtml: Fabricante | null = null;
  productosPorFabricanteHtml: Producto[] = [];

  constructor(
    private fabricanteService: FabricanteService,
    private route: ActivatedRoute  // Inyectar ActivatedRoute aquí
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const nombreFabricante = params['nombreFabricanteRoutingModule'];
      this.fabricanteService.getFabricantePorNombre(nombreFabricante).subscribe({
        next: (fabricanteeee) => {
          this.fabricanteHtml = fabricanteeee;
          this.cargarProductosDeEseFabricante(this.fabricanteHtml.idFabricante);
        },
        error: (error) => {
          console.error('Error al obtener el ID del fabricante', error);
        }
      });
    });
  }

      private cargarProductosDeEseFabricante(idFabricante: number) {
        if (idFabricante) {

          this.fabricanteService.getProductosPorIdFabricante(idFabricante).subscribe({
            next: (productossss) => {
              this.productosPorFabricanteHtml = productossss;
            },
            error: (error) => {
              console.error('Error al obtener productos', error);
            }
          });
        }
      };

  }
