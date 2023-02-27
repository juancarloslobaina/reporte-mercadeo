import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CentroDetailComponent } from './centro-detail.component';

describe('Centro Management Detail Component', () => {
  let comp: CentroDetailComponent;
  let fixture: ComponentFixture<CentroDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CentroDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ centro: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CentroDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CentroDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load centro on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.centro).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
