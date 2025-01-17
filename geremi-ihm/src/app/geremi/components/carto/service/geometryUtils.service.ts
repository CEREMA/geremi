import { Injectable } from "@angular/core";
import { GeoJsonProperties, Feature, Point, FeatureCollection, Geometry, GeometryCollection, BBox, Position, GeometryObject } from "geojson";

export type AllGeoJSON =
  | Feature
  | FeatureCollection
  | Geometry
  | GeometryCollection;

@Injectable({
  providedIn: 'root'
})
export class GeometryUtilsService {
  constructor() { }
  centroid<P = GeoJsonProperties>(
      geojson: AllGeoJSON,
      options: {
        properties?: P;
      } = {}
    ): Feature<Point, P> {
      let xSum = 0;
      let ySum = 0;
      let len = 0;
      this.coordEach(
        geojson,
        function (coord:any) {
          xSum += coord[0];
          ySum += coord[1];
          len++;
        },
        true
      );
      return this.point([xSum / len, ySum / len], options.properties);
  }

  coordEach(geojson:any, callback:any, excludeWrapCoord:any):any {
      // Handles null Geometry -- Skips this GeoJSON
      if (geojson === null) return;
      var j,
        k,
        l,
        geometry,
        stopG,
        coords,
        geometryMaybeCollection,
        wrapShrink = 0,
        coordIndex = 0,
        isGeometryCollection,
        type = geojson.type,
        isFeatureCollection = type === "FeatureCollection",
        isFeature = type === "Feature",
        stop = isFeatureCollection ? geojson.features.length : 1;
    
      // This logic may look a little weird. The reason why it is that way
      // is because it's trying to be fast. GeoJSON supports multiple kinds
      // of objects at its root: FeatureCollection, Features, Geometries.
      // This function has the responsibility of handling all of them, and that
      // means that some of the `for` loops you see below actually just don't apply
      // to certain inputs. For instance, if you give this just a
      // Point geometry, then both loops are short-circuited and all we do
      // is gradually rename the input until it's called 'geometry'.
      //
      // This also aims to allocate as few resources as possible: just a
      // few numbers and booleans, rather than any temporary arrays as would
      // be required with the normalization approach.
      for (var featureIndex = 0; featureIndex < stop; featureIndex++) {
        geometryMaybeCollection = isFeatureCollection
          ? geojson.features[featureIndex].geometry
          : isFeature
          ? geojson.geometry
          : geojson;
        isGeometryCollection = geometryMaybeCollection
          ? geometryMaybeCollection.type === "GeometryCollection"
          : false;
        stopG = isGeometryCollection
          ? geometryMaybeCollection.geometries.length
          : 1;
    
        for (var geomIndex = 0; geomIndex < stopG; geomIndex++) {
          var multiFeatureIndex = 0;
          var geometryIndex = 0;
          geometry = isGeometryCollection
            ? geometryMaybeCollection.geometries[geomIndex]
            : geometryMaybeCollection;
    
          // Handles null Geometry -- Skips this geometry
          if (geometry === null) continue;
          coords = geometry.coordinates;
          var geomType = geometry.type;
    
          wrapShrink =
            excludeWrapCoord &&
            (geomType === "Polygon" || geomType === "MultiPolygon")
              ? 1
              : 0;
    
          switch (geomType) {
            case null:
              break;
            case "Point":
              if (
                callback(
                  coords,
                  coordIndex,
                  featureIndex,
                  multiFeatureIndex,
                  geometryIndex
                ) === false
              ){return false;}
              coordIndex++;
              multiFeatureIndex++;
              break;
            case "LineString":
            case "MultiPoint":
              for (j = 0; j < coords.length; j++) {
                if (
                  callback(
                    coords[j],
                    coordIndex,
                    featureIndex,
                    multiFeatureIndex,
                    geometryIndex
                  ) === false
                ){return false;}
                coordIndex++;
                if (geomType === "MultiPoint") multiFeatureIndex++;
              }
              if (geomType === "LineString") multiFeatureIndex++;
              break;
            case "Polygon":
            case "MultiLineString":
              for (j = 0; j < coords.length; j++) {
                for (k = 0; k < coords[j].length - wrapShrink; k++) {
                  if (
                    callback(
                      coords[j][k],
                      coordIndex,
                      featureIndex,
                      multiFeatureIndex,
                      geometryIndex
                    ) === false
                  ){return false;}
                  coordIndex++;
                }
                if (geomType === "MultiLineString") multiFeatureIndex++;
                if (geomType === "Polygon") geometryIndex++;
              }
              if (geomType === "Polygon") multiFeatureIndex++;
              break;
            case "MultiPolygon":
              for (j = 0; j < coords.length; j++) {
                geometryIndex = 0;
                for (k = 0; k < coords[j].length; k++) {
                  for (l = 0; l < coords[j][k].length - wrapShrink; l++) {
                    if (
                      callback(
                        coords[j][k][l],
                        coordIndex,
                        featureIndex,
                        multiFeatureIndex,
                        geometryIndex
                      ) === false
                    ){
                      return false;
                    }
                      
                    coordIndex++;
                  }
                  geometryIndex++;
                }
                multiFeatureIndex++;
              }
              break;
            case "GeometryCollection":
              for (j = 0; j < geometry.geometries.length; j++)
                if (this.coordEach(geometry.geometries[j], callback, excludeWrapCoord) === false){
                  return false;
                }
              break;
            default:
              throw new Error("Unknown Geometry Type");
          }
        }
      }
    }

  point<P = GeoJsonProperties>(
      coordinates: Position,
      properties?: P,
      options: { bbox?: BBox; id?: number } = {}
    ): Feature<Point, P> {
    if (!coordinates) {
      throw new Error("coordinates is required");
    }
    if (!Array.isArray(coordinates)) {
      throw new Error("coordinates must be an Array");
    }
    if (coordinates.length < 2) {
      throw new Error("coordinates must be at least 2 numbers long");
    }
    //if (!isNumber(coordinates[0]) || !isNumber(coordinates[1])) {
    //  throw new Error("coordinates must contain numbers");
    //}
  
    const geom: Point = {
      type: "Point",
      coordinates,
    };
    return this.feature(geom, properties, options);
  }


feature<G extends GeometryObject = Geometry,P = GeoJsonProperties>(
    geom: G | null,
    properties?: P,
    options: { bbox?: BBox; id?: number } = {}
  ): Feature<G, P> {
    const feat: any = { type: "Feature" };
    if (options.id === 0 || options.id) {
      feat.id = options.id;
    }
    if (options.bbox) {
      feat.bbox = options.bbox;
    }
    feat.properties = properties || {};
    feat.geometry = geom;
    return feat;
  }
}