package fr.cerema.dsi.commons.datastore;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Attribut {

  private String name;
  private Object value;
  private Class binding;
}
