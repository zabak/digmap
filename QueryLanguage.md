# LGTE Query Language #
[go back to LuceneGeoTemporal ](LuceneGeoTemporal.md)

This page is a quick tour on how to use LGTE query language. Follow we describe the fields carried by LGTE and their possible values.

## Fields ##

LGTE provides a top layer on lucene query parser creating an `LgteQuery`. `LgteQueryParser` parses a query on LGTE query language creating a middle query we call a level 1 query. This query will have all LGTE specific parameters in `QueryParams` class and the rest of the query will be parsed with lucene `QueryParser`. You can crete an `LgteQuery` by yourself passing a `Query` and a `QueryParams`, or you can simply use our query language and let `LgteQueryParser` do the job for you. Next list enumerates all possible fields of LGTE query language, each of the with parameter in `QueryParam`:

  * lat (query reference latitude)
  * lng (query reference longitude)
  * north (limited box north limit)
  * south (limited box south limit)
  * east (limited box east limit)
  * west (limited box west limit)
  * radium (limit radium in miles by default)
  * radiumMiles
  * radiumKm
  * starttime (Time interval query bottom limit yyyy[-mm[-dd]] or reverse direction)
  * endtime (Top limit in time)
  * starttimeMiliseconds (in miliseconds)
  * endtimeMiliseconds (in miliseconds)
  * time (Reference Query Time yyyy[-mm[-dd]] or reverse direction)
  * timeMiliseconds (in miliseconds)
  * radiumYears (limit radium in years)
  * radiumMonths
  * radiumDays
  * radiumHours
  * radiumMinutes
  * radiumSeconds
  * radiumMiliSeconds
  * order (order type spatial, temporal, textual)
  * filter (filter results out of the box)
  * qe (use query expansion)
  * model (Searching model)

You can combine all these fields creating your own query. You just need to know the constraints in terms of minimal queries, further you can combine all of them:

  * `SpatialPointQuery(lat,lng)`
  * `SpatialPointQuery(lat,lng,SRadium)` where `SRadium := radium | radiumMiles | radiumKm
  * `SpatialRectangleQuery(north,south,east,west)` where `north, south := latitude; west, east := longitude`
    * `Point` can be defined by your self with a simple `SpatialPointQuery` or LGTE will set the middle point in rectangle
  * `TimePointQuery(time)`
  * `TimePointQuery(time,TRadium)` where `TRadium := radiumYears | radiumMonths | ... | radiumMiliSeconds`
  * `IntervalQuery(startTime, endTime)`
    * You can define a `TimePointQuery` to set reference query point or you can let LGTE choose the middle point between start and end time

## Order ##
The order or ranking system is using aou `GeoTemporal` Model based in ranking sigmoid formulas and puted all together in an integrated avarege model. We can choose here wich dimensions should and should not be used in that ranking model.

| **Order Field Values** | **Ranking** |
|:-----------------------|:------------|
| `sc`                   | Score       |
| `sp`                   | Spatial distance |
| `t`                    | Time distance |
| `sc_sp`                | Score and Spatial distance  |
| `sc_t`                 | Score and Time distance |
| `t_sp`                 | Time distance and Spatial distance |
| `sc_t_sp`              | All together |

## Filter ##
Filtering cn be used to prune results out of the box in rectangle, interval, and radium queries. When you define a radium or a limit you dont need to prune the results, by default thats what is being done for performance reasons. Radium or limits are allays   used in ranking formulas, and if you choose not prune out of the box results they will appear but the ranking depends on time, spatial and textual formulas.

| Filter Field Values | Active Filters |
|:--------------------|:---------------|
| `no`                | all Filters off |
| `sp`                | Filter spatial |
| `t`                 | Filter time    |
| `t_sp`              | Filter time and spatial |

## Query Expansion ##
Query expansion uses LuQE with rochio pseudo feedback alghorithm. You can set your own algorithm using advanced query configuration provided by LGTE. Query expansion can be provided in two diferent configurations. First using only textual retrieval in first set of results. Second using LGTE full machine to get first set of results to obtain expanded query. In second interaction full LGTE machine will allays be used.

| QE Field  Values | |
|:-----------------|:|
| no               | don't use query expansion |
| text             | use but first retrieval uses only textual ranking |
| lgte             | use and first retrieval uses full lgte ranking |

## Text Model ##
LGTE provides retrieval model configuration at run time using an implementation of University of Amsterdam (IPLS) Language Model. Beside Language Model and Vector Space Model, LGTE provides a set of 8 more models including BM25 and another 7 studied intensively in references. Follow we enumerate the possible values for model field:

  * `VectorSpace`
  * `LanguageMoldel`
  * `DLHHypergeometricDFRModel`
  * `InExpC2DFRModel`
  * `InExpB2DFRModel`
  * `IFB2DFRModel`
  * `InL2DFRModel`
  * `PL2DFRModel`
  * `BB2DFRModel`
  * `OkapiBM25Model`

## Examples of Queries ##
Here we present several queries and a short explanation on it.

```
 q1 := contents:(rivers montains) starttime:1880 endtime:1880 
 q2 := contents:(world war rails) time:1830 radiumYears:30 
 q3 := earthquake time:1755 radiumYears:2 north:39.754720 south:37.754720 west:-9.5 east:-4
 q4 := author:(Jorge Bruno) papers time:2007 radiumYears:4 order:t filter:none qe:text
 q5 := papers ist time:2007 lat:56.162500 lng:10.144250 radiumYears:4 order:t_sp_sc  
 q7 := restaurants portalegre lat:38.78844 lng:-9.171290 radiumKm:30 qe:lgte model:bm25
```

Query 1 it's a time interval query. With years ou can use diferent combinations in date format because parser find 4 digits for year and 2 digits for month and day. Another option is setting times in miliseconds (starttimeMiliseconds endtimeMiliseconds. Query 2 is a time radium query goes from 2003 to 2011 with center time in 2007. Query 3 is selecting results from the specified box limited from south to north and from west to east. Query 4 is doing the same as query 3 but is turning off the filters and setting up query expansion in textual mode. Note that turning off the filters will not prune results out of the choosed radium but the radium will be used to obtain time ranking trough our sigmoid formula. Query 5 is setting up text, spatial and time fields so ranking model will conbine the 3 dimensions (See strategies in next subsection). Query 6 is possible but is a bad combination, because is setting up order ranking to just use text model score, is setting reference points in time and space but is turning off the filters, so time and spatial fields will not be used. Query 7 is setting up a spatial search using query expansion in the same conditions used in final query, using lgte full engine. Query 8 is similar to query 7 but is setting up in runtime the model `OkapiBM25Model`, `bm25` is a short keyword that can be defined in Model enum class.


## Strategies when no radium is assigned ##

### Time ###
In strategy one we set the radium to be the half of the distance between the two most distant resources. In strategy two radium is the minimum distance between: a) the distance from the lower time in index and query time; b) the distance from the searched time to the upper time in index . In strategy 3 we will set radium to the diference between query time and actual date. In strategy one we will set α1=0.75 to get a score very close to zero when (distance = radium) since any resource should be after the radium distance. In strategy two and three we set α2=0. 5 because it is more permissive. Both variables can be defined in run time over query configuration class.

## Spatial ##
For spatial dimension we will set the radium equal to twice the bigger width in index. The width is the distance between the two most distant point in a form. If there are no resources with width defined we will use half of earth radium 3000 km. We hope this case don’t happens many times.