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

describe('Ciudad e2e test', () => {
  const ciudadPageUrl = '/ciudad';
  const ciudadPageUrlPattern = new RegExp('/ciudad(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const ciudadSample = { nombreCiudad: 'uniforme Ladrillo' };

  let ciudad;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/ciudads+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/ciudads').as('postEntityRequest');
    cy.intercept('DELETE', '/api/ciudads/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (ciudad) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/ciudads/${ciudad.id}`,
      }).then(() => {
        ciudad = undefined;
      });
    }
  });

  it('Ciudads menu should load Ciudads page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ciudad');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ciudad').should('exist');
    cy.url().should('match', ciudadPageUrlPattern);
  });

  describe('Ciudad page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ciudadPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ciudad page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ciudad/new$'));
        cy.getEntityCreateUpdateHeading('Ciudad');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ciudadPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/ciudads',
          body: ciudadSample,
        }).then(({ body }) => {
          ciudad = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/ciudads+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/ciudads?page=0&size=20>; rel="last",<http://localhost/api/ciudads?page=0&size=20>; rel="first"',
              },
              body: [ciudad],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ciudadPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Ciudad page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ciudad');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ciudadPageUrlPattern);
      });

      it('edit button click should load edit Ciudad page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ciudad');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ciudadPageUrlPattern);
      });

      it('edit button click should load edit Ciudad page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ciudad');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ciudadPageUrlPattern);
      });

      it('last delete button click should delete instance of Ciudad', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ciudad').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ciudadPageUrlPattern);

        ciudad = undefined;
      });
    });
  });

  describe('new Ciudad page', () => {
    beforeEach(() => {
      cy.visit(`${ciudadPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ciudad');
    });

    it('should create an instance of Ciudad', () => {
      cy.get(`[data-cy="nombreCiudad"]`).type('explícita Ergonómico Guinea').should('have.value', 'explícita Ergonómico Guinea');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ciudad = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ciudadPageUrlPattern);
    });
  });
});
