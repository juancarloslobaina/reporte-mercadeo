import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICentroMedico } from '../centro-medico.model';
import { CentroMedicoService } from '../service/centro-medico.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './centro-medico-delete-dialog.component.html',
})
export class CentroMedicoDeleteDialogComponent {
  centroMedico?: ICentroMedico;

  constructor(protected centroMedicoService: CentroMedicoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.centroMedicoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
