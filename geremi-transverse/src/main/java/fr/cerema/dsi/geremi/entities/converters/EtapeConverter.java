package fr.cerema.dsi.geremi.entities.converters;

import java.util.stream.Stream;

import fr.cerema.dsi.geremi.enums.Etape;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EtapeConverter implements AttributeConverter<Etape, String> {

  @Override
  public String convertToDatabaseColumn(Etape etape) {
    if (etape == null) {
      return null;
    }
    return etape.getLibelle();
  }

  @Override
  public Etape convertToEntityAttribute(String libelle) {
    if (libelle == null) {
      return null;
    }

    return Stream.of(Etape.values())
      .filter(c -> c.getLibelle().equals(libelle))
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }
}
