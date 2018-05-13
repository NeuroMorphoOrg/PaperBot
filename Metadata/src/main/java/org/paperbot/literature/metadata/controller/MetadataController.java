package org.paperbot.literature.metadata.controller;

import org.paperbot.literature.metadata.service.MetadataService;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.paperbot.literature.metadata.model.MetadataFirstStage;
import org.paperbot.literature.metadata.model.MetadataValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class MetadataController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MetadataService metadataService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String home() {
        return "Metadata up & running!";
    }

    @CrossOrigin
    @RequestMapping(value = "/distinct", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> getMetadataDistinctValues(
            @RequestParam(required = true) String key) throws ParseException {
        return metadataService.getDistinctByKey(key);
    }

    @CrossOrigin
    @RequestMapping(value = "/values", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> getMetadataValues(
            @RequestParam(required = true) String key) throws ParseException {
        List<String> result = new ArrayList();
        List<MetadataValues> metadataValuesList = metadataService.getByKey(key);
        for (MetadataValues metadata : metadataValuesList) {
            result.add(metadata.getName());
        }
        return result;
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveMetadata(
            @PathVariable String id,
            @RequestBody Map<String, Object> attributes) {

        MetadataFirstStage metadata = new MetadataFirstStage(new ObjectId(id), attributes);
        metadataService.save(metadata);
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Map<String, Object> findMetadata(@PathVariable String id) {
        Map<String, Object> attributes = new HashMap();
        MetadataFirstStage metadata = metadataService.find(new ObjectId(id));
        if (metadata != null) {
            attributes = metadata.getAttributes();
        }
        return attributes;

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteMetadataList(@RequestParam List<String> ids) {
        metadataService.deleteMetadataList(ids);

    }

    @CrossOrigin
    @RequestMapping(value = "/removeAll", method = RequestMethod.DELETE)
    public void deleteMetadataList() {
        metadataService.deleteMetadataList();
    }

}
