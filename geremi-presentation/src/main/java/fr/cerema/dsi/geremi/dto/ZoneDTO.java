package fr.cerema.dsi.geremi.dto;

import org.locationtech.jts.geom.Geometry;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneDTO {

	/**
	 *
	 */
	private static final long serialVersionUID = 3008965467440490443L;

	@JsonProperty("id_zone")
	private Long idZone;

	@JsonProperty("type")
	private String type;

	@JsonProperty("nom")
	private String nom;

	@JsonProperty("descritpion")
	private String description;

	@JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
	private Geometry geometry;

	@JsonProperty("id_territoire")
	private Long idTerritoire;

	@JsonProperty
	private Long idEtude;
}
