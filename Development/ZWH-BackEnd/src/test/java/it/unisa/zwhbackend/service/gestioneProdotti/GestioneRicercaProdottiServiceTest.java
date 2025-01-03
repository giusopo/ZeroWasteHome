package it.unisa.zwhbackend.service.gestioneProdotti;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.unisa.zwhbackend.model.entity.PossiedeInDispensa;
import it.unisa.zwhbackend.model.entity.Prodotto;
import it.unisa.zwhbackend.model.entity.ProdottoRequestDTO;
import it.unisa.zwhbackend.model.entity.Utente;
import it.unisa.zwhbackend.model.enums.CategoriaAlimentare;
import it.unisa.zwhbackend.model.repository.PossiedeInDispensaRepository;
import it.unisa.zwhbackend.model.repository.PossiedeInFrigoRepository;
import it.unisa.zwhbackend.model.repository.ProdottoRepository;
import it.unisa.zwhbackend.model.repository.UtenteRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per il servizio {@link GestioneProdottoService}. Questa classe verifica il
 * comportamento della ricerca dei prodotti per nome in vari scenari, come risultati validi, assenza
 * di risultati o gestione di input non validi. Utilizza Mockito per simulare le dipendenze.
 *
 * @author Alessandra Trotta
 */
class GestioneRicercaProdottiServiceTest {

  private ProdottoRepository prodottoRepository;
  private GestioneProdottoService gestioneProdottoService;
  private PossiedeInFrigoRepository possiedeInFrigoRepository;
  private PossiedeInDispensaRepository possiedeInDispensaRepository;
  private UtenteRepository utenteRepository;

  /**
   * Configura l'ambiente di test prima di ogni esecuzione. Crea un mock per il repository dei
   * prodotti e per l'interfaccia ProdottoService.
   */
  @BeforeEach
  void setUp() {
    prodottoRepository = mock(ProdottoRepository.class);
    possiedeInFrigoRepository = mock(PossiedeInFrigoRepository.class);
    possiedeInDispensaRepository = mock(PossiedeInDispensaRepository.class);
    utenteRepository = mock(UtenteRepository.class);
    gestioneProdottoService =
        new GestioneProdottoService(
            prodottoRepository,
            possiedeInFrigoRepository,
            possiedeInDispensaRepository,
            utenteRepository);
  }

  /**
   * Verifica che il servizio restituisca correttamente una lista di prodotti quando esistono
   * corrispondenze per il nome fornito.
   *
   * <p>Scenario testato: - Nome del prodotto presente nel database.
   *
   * @throws AssertionError se il risultato non corrisponde alle aspettative
   */
  @Test
  void testTC_GUS_RPN_01() {
    // Arrange: Preparazione del contesto di test
    String emailUtente = "test1@example.com";
    String nomeProdotto = "Pasta";

    // Creazione di un prodotto e di un record in dispensa associato all'utente
    Prodotto prodotto = creaProdotto(nomeProdotto);
    prodotto.setCodiceBarre("1234567890123");

    PossiedeInDispensa recordDispensa = new PossiedeInDispensa();
    recordDispensa.setProdotto(prodotto);
    recordDispensa.setQuantita(2);
    recordDispensa.setDataScadenza("2024-12-31");

    Utente utente = new Utente();
    utente.setEmail(emailUtente);

    // Mock del comportamento dei repository
    when(prodottoRepository.findByNameContainingIgnoreCase(nomeProdotto))
        .thenReturn(List.of(prodotto)); // Simula il ritorno del prodotto filtrato
    when(possiedeInDispensaRepository.findByUtente(any(Utente.class)))
        .thenReturn(List.of(recordDispensa)); // Simula il record in dispensa

    // Act: Esecuzione del metodo da testare
    List<ProdottoRequestDTO> risultato =
        gestioneProdottoService.RicercaPerNome(emailUtente, nomeProdotto);

    // Assert: Verifica del risultato
    assertNotNull(risultato);
    assertEquals(1, risultato.size()); // Verifica che ci sia un solo risultato

    // Verifica dei dettagli del DTO restituito
    ProdottoRequestDTO dto = risultato.get(0);
    assertEquals("Pasta", dto.getNomeProdotto());
    assertEquals("1234567890123", dto.getCodiceBarre());
    assertEquals("2024-12-31", dto.getDataScadenza());
    assertEquals(2, dto.getQuantità());
  }

  /**
   * Verifica che venga sollevata un'eccezione {@link NoSuchElementException} quando non vengono
   * trovati prodotti che corrispondono al nome fornito.
   *
   * <p>Scenario testato: - Nome del prodotto non presente nel database.
   *
   * @throws NoSuchElementException se nessun prodotto viene trovato
   */
  @Test
  void testTC_GUS_RPN_02() {
    // Arrange: Preparazione del contesto di test
    String nomeProdotto = "Spaghetti";
    when(prodottoRepository.findByNameContainingIgnoreCase(nomeProdotto))
        .thenReturn(Collections.emptyList());

    // Act & Assert: Verifica che venga sollevata l'eccezione corretta
    NoSuchElementException exception =
        assertThrows(
            NoSuchElementException.class,
            () -> {
              gestioneProdottoService.RicercaPerNome("test1@example.com", nomeProdotto);
            });
    assertEquals("Nessun prodotto trovato", exception.getMessage());
  }

  /**
   * Metodo helper per creare un'istanza di {@link Prodotto} con un nome specifico.
   *
   * @param nome il nome del prodotto da creare
   * @return un'istanza di {@link Prodotto} con i dati specificati
   */
  private Prodotto creaProdotto(String nome) {
    Prodotto prodotto = new Prodotto();
    prodotto.setName(nome);
    prodotto.setCategoria(List.of(CategoriaAlimentare.VEGANO.toString()));
    return prodotto;
  }
}
