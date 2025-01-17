package fr.cerema.dsi.geremi.services.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarriereDetailDTO {
    private String attr;
    private List<String> attrList;
    private String attrListSeparator;
    private String label;
}
