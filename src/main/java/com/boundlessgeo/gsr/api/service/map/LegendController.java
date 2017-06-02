package com.boundlessgeo.gsr.api.service.map;

import com.boundlessgeo.gsr.model.map.LayerLegend;
import com.boundlessgeo.gsr.model.map.LayerNameComparator;
import com.boundlessgeo.gsr.model.map.LayersAndTables;
import com.boundlessgeo.gsr.model.map.Legends;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedType;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class LegendController extends AbstractMapServiceController {

    @Autowired
    public LegendController(@Qualifier("geoServer") GeoServer geoServer) {
        super(geoServer);
    }

    @GetMapping(path = "/legend")
    public Legends legendGet(@PathVariable String workspaceName) throws IOException {
        WorkspaceInfo workspace = catalog.getWorkspaceByName(workspaceName);
        if (workspace == null) {
            throw new NoSuchElementException("No workspace known by name: " + workspaceName);
        }

        List<LayerInfo> layersInWorkspace = new ArrayList<LayerInfo>();
        for (LayerInfo l : catalog.getLayers()) {
            if (l.getType() == PublishedType.VECTOR && l.getResource().getStore().getWorkspace().getName().equals(workspaceName)) {
                layersInWorkspace.add(l);
            }
        }
        Collections.sort(layersInWorkspace, LayerNameComparator.INSTANCE);
        List<LayerLegend> legends = new ArrayList<LayerLegend>();
        for (int i = 0; i < layersInWorkspace.size(); i++) {
            legends.add(new LayerLegend(layersInWorkspace.get(i), i));

        }
        return new Legends(legends);
    }
}
