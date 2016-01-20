# Introduction #

The DIGMAP gazetteer is essentially a dictionary of geographic placenames. It maps geographic placenames (the names of natural features such as oceans and the names of human constructs such as cities and countries) to coordinate-based geographic locations.

# Details #

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages

# Querying the DIGMAP Gazetteer Service #

The DIGMAP gazetteer supports the [Alexandria Digital Library (ADL) protocol for accessing general-purpose gazetteer services](http://www.alexandria.ucsb.edu/gazetteer/protocol/specification.html).

An example of a service request is shown below. The request asks the gazetteer for standard reports for all populated places whose names contain the phrase "portugal".

```
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-service xmlns="http://www.alexandria.ucsb.edu/gazetteer" version="1.2">
<query-request>
  <gazetteer-query>
    <and>
      <name-query operator="contains-phrase" text="portugal"/>
      <class-query thesaurus="ADL Feature Type Thesaurus" term="countries"/>
    </and>
  </gazetteer-query>
  <report-format>standard</report-format>
</query-request>
</gazetteer-service>
```

Another example of a gazetteer query is shown below. This example requests all currently existing places whose names contain the phrase "lisbon" and that overlap a given spatial region, and that are neither populated places nor cemeteries.

```
<?xml version="1.0" encoding="UTF-8"?>
<gazetteer-query xmlns="http://www.alexandria.ucsb.edu/gazetteer" xmlns:gml="http://www.opengis.net/gml">
<and-not>
  <and>
    <place-status-query status="current"/>
    <name-query operator="contains-phrase" text="lisbon"/>
    <footprint-query operator="overlaps">
      <gml:Box>
        <gml:coordinates>-140,30 110,35</gml:coordinates>
      </gml:Box>
    </footprint-query>
  </and>
  <or>
    <class-query thesaurus="ADL Feature Type Thesaurus" term="populated places"/>
    <class-query thesaurus="ADL Feature Type Thesaurus" term="cemeteries"/>
  </or>
</and-not>
</gazetteer-query>
```