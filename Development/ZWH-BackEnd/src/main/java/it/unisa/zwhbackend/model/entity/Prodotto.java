package it.unisa.zwhbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Classe che rappresenta un prodotto nel sistema.
 * Mappa la tabella "prodotto" nel database e include le regole di validazione per i campi.
 *
 * <p>Annota l'entità con {@code @Entity} per indicare che è una classe JPA. Usa {@code @Table} per
 * specificare il nome della tabella nel database. Usa {@code @Data} di Lombok per generare automaticamente
 * i metodi getter, setter, toString, equals e hashCode. Esclude la proprietà {@code utentiPossessori}
 * dalla serializzazione JSON tramite {@code @JsonIgnoreProperties}.
 *
 * @author Marco Meglio
 */
@Entity
@Data
@Table(name = "prodotto")
@JsonIgnoreProperties({"utentiPossessori"}) // Esclude dalla serializzazione la lista utentiPossessori
public class Prodotto {

  /**
   * Codice a barre del prodotto.
   *
   * <p>Questo campo è obbligatorio e deve avere una lunghezza massima di 8 caratteri, contenente
   * solo cifre. Annota il campo con {@code @Pattern} per validare il formato del codice.
   */
  @Id
  @Column(name = "codice_barre", nullable = false, unique = true)
  @Pattern(
          regexp = "^[0-9]{1,8}$",
          message = "Il codice deve avere una lunghezza massima di 8 caratteri e deve contenere solo cifre.")
  private String codiceBarre;

  /**
   * Nome del prodotto.
   *
   * <p>Questo campo è obbligatorio e deve contenere solo lettere dell'alfabeto, con una lunghezza massima
   * di 50 caratteri. Annota il campo con {@code @Pattern} per validare il formato del nome.
   */
  @Column(name = "nome_prodotto")
  @Pattern(
          regexp = "^[a-zA-Z]{1,50}$",
          message = "La lunghezza massima per questo campo è 50 caratteri e deve contenere solo lettere dell'alfabeto.")
  private String name;

  /**
   * Data di scadenza del prodotto.
   *
   * <p>Questo campo è obbligatorio e deve rispettare il formato "gg/mm/aa". Annota il campo con
   * {@code @Pattern} per validare il formato della data.
   */
  @Column(name = "data_scadenza")
  @Pattern(
          regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{2}$",
          message = "La Data deve essere del formato gg/mm/aa.")
  private String dataScadenza;

  /**
   * Categoria del prodotto.
   *
   * <p>Questo campo è una lista di stringhe che rappresentano le categorie a cui il prodotto appartiene.
   * Ogni prodotto può appartenere a più categorie (ad esempio 'vegano', 'gluten-free', ecc.).
   */
  @ElementCollection
  @Column(name = "categoria")
  private List<String> categoria;

  /**
   * Costruttore vuoto.
   *
   * <p>Necessario per il funzionamento di JPA, permette di creare una nuova istanza senza parametri.
   */
  public Prodotto() {}

  /**
   * Costruttore con parametri.
   *
   * <p>Permette di creare una nuova istanza di {@code Prodotto} con i dati specificati per nome, data di scadenza
   * e codice a barre. La lista delle categorie viene inizializzata come vuota.
   *
   * @param nomeProdotto il nome del prodotto
   * @param dataScadenza la data di scadenza del prodotto
   * @param codiceBarre il codice a barre del prodotto
   */
  public Prodotto(String nomeProdotto, String dataScadenza, String codiceBarre) {
    this.name = nomeProdotto;
    this.dataScadenza = dataScadenza;
    this.codiceBarre = codiceBarre;
    this.categoria = new ArrayList<>();
  }

  /**
   * Lista di utenti che possiedono il prodotto nel loro frigo.
   *
   * <p>Questa relazione è mappata tramite {@code @OneToMany}, indicando che un prodotto può essere posseduto
   * da molti utenti. La relazione è gestita dalla classe {@code PossiedeInFrigo}.
   */
  @OneToMany(mappedBy = "prodotto", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PossiedeInFrigo> utentiPossessori = new ArrayList<>();
}