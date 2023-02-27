import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EspecialidadDetailComponent } from './especialidad-detail.component';

describe('Especialidad Management Detail Component', () => {
  let comp: EspecialidadDetailComponent;
  let fixture: ComponentFixture<EspecialidadDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EspecialidadDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ especialidad: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EspecialidadDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EspecialidadDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load especialidad on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.especialidad).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
