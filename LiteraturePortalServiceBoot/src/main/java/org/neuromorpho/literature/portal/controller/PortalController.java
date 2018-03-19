package org.neuromorpho.literature.portal.controller;

import java.util.List;
import org.neuromorpho.literature.portal.model.Portal;
import org.neuromorpho.literature.portal.service.PortalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/literature/portals")
public class PortalController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PortalService portalService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "Portal up & running!";
    }
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    public List<Portal> getPortalList(
            @RequestParam(required = false) Boolean active) {
        return portalService.findPortalList(active);
    }
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT)
    public void updatePortalList(
            @RequestBody List<Portal> portalList) {
        portalService.updatePortalList(portalList);
    }

}
