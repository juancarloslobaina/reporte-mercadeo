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

describe('Especialidad e2e test', () => {
  const especialidadPageUrl = '/especialidad';
  const especialidadPageUrlPattern = new RegExp('/especialidad(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const especialidadSample = { nombreEspecialidad: 'out-of-the-box Huerta bus' };

  let especialidad;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/especialidads+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/especialidads').as('postEntityRequest');
    cy.intercept('DELETE', '/api/especialidads/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (especialidad) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/especialidads/${especialidad.id}`,
      }).then(() => {
        especialidad = undefined;
      });
    }
  });

  it('Especialidads menu should load Especialidads page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('especialidad');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Especialidad').should('exist');
    cy.url().should('match', especialidadPageUrlPattern);
  });

  describe('Especialidad page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(especialidadPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Especialidad page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/especialidad/new$'));
        cy.getEntityCreateUpdateHeading('Especialidad');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', especialidadPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/especialidads',
          body: especialidadSample,
        }).then(({ body }) => {
          especialidad = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/especialidads+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/especialidads?page=0&size=20>; rel="last",<http://localhost/api/especialidads?page=0&size=20>; rel="first"',
              },
              body: [especialidad],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(especialidadPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Especialidad page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('especialidad');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', especialidadPageUrlPattern);
      });

      it('edit button click should load edit Especialidad page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Especialidad');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', especialidadPageUrlPattern);
      });

      it('edit button click should load edit Especialidad page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Especialidad');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', especialidadPageUrlPattern);
      });

      it('last delete button click should delete instance of Especialidad', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('especialidad').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', especialidadPageUrlPattern);

        especialidad = undefined;
      });
    });
  });

  describe('new Especialidad page', () => {
    beforeEach(() => {
      cy.visit(`${especialidadPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Especialidad');
    });

    it('should create an instance of Especialidad', () => {
      cy.get(`[data-cy="nombreEspecialidad"]`).type('state feed').should('have.value', 'state feed');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        especialidad = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', especialidadPageUrlPattern);
    });
  });
});
