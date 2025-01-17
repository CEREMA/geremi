package fr.cerema.dsi.geremi.entities.converters;

import java.util.stream.Stream;

import fr.cerema.dsi.geremi.enums.EtatEtape;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EtatEtapeConverter implements AttributeConverter<EtatEtape, String> {

  @Override
  public String convertToDatabaseColumn(EtatEtape etatEtape) {
    if (etatEtape == null) {
      return null;
    }
    return etatEtape.getLibelle();
  }

  @Override
  public EtatEtape convertToEntityAttribute(String libelle) {
    if (libelle == null) {
      return null;
    }

    return Stream.of(EtatEtape.values())
      .filter(c -> c.getLibelle().equals(libelle))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }
}
