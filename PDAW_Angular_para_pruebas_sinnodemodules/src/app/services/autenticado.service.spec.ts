import { TestBed } from '@angular/core/testing';

import { AutenticadoService } from './autenticado.service';

describe('AutenticadoServiceService', () => {
  let service: AutenticadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AutenticadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
