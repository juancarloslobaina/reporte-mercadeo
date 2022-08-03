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

describe('Reporte e2e test', () => {
  const reportePageUrl = '/reporte';
  const reportePageUrlPattern = new RegExp('/reporte(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reporteSample = { descripcion: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=', fecha: '2022-08-01T13:18:08.295Z' };

  let reporte;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reportes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reportes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reportes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reporte) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reportes/${reporte.id}`,
      }).then(() => {
        reporte = undefined;
      });
    }
  });

  it('Reportes menu should load Reportes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reporte');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reporte').should('exist');
    cy.url().should('match', reportePageUrlPattern);
  });

  describe('Reporte page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reportePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reporte page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reporte/new$'));
        cy.getEntityCreateUpdateHeading('Reporte');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reportePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reportes',
          body: reporteSample,
        }).then(({ body }) => {
          reporte = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reportes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/reportes?page=0&size=20>; rel="last",<http://localhost/api/reportes?page=0&size=20>; rel="first"',
              },
              body: [reporte],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(reportePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reporte page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reporte');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reportePageUrlPattern);
      });

      it('edit button click should load edit Reporte page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reporte');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reportePageUrlPattern);
      });

      it('edit button click should load edit Reporte page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reporte');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reportePageUrlPattern);
      });

      it('last delete button click should delete instance of Reporte', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reporte').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reportePageUrlPattern);

        reporte = undefined;
      });
    });
  });

  describe('new Reporte page', () => {
    beforeEach(() => {
      cy.visit(`${reportePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reporte');
    });

    it('should create an instance of Reporte', () => {
      cy.get(`[data-cy="descripcion"]`)
        .type('../fake-data/blob/hipster.txt')
        .invoke('val')
        .should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="fecha"]`).type('2022-08-01T17:16').blur().should('have.value', '2022-08-01T17:16');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        reporte = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', reportePageUrlPattern);
    });
  });
});
