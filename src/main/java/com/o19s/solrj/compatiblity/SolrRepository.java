package com.o19s.solrj.compatiblity;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SolrRepository {

    private final SolrServer solrServer;

    @Autowired
    public SolrRepository(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public List<Techproduct> search(String query) {

        // build params
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);

        try {
            final SolrDocumentList results = solrServer.query(solrQuery).getResults();
            final List<Techproduct> products = new ArrayList<>();

            for (SolrDocument solrDocument : results) {
                products.add(new Techproduct((String) solrDocument.getFirstValue("id"), (String) solrDocument.getFirstValue("name")));
            }

            return products;
        } catch (SolrServerException e) {
            throw new RuntimeException("Error executing Solr query: " + query, e);
        }
    }

}
