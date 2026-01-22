package com.o19s.solrj.compatiblity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SolrController {

    private final SolrRepository solrRepository;
    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    public SolrController(SolrRepository solrRepository) {
        this.solrRepository = solrRepository;
    }

    @RequestMapping("/")
    @ResponseBody
    public String search(@RequestParam(value = "q", required = false, defaultValue = "*:*") String query) {
        try {
            return om.writeValueAsString(solrRepository.search(query));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing search results to JSON", e);
        }
    }

}
