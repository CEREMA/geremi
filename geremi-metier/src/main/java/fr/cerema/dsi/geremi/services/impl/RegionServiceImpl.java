package fr.cerema.dsi.geremi.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.cerema.dsi.geremi.dto.SelectionZonage;
import fr.cerema.dsi.geremi.services.RegionGeometryService;
import fr.cerema.dsi.geremi.services.mapper.SelectionZonageMapper;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import fr.cerema.dsi.geremi.entities.Region;
import fr.cerema.dsi.geremi.repositories.RegionRepository;
import fr.cerema.dsi.geremi.services.RegionService;
import fr.cerema.dsi.geremi.services.dto.RegionDTO;
import org.wololo.geojson.Feature;

@Service("regionService")
public class RegionServiceImpl implements RegionService {

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private RegionGeometryService regionGeometryService;

  @Autowired
  private SelectionZonageMapper selectionZonageMapper;

	@Override
	public List<Region> findAll() {
		return null;
	}

	@Override
	public Region getOne(Long aLong) {
		return null;
	}

	@Override
	public Region findById(Long aLong) {
		return this.regionRepository.findById(aLong).get();
	}

	@Override
	public Region create(Region entity) {
		return null;
	}

	@Override
	public void deleteById(Long aLong) {

	}

	@Override
	public Region save(Region entity) {
		return null;
	}

  public Geometry getGeometryByRegionId(Long id,double precision){
    return this.regionGeometryService.getGeometryByRegionId(id,precision);
  }

  @Override
	public List<Feature> findInBox(double  bbox0, double  bbox1, double  bbox2, double  bbox3, BigDecimal precision){
    return this.regionRepository.findInBox(bbox0, bbox1, bbox2, bbox3, precision).stream().map(sz -> selectionZonageMapper.toFeature(sz, false)).toList();
	}

  @Override
  public RegionDTO findRegionById(Long id, double precision) {
    Region region = this.regionRepository.findById(id).get();

    RegionDTO regionDTO = new RegionDTO();
    regionDTO.setId(region.getId());
    regionDTO.setCode(region.getCode());
    regionDTO.setNom(region.getNom());
    regionDTO.setGeometry(getGeometryByRegionId(region.getId(), precision));

    return regionDTO;
  }
}
