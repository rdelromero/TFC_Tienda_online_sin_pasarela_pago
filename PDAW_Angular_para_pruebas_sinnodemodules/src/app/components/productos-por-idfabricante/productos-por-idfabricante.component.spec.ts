import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductosPorIdfabricanteComponent } from './productos-por-idfabricante.component';

describe('ProductosPorIdfabricanteComponent', () => {
  let component: ProductosPorIdfabricanteComponent;
  let fixture: ComponentFixture<ProductosPorIdfabricanteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductosPorIdfabricanteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductosPorIdfabricanteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
