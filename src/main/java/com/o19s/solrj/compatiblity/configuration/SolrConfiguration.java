package com.o19s.solrj.compatiblity.configuration;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfiguration {

    @Bean
    public SolrServer solrServer(@Value("${solr.zookeeperHost}") String zkHost, @Value("${solr.collection}") String collection) {
        try {
            final CloudSolrServer solrServer = new CloudSolrServer(zkHost);
            solrServer.setDefaultCollection(collection);
            solrServer.connect();
            return solrServer;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Zookeeper host URL: " + zkHost, e);
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to Zookeeper host: " + zkHost, e);
        }
    }

}
