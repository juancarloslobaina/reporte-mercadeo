import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('CentroMedico e2e test', () => {
  const centroMedicoPageUrl = '/centro-medico';
  const centroMedicoPageUrlPattern = new RegExp('/centro-medico(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const centroMedicoSample = { nombreCentroMedico: 'Honduras Escalinata' };

  let centroMedico;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/centro-medicos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/centro-medicos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/centro-medicos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (centroMedico) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/centro-medicos/${centroMedico.id}`,
      }).then(() => {
        centroMedico = undefined;
      });
    }
  });

  it('CentroMedicos menu should load CentroMedicos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('centro-medico');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('CentroMedico').should('exist');
    cy.url().should('match', centroMedicoPageUrlPattern);
  });

  describe('CentroMedico page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(centroMedicoPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create CentroMedico page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/centro-medico/new$'));
        cy.getEntityCreateUpdateHeading('CentroMedico');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', centroMedicoPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/centro-medicos',
          body: centroMedicoSample,
        }).then(({ body }) => {
          centroMedico = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/centro-medicos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/centro-medicos?page=0&size=20>; rel="last",<http://localhost/api/centro-medicos?page=0&size=20>; rel="first"',
              },
              body: [centroMedico],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(centroMedicoPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details CentroMedico page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('centroMedico');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', centroMedicoPageUrlPattern);
      });

      it('edit button click should load edit CentroMedico page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CentroMedico');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', centroMedicoPageUrlPattern);
      });

      it('edit button click should load edit CentroMedico page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('CentroMedico');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', centroMedicoPageUrlPattern);
      });

      it('last delete button click should delete instance of CentroMedico', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('centroMedico').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', centroMedicoPageUrlPattern);

        centroMedico = undefined;
      });
    });
  });

  describe('new CentroMedico page', () => {
    beforeEach(() => {
      cy.visit(`${centroMedicoPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('CentroMedico');
    });

    it('should create an instance of CentroMedico', () => {
      cy.get(`[data-cy="nombreCentroMedico"]`).type('Gerente Respuesta').should('have.value', 'Gerente Respuesta');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        centroMedico = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', centroMedicoPageUrlPattern);
    });
  });
});
