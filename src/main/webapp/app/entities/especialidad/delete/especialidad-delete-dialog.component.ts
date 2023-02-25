import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEspecialidad } from '../especialidad.model';
import { EspecialidadService } from '../service/especialidad.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './especialidad-delete-dialog.component.html',
})
export class EspecialidadDeleteDialogComponent {
  especialidad?: IEspecialidad;

  constructor(protected especialidadService: EspecialidadService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.especialidadService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
