![http://digmap.googlecode.com/svn/trunk/LuceneGeoTemporal/lgtesmall.png](http://digmap.googlecode.com/svn/trunk/LuceneGeoTemporal/lgtesmall.png)

# Introduction #

LGTE is the IR system behind DIGMAP. It is generally built around the Lucene library for full-text search, introducing some extensions for dealing with geographical and temporal information. The package also includes useful utilities for IR evaluation experiments, such as methods for handling CLEF/TREC topics and document collections, many different text retrieval models (i.e. Okapi BM25, language modeling, divergence from randomness models) and query expansion mechanisms.

In a glance, the main features of LGTE are:

  * Provides a simple and efective abstraction layer on top of Lucene
  * Supports integrated retrieval and ranking with basis on thematic, temporal and geographical aspects.
  * Supports the Lucene standard retrieval model, as well as the more advanced probabilistic retrieval approaches.
  * Supports Rochio Query Expansion.
  * Provides a framework for IR evaluation experiments (e.g. handling CLEF/TREC topics).
  * Includes a Java alternative to the trec\_eval tool, capable of performing significance tests over pairs of runs.
  * Includes a simple test application for searching over the [Braun Corpus](http://ilps.science.uva.nl/datafiles/braun-corpus.zip) or the [Cranfield Corpus](http://dbappl.cs.utwente.nl/pftijah/Documentation/CranfieldDemo).

If you want to see LGTE running you have a [DEMO online here](http://digmap2.ist.utl.pt:8080/lgte).
# Libraries #

LGTE essentially assembles the following open-source libraries, patching them when necessary to work with our specific extensions:

  * Lucene http://lucene.apache.org
  * Local search extension for lucene http://sourceforge.net/projects/locallucene
  * LucQE http://lucene-qe.sourceforge.net/
  * Misc. Projects from Lucene sandbox http://lucene.apache.org/java/docs/lucene-sandbox/
  * IPLS Lucene with language modeling (LM-Lucene) http://ilps.science.uva.nl/resources/lm-lucene
  * Webgraph framework for making computations over linkage graphs http://webgraph.dsi.unimi.it/
  * LAW framework for computing PageRank and devivatives http://law.dsi.unimi.it/software/
  * Lucene Image REtrival (LIRE) library for content-based image retrieval http://www.semanticmetadata.net/lire/

# Using LGTE #

We provide a set of unit tests to help you to learn how to use LGTE. Our Library it is very similar to Lucene
and gives you an abstraction layer of time and spatial. You can find our examples at pt.utl.ist.lucene.test

Start with classes suffixed with SIMPLE. The other classes are also simple but there you will found more complex tests. After the simple try `TestSpatialDistanceWithLgteQueryParser` and `TestSpatialDistanceWithLgte`.

In our spatial examples we use a set of bars and restaurants in Portalegre, Portugal.
We set the stadium as the start geoPoint. you can find a Google Earth Document KML with this information.
If you check the distances with Google Earth you will see that the results are correct.

Here we provide a set of examples on LGTE QueryLanguage

# TREC/CLEF Evaluation Framework #

We provide a tool to index a collection, run a set of search topics and output the results in treckeval format. This format is like a standard in information retrieval tasks. You can found in the package [ireval](http://code.google.com/p/digmap/source/browse/trunk/LuceneGeoTemporal/WEB-INF/classes/ireval/Main.java)

We provide a test collection and a geospatial collection as an example and we configure those collections with a set of runs previously used in old tasks.