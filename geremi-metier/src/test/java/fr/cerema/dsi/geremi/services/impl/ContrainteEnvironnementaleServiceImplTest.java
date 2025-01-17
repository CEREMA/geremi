package fr.cerema.dsi.geremi.services.impl;

import fr.cerema.dsi.geremi.entities.ContrainteEnvironnementale;
import fr.cerema.dsi.geremi.entities.Etude;
import fr.cerema.dsi.geremi.entities.Territoire;
import fr.cerema.dsi.geremi.repositories.ContrainteEnvironnementaleRepository;
import fr.cerema.dsi.geremi.services.SecurityService;
import fr.cerema.dsi.geremi.services.TerritoireService;
import fr.cerema.dsi.geremi.services.mapper.ContrainteEnvironnementaleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wololo.geojson.Feature;

import java.util.*;

import static org.geotools.feature.type.DateUtil.isEqual;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContrainteEnvironnementaleServiceImplTest {

  @InjectMocks
  private ContrainteEnvironnementaleServiceImpl contrainteEnvironnementaleService;
  @Mock
  private ContrainteEnvironnementaleRepository contrainteEnvironnementaleRepository;
  @Mock
  private ContrainteEnvironnementaleMapper contrainteMapper;

  @Mock
  private TerritoireService territoireService;

  @Mock
  private SecurityService securityService;

  List<ContrainteEnvironnementale> contraintesExistantes = new ArrayList<>();
  List<ContrainteEnvironnementale> contraintesExistantesEtude2 = new ArrayList<>();
  List<ContrainteEnvironnementale> contraintesEtude2 = new ArrayList<>();
  List<ContrainteEnvironnementale> contraintesEtude3 = new ArrayList<>();

  @BeforeEach
  public void setup() {
    Etude etude1 = new Etude();
    Etude etude2 = new Etude();

    etude1.setId(1L);
    etude2.setId(2L);

    contraintesExistantes = new ArrayList<>();
    contraintesEtude2 = new ArrayList<>();
    contraintesExistantesEtude2 = new ArrayList<>();

    ContrainteEnvironnementale contrainte1 = new ContrainteEnvironnementale();
    ContrainteEnvironnementale contrainte2 = new ContrainteEnvironnementale();
    ContrainteEnvironnementale contrainte3 = new ContrainteEnvironnementale();
    ContrainteEnvironnementale contrainte4 = new ContrainteEnvironnementale();
    ContrainteEnvironnementale contrainte5 = new ContrainteEnvironnementale();

    contrainte1.setIdContrEnv(1);
    contrainte1.setNom("Contrainte 1");
    contrainte1.setDescription("Description 1");
    contrainte1.setNiveau("Faible");
    contrainte1.setEtude(etude1);
    contrainte1.setIdSource(null);
    contrainte1.setTheGeom(null);

    contrainte2.setIdContrEnv(2);
    contrainte2.setNom("Contrainte 2");
    contrainte2.setDescription("Description 2");
    contrainte2.setNiveau("Moyenne");
    contrainte2.setEtude(etude1);
    contrainte2.setIdSource(null);
    contrainte2.setTheGeom(null);

    contrainte3.setIdContrEnv(3);
    contrainte3.setNom("Contrainte 3");
    contrainte3.setDescription("Description 3");
    contrainte3.setNiveau("Forte");
    contrainte3.setEtude(etude1);
    contrainte3.setIdSource(null);
    contrainte3.setTheGeom(null);

    contrainte4.setIdContrEnv(4);
    contrainte4.setNom("Contrainte 3");
    contrainte4.setDescription("Description 3");
    contrainte4.setNiveau("Forte");
    contrainte4.setEtude(etude2);
    contrainte4.setIdSource(3);
    contrainte4.setTheGeom(null);

    contrainte5.setIdContrEnv(5);
    contrainte5.setNom("Contrainte importé");
    contrainte5.setDescription("Description importé");
    contrainte5.setNiveau("Moyenne");
    contrainte5.setEtude(etude2);
    contrainte5.setIdSource(null);
    contrainte5.setTheGeom(null);

    contraintesExistantes.add(contrainte1);
    contraintesExistantes.add(contrainte2);
    contraintesExistantes.add(contrainte3);

    contraintesExistantesEtude2.add(contrainte1);
    contraintesExistantesEtude2.add(contrainte2);

    contraintesEtude2.add(contrainte4);
    contraintesEtude2.add(contrainte5);
  }

  @Test
  void getContrainteEnvironnementaleByIdTerritoireEmpty() {
    // GIVEN
    Long idTerritoire = 1L;
    Etude etude = new Etude();
    etude.setId(1L);
    Territoire territoire = new Territoire();
    territoire.setEtude(etude);
    Optional<List<Feature>> resultatAttendu = Optional.empty();

    // WHEN
    when(territoireService.findById(idTerritoire)).thenReturn(territoire);
    when(contrainteEnvironnementaleRepository.getContrainteEnvironnementaleByIdTerritoire(idTerritoire)).thenReturn(new ArrayList<>());

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getContrainteEnvironnementaleByIdTerritoire(idTerritoire);

    verify(contrainteEnvironnementaleRepository,times(1)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
    assertTrue(isEqual(resultatAttendu,resultat));
  }

  @Test
  void getContrainteEnvironnementaleByIdTerritoireFind() {
    // GIVEN
    Long idTerritoire = 1L;
    Etude etude = new Etude();
    etude.setId(1L);
    Territoire territoire = new Territoire();
    territoire.setEtude(etude);

    List<Feature> resultatAttendu = contraintesExistantes.stream().map(c -> {
        Map<String, Object> properties = new HashMap<>();
        properties.put("nom", c.getNom());
        properties.put("description", c.getDescription());
        properties.put("niveau",c.getNiveau());
        properties.put("etude",c.getEtude().getId());
        properties.put("idSource",c.getIdSource());
        return new Feature(c.getIdContrEnv(),null,properties);
      }
    ).toList();

    // WHEN
    when(territoireService.findById(idTerritoire)).thenReturn(territoire);
    when(contrainteEnvironnementaleRepository.getContrainteEnvironnementaleByIdTerritoire(idTerritoire)).thenReturn(contraintesExistantes);
    when(contrainteMapper.contraintesToFeature(contraintesExistantes)).thenReturn(resultatAttendu);

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getContrainteEnvironnementaleByIdTerritoire(idTerritoire);

    if(resultat.isPresent()){
      verify(contrainteEnvironnementaleRepository,times(1)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
      assertTrue(isEqual(resultatAttendu,resultat.get()));
    } else {
      fail();
    }
  }

  @Test
  void getExistingContraintesByIdTerritoireVide() {
    // GIVEN
    Long idTerritoire = 1L;
    Long idEtude = 3L;
    Etude etude = new Etude();
    etude.setId(idEtude);
    Territoire territoire = new Territoire();
    territoire.setEtude(etude);
    List<Integer> listeId = new ArrayList<>();
    Optional<List<Feature>> resultatAttendu = Optional.empty();

    // WHEN
    when(territoireService.findById(idTerritoire)).thenReturn(territoire);
    when(contrainteEnvironnementaleRepository.getContrainteByEtude(idEtude)).thenReturn(new ArrayList<>());
    when(contrainteEnvironnementaleRepository.getContrainteEnvironnementaleByIdTerritoire(idTerritoire)).thenReturn(new ArrayList<>());

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getExistingContraintesByIdTerritoire(idTerritoire);

    verify(contrainteEnvironnementaleRepository,times(1)).getContrainteByEtude(idEtude);
    verify(contrainteEnvironnementaleRepository,times(0)).getExistingContraintesByIdTerritoire(idTerritoire,listeId);
    verify(contrainteEnvironnementaleRepository,times(1)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
    assertTrue(isEqual(resultatAttendu,resultat));
  }

  @Test
  void getExistingContraintesByIdTerritoireEtudeContrainteVide() {
    // GIVEN
    Long idTerritoire = 1L;
    Long idEtude = 3L;
    Etude etude = new Etude();
    etude.setId(idEtude);
    Territoire territoire = new Territoire();
    territoire.setEtude(etude);
    List<Integer> listeId = new ArrayList<>();
    List<Feature> resultatAttendu = contraintesExistantes.stream().map(c -> {
        Map<String, Object> properties = new HashMap<>();
        properties.put("nom", c.getNom());
        properties.put("description", c.getDescription());
        properties.put("niveau",c.getNiveau());
        properties.put("etude",c.getEtude().getId());
        properties.put("idSource",c.getIdSource());
        return new Feature(c.getIdContrEnv(),null,properties);
      }
    ).toList();

    // WHEN
    when(territoireService.findById(idTerritoire)).thenReturn(territoire);
    when(contrainteEnvironnementaleRepository.getContrainteByEtude(idEtude)).thenReturn(contraintesEtude3);
    when(contrainteEnvironnementaleRepository.getContrainteEnvironnementaleByIdTerritoire(idTerritoire)).thenReturn(contraintesExistantes);
    when(contrainteMapper.contraintesToFeature(contraintesExistantes)).thenReturn(resultatAttendu);

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getExistingContraintesByIdTerritoire(idTerritoire);

    if(resultat.isPresent()){
      verify(contrainteEnvironnementaleRepository,times(1)).getContrainteByEtude(idEtude);
      verify(contrainteEnvironnementaleRepository,times(0)).getExistingContraintesByIdTerritoire(idTerritoire,listeId);
      verify(contrainteEnvironnementaleRepository,times(1)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
      assertTrue(isEqual(resultatAttendu,resultat.get()));
    } else {
      fail();
    }
  }

  @Test
  void getExistingContraintesByIdTerritoireEtudeContraintePresente() {
    // GIVEN
    Long idTerritoire = 1L;
    Long idEtude = 2L;
    Etude etude = new Etude();
    etude.setId(idEtude);
    Territoire territoire = new Territoire();
    territoire.setEtude(etude);

    List<Integer> listeId = contraintesEtude2.stream().map(ContrainteEnvironnementale::getIdSource).filter(Objects::nonNull).toList();
    List<Feature> resultatAttendu = contraintesExistantesEtude2.stream().map(c -> {
        Map<String, Object> properties = new HashMap<>();
        properties.put("nom", c.getNom());
        properties.put("description", c.getDescription());
        properties.put("niveau",c.getNiveau());
        properties.put("etude",c.getEtude().getId());
        properties.put("idSource",c.getIdSource());
        return new Feature(c.getIdContrEnv(),null,properties);
      }
    ).toList();

    // WHEN
    when(territoireService.findById(idTerritoire)).thenReturn(territoire);
    when(contrainteEnvironnementaleRepository.getContrainteByEtude(idEtude)).thenReturn(contraintesEtude2);
    when(contrainteEnvironnementaleRepository.getExistingContraintesByIdTerritoire(idTerritoire,listeId)).thenReturn(contraintesExistantesEtude2);
    when(contrainteMapper.contraintesToFeature(contraintesExistantesEtude2)).thenReturn(resultatAttendu);

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getExistingContraintesByIdTerritoire(idTerritoire);

    if(resultat.isPresent()){
      verify(contrainteEnvironnementaleRepository,times(1)).getContrainteByEtude(idEtude);
      verify(contrainteEnvironnementaleRepository,times(1)).getExistingContraintesByIdTerritoire(idTerritoire,listeId);
      verify(contrainteEnvironnementaleRepository,times(0)).getContrainteEnvironnementaleByIdTerritoire(idTerritoire);
      assertTrue(isEqual(resultatAttendu,resultat.get()));
    } else {
      fail();
    }
  }

  @Test
  void getContrainteByEtudeVide() {
    // GIVEN
    Long idEtude = 3L;
    Optional<List<Feature>> resultatAttendu = Optional.empty();

    // WHEN
    when(contrainteEnvironnementaleRepository.getContrainteByEtude(idEtude)).thenReturn(contraintesEtude3);

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getContrainteByEtude(idEtude);

    verify(contrainteEnvironnementaleRepository,times(1)).getContrainteByEtude(idEtude);
    assertTrue(isEqual(resultatAttendu,resultat));
  }

  @Test
  void getContrainteByEtudePresente() {
    // GIVEN
    Long idEtude = 2L;
    List<Feature> resultatAttendu = contraintesEtude2.stream().map(c -> {
        Map<String, Object> properties = new HashMap<>();
        properties.put("nom", c.getNom());
        properties.put("description", c.getDescription());
        properties.put("niveau",c.getNiveau());
        properties.put("etude",c.getEtude().getId());
        properties.put("idSource",c.getIdSource());
        return new Feature(c.getIdContrEnv(),null,properties);
      }
    ).toList();

    // WHEN
    when(contrainteEnvironnementaleRepository.getContrainteByEtude(idEtude)).thenReturn(contraintesEtude2);
    when(contrainteMapper.contraintesToFeature(contraintesEtude2)).thenReturn(resultatAttendu);

    // THEN
    Optional<List<Feature>> resultat = this.contrainteEnvironnementaleService.getContrainteByEtude(idEtude);

    if(resultat.isPresent()){
      verify(contrainteEnvironnementaleRepository,times(1)).getContrainteByEtude(idEtude);
      assertTrue(isEqual(resultatAttendu,resultat.get()));
    } else {
      fail();
    }
  }
}
