import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CentroMedicoDetailComponent } from './centro-medico-detail.component';

describe('CentroMedico Management Detail Component', () => {
  let comp: CentroMedicoDetailComponent;
  let fixture: ComponentFixture<CentroMedicoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CentroMedicoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ centroMedico: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CentroMedicoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CentroMedicoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load centroMedico on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.centroMedico).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
