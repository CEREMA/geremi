package fr.cerema.dsi.commons.datastore;

import java.util.HashMap;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class DataStore {

  private String referenceSystem;
  private HashMap<String,List<Attribut>> elements;
}
