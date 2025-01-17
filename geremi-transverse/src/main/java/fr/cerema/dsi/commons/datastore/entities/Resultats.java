package fr.cerema.dsi.commons.datastore.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Resultats {

    private String codeZone;
    private Integer annee;
    private Double prodprim;
    private Double prodtot;
    private Double bestot;
    private Double pprimintra;
    private Double pprimbrute;
    private Double besprim;
    private Double beschant;
}
