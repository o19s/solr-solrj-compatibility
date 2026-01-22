# ⛅️ A compatibility demonstrator for ancient SolrJ versions

Want to upgrade your Solr infrastructure but your stuck with an ancient
SolrJ version in your application? We got you covered. This repo contains
a patched version of SolrJ 4.5.1 which is able to talk to recent Solr 9.x
Cloud setups.

## 🧪 Testing access

#### ⛅️ Launch a Solr Cloud ensemble

Launch a Solr Cloud ensemble and index some example data,
the Solr own `techproducts` in this case. We'll spread the
collection across 3 shards and 2 replicas.

```bash
$ docker compose up
$ docker exec -it solr bin/solr create_core -c techproducts -sh 3 -rf 2
$ docker exec -it solr bin/post -c techproducts example/exampledocs/
```

Afterwards build the Spring application and launch it. The Spring version
matches the era of SolrJ and is pinned to `1.2.8.RELEASE`:

```bash
$ mvn clean package
$ java -jar target/solrj-compatiblity-0.0.1-SNAPSHOT.jar
```

Now you're able to query the data in your Solr Cloud ensemble using
the root resource:

```bash
$ curl "http://localhost:8080/"
```

## 📦 Using the patched SolrJ client

To use the patched SolrJ client, add the following `repository` and `dependency` definition to your
Maven `pom.xml`.

```xml
<project>
  <dependencies>
      <dependency>
          <groupId>org.apache.solr</groupId>
          <artifactId>solr-solrj</artifactId>
          <version>4.5.1</version>
          <classifier>solr9-patched</classifier>
      </dependency>
  </dependencies>

  <repositories>
    <repository>
      <id>github</id>
      <name>OpenSource Connections SolrJ 4.x client for modern Solr Cloud clusters</name>
      <url>https://maven.pkg.github.com/o19s/solr-solrj-compatibility</url>
    </repository>
  </repositories>
</project>
```

> In order to access the GitHub Maven Repository, you need to
> [authenticate against GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
> using your GitHub credentials


## 👩‍💻 Building the patched SolrJ client

It's a simple Maven build

```bash
mvn clean package
```
