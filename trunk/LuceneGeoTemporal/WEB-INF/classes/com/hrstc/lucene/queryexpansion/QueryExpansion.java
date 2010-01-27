package com.hrstc.lucene.queryexpansion;


import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import com.google.soap.search.GoogleSearchFault;
import com.hrstc.lucene.*;

import pt.utl.ist.lucene.config.PropertiesUtil;
import pt.utl.ist.lucene.versioning.LuceneVersionFactory;
import pt.utl.ist.lucene.QueryConfiguration;

/**
 * Implements Rocchio's pseudo feedback QueryExpansion algorithm
 * <p/>
 * Level1Query Expansion - Adding searchCallback terms to a user's
 * searchCallback. Level1Query expansion is the process of a searchCallback
 * engine adding searchCallback terms to a user's weighted searchCallback. The
 * intent is to improve precision and/or recall. The additional terms may be
 * taken from a thesaurus. For example a searchCallback for "car" may be
 * expanded to: car cars auto autos automobile automobiles [foldoc.org].
 * <p/>
 * To see options that could be configured through the properties file @see
 * Constants Section
 * <p/>
 * Created on February 23, 2005, 5:29 AM
 * <p/>
 * TODO: Yahoo started providing API to level1query www; could be nice to add
 * yahoo implementation as well
 * <p/>
 *
 * @author Neil O. Rouben
 */
public class QueryExpansion
{
    // CONSTANTS
    /**
     * Indicates which method to use for QE
     */
    public static final String METHOD_FLD = "QE.method";
    public static final String ROCCHIO_METHOD = "rocchio";
    /**
     * how much importance of document decays as doc rank gets higher. decay =
     * decay * rank 0 - no decay
     */
    public static final String DECAY_FLD = "QE.decay";
    /**
     * Number of documents to use
     */
    public static final String DOC_NUM_FLD = "QE.doc.num";
    /**
     * Number of terms to produce
     */
    public static final String TERM_NUM_FLD = "QE.term.num";

    /**
     * Indicates FLD what source to use to obtain documents {google, local,
     * null}
     */
    public static final String DOC_SOURCE_FLD = "QE.doc.source";
    /**
     * get documents from local repository
     */
    public static final String DOC_SOURCE_LOCAL = "local";
    /**
     * get documents from google
     */
    public static final String DOC_SOURCE_GOOGLE = "google";

    /**
     * Rocchio Params
     */
    public static final String ROCCHIO_ALPHA_FLD = "QE.rocchio.alpha";
    public static final String ROCCHIO_BETA_FLD = "QE.rocchio.beta";

    private QueryConfiguration prop;
    private Analyzer analyzer;
    private Searcher searcher;
    private Similarity modelSimilarity;
    private Similarity similarity;
    private Vector<TermQuery> expandedTerms;
    private static Logger logger = Logger
            .getLogger("com.hrstc.lucene.queryexpansion.QueryExpansion");

    /**
     * Creates a new instance of QueryExpansion
     *
     * @param similarity
     * @param analyzer   - used to parse documents to extract terms
     * @param searcher   - used to obtain idf
     */
    public QueryExpansion(Analyzer analyzer, Searcher searcher,
                          Similarity similarity, QueryConfiguration queryConfiguration)
    {
        this.analyzer = analyzer;
        this.searcher = searcher;
        this.modelSimilarity = similarity;
        this.similarity = Similarity.getDefault();
        this.prop = queryConfiguration;
    }

    /**
     * Performs Rocchio's level1query expansion with pseudo feedback qm = alpha
     * * level1query + ( beta / relevanDocsCount ) * Sum ( rel docs vector )
     *
     * @param queryStr - that will be expanded
     * @param hits     - from the original level1query to use for expansion
     * @return expandedQuery
     * @throws IOException
     * @throws ParseException
     */
    public Query expandQuery(String queryStr, Hits hits)
            throws IOException, ParseException
    {
        // Get Docs to be used in level1query expansion
        Vector<Document> vHits = getDocs(queryStr, hits);

        return expandQuery(queryStr, vHits);
    }

    /**
     * Gets documents that will be used in level1query expansion. number of docs
     * indicated by <code>QueryExpansion.DOC_NUM_FLD</code> from
     * <code> QueryExpansion.DOC_SOURCE_FLD </code>
     *
     * @param query - for which expansion is being performed
     * @param hits  - to use in case <code> QueryExpansion.DOC_SOURCE_FLD </code>
     *              is not specified
     * @return number of docs indicated by
     *         <code>QueryExpansion.DOC_NUM_FLD</code> from
     *         <code> QueryExpansion.DOC_SOURCE_FLD </code>
     * @throws IOException
     * @throws GoogleSearchFault
     */
    private Vector<Document> getDocs(String query, Hits hits)
            throws IOException
    {
        Vector<Document> vHits = new Vector<Document>();
        String docSource = prop.getProperty(QueryExpansion.DOC_SOURCE_FLD);
        // Extract only as many docs as necessary
        int docNum = Integer.valueOf(
                prop.getProperty(QueryExpansion.DOC_NUM_FLD)).intValue();

        // obtain docs from local hits
        if (docSource == null
                || docSource.equals(QueryExpansion.DOC_SOURCE_LOCAL))
        {
            // Convert Hits -> Vector
            for (int i = 0; ((i < docNum) && (i < hits.length())); i++)
            {
                vHits.add(hits.doc(i));
            }
        }
        // obtain docs from www through google
        else if (docSource.equals(QueryExpansion.DOC_SOURCE_GOOGLE))
        {
            GoogleSearcher googleQE = new GoogleSearcher(prop.getQueryProperties());
            try
            {
                vHits = googleQE.search(query);
            }
            catch (GoogleSearchFault e)
            {
                e.printStackTrace();
                throw new IOException(e.getStackTrace().toString());
            }
        }
        else
        {
            throw new RuntimeException(docSource + ": is not implemented");
        }

        return vHits;
    }

    /**
     * Performs Rocchio's level1query expansion with pseudo feedback qm = alpha
     * * level1query + ( beta / relevanDocsCount ) * Sum ( rel docs vector )
     *
     * @param queryStr - that will be expanded
     * @param hits     - from the original level1query to use for expansion
     *                 level1query; see constants for field names and values
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public Query expandQuery(String queryStr, Vector<Document> hits) throws IOException, ParseException
    {
        // Load Necessary Values from Properties
        float alpha = Float.valueOf(
                prop.getProperty(QueryExpansion.ROCCHIO_ALPHA_FLD))
                .floatValue();
        float beta = Float.valueOf(
                prop.getProperty(QueryExpansion.ROCCHIO_BETA_FLD)).floatValue();
        float decay = Float.valueOf(
                prop.getProperty(QueryExpansion.DECAY_FLD)).floatValue();
        int docNum = Integer.valueOf(
                prop.getProperty(QueryExpansion.DOC_NUM_FLD)).intValue();
        int termNum = Integer.valueOf(
                prop.getProperty(QueryExpansion.TERM_NUM_FLD)).intValue();

        // Create combine documents term vectors - sum ( rel term vectors )
        List<Vector<QueryTermVector>> docsTermVector = new ArrayList<Vector<QueryTermVector>>();
        List<String> boostFields = PropertiesUtil.getListPropertiesSuffix(prop.getQueryProperties(),"field.boost.");
        if(boostFields != null && boostFields.size() > 0)
        {
            for(String field: boostFields)
            {
                Vector<QueryTermVector> docsTermVectorField = getDocsTerms(hits, docNum, analyzer, field);
                if(docsTermVectorField != null)
                    docsTermVector.add(docsTermVectorField);
            }
        }
        else
        {
            docsTermVector.add(getDocsTerms(hits, docNum, analyzer, Defs.FLD_TEXT));
        }



        // Adjust term features of the docs with alpha * level1query; and beta;
        // and assign weights/boost to terms (tf*idf)
        Query expandedQuery = adjust(docsTermVector, queryStr, alpha, beta,
                decay, docNum, termNum);

        return expandedQuery;
    }


    private class FieldTermVector
    {
        HashMap<String,QueryTermVector> fields = new HashMap<String,QueryTermVector>();

        public FieldTermVector(Document doc)
        {
            List<String> boostFields = PropertiesUtil.getListPropertiesSuffix(prop.getQueryProperties(),"field.boost.");
            if(boostFields != null && boostFields.size() > 0)
            {

            }
            else
            {
                QueryTermVector docsTermVector = getQueryTermVector(doc, analyzer, Defs.FLD_TEXT);
                fields.put(Defs.FLD_TEXT,docsTermVector);
            }

        }

        public FieldTermVector(String query)
        {

        }

    }


    /**
     * Adjust term features of the docs with alpha * level1query; and beta; and
     * assign weights/boost to terms (tf*idf).
     *
     * @param docsTermsVector       of the terms of the top <code> docsRelevantCount </code>
     *                              documents returned by original level1query
     * @param queryStr              - that will be expanded
     * @param alpha                 - factor of the equation
     * @param beta                  - factor of the equation
     * @param docsRelevantCount     - number of the top documents to assume to be relevant
     * @param maxExpandedQueryTerms - maximum number of terms in expanded level1query
     * @return expandedQuery with boost factors adjusted using Rocchio's
     *         algorithm
     * @throws IOException
     * @throws ParseException
     */
    public Query adjust(List<Vector<QueryTermVector>> docsTermsVector,
                        String queryStr, float alpha, float beta, float decay,
                        int docsRelevantCount, int maxExpandedQueryTerms)
            throws IOException, ParseException
    {
        Query expandedQuery;

        // setBoost of docs terms
        Vector<TermQuery> docsTerms = new Vector<TermQuery>();
        for(Vector<QueryTermVector> qT : docsTermsVector)
        {
            Vector<TermQuery> termQueries = setBoost(qT, beta, decay);
            docsTerms.addAll(termQueries);
        }
        merge(docsTerms);
        logger.finer(docsTerms.toString());

        // setBoost of level1query terms
        // Get queryTerms from the level1query
        Vector<TermQuery> queryTerms;
        if (PropertiesUtil.getListProperties(prop.getQueryProperties(), "field.boost").size() == 0)
        {
            QueryTermVector queryTermsVector = new QueryTermVector(queryStr, analyzer);
            queryTerms = setBoost(queryTermsVector, alpha);
        }
        else
        {
            queryTerms = getBoostedOriginalQuery(queryStr, alpha);
        }
        // combine weights according to expansion formula
        int queryTermsCount = queryTerms.size();
        queryTerms.addAll(docsTerms);
//        Vector<TermQuery> expandedQueryTerms = combine(queryTerms, docsTerms);
        merge(queryTerms);
        setExpandedTerms(queryTerms);
        // Sort by boost=weight
        Comparator comparator = new QueryBoostComparator();
        Collections.sort(queryTerms, comparator);

        // Create Expanded Level1Query
        expandedQuery = mergeQueries(queryTerms, queryTermsCount + maxExpandedQueryTerms);
        logger.finer(expandedQuery.toString());

        return expandedQuery;
    }

    /**
     *
     * @param termsFreqs
     * @param vector
     * @param q
     * @param alpha
     * @throws IOException
     */
    private void getTermVectorsBoosts(Map<String,Integer> termsFreqs, Vector<TermQuery> vector, Query q, float alpha) throws IOException
    {
        if (q instanceof BooleanQuery)
        {
            BooleanQuery bq = (BooleanQuery) q;
            for (BooleanClause bc : bq.getClauses())
            {
                getTermVectorsBoosts(termsFreqs, vector, bc.query, alpha);
            }
        }
        else if (q instanceof TermQuery)
        {

            TermQuery tq = (TermQuery) q;
//            if(tq.getTerm().field().equals(Defs.FLD_TEXT)) // using only model 2
//            {

            int tf = termsFreqs.get(tq.getTerm().field() + ":" + tq.getTerm().text());
            setBoost(tq,tf,alpha,0.0f);
            vector.add(tq);
//            }
        }
    }

    /**
     *
     * @param termsFreqs
     * @param vector
     * @param q
     * @throws IOException
     */
    private void getTermFrequencies(Map<String,Integer> termsFreqs, Vector<TermQuery> vector, Query q) throws IOException
    {
        if (q instanceof BooleanQuery)
        {
            BooleanQuery bq = (BooleanQuery) q;
            for (BooleanClause bc : bq.getClauses())
            {
                getTermFrequencies(termsFreqs,vector, bc.query);
            }
        }
        else if (q instanceof TermQuery)
        {
            TermQuery tq = (TermQuery) q;
            Integer tf = termsFreqs.get(tq.getTerm().field() + ":" + tq.getTerm().text());
            if(tf == null)
            {
                termsFreqs.put(tq.getTerm().field() + ":" + tq.getTerm().text(),1);
                vector.add(tq);
            }
            else
            {
                termsFreqs.put(tq.getTerm().field() + ":" + tq.getTerm().text(),tf+1);
            }
        }
    }

    /**
     * author: Jorge Machado
     *
     * @param query to analyze
     * @param alpha factor
     * @return a query terms vector
     * @throws IOException on index read error
     */
    private Vector<TermQuery> getBoostedOriginalQuery(String query, float alpha) throws IOException
    {
        Vector<TermQuery> queryTerms = new Vector<TermQuery>();
        try
        {
            Query q = QueryParser.parse(query, "", analyzer);
            Map<String,Integer> termFreqs = new HashMap<String,Integer>();
            getTermFrequencies(termFreqs,queryTerms,q);
            getTermVectorsBoosts(termFreqs,queryTerms, q, alpha);
        }
        catch (ParseException e)
        {
            logger.severe(e.toString());
        }
        return queryTerms;
    }

    private void buildTermBoosted(TermQuery termQuery, StringBuilder termBuilder, boolean useField)
    {
        Term term = termQuery.getTerm();
        float boost = termQuery.getBoost();
        if (boost > 0)
        {
            String field = term.field();
            if (!useField || field == null || field.trim().length() == 0)
                field = "";
            else
                field += ":";
            if ((boost + "").indexOf("E-") < 0)
                termBuilder.append(field).append(term.text()).append("^").append(boost).append(" ");
            else
            {
                System.out.println("$$$$$$$$$BAD BOOST:" + term.text() + "^" + boost + " normalized = " + boost);
                System.exit(-1);
            }
            logger.finest(term + " : " + boost);
        }
    }
    /**
     * Merges <code>termQueries</code> into a single level1query. In the future
     * this method should probably be in <code>Level1Query</code> class. This is
     * akward way of doing it; but only merge queries method that is available
     * is mergeBooleanQueries; so actually have to make a string term1^boost1,
     * term2^boost and then parse it into a level1query
     *
     * @param termQueries - to merge
     * @return level1query created from termQueries including boost parameters
     */
    public Query mergeQueries(Vector<TermQuery> termQueries, int maxTerms)
            throws ParseException, IOException
    {
        Query query;
        // Select only the maxTerms number of terms
        int termCount = Math.min(termQueries.size(), maxTerms);
        List<TermQuery> terms = new ArrayList<TermQuery>();
        for(int i= 0; i < termCount;i++)
        {
            terms.add(termQueries.elementAt(i));
        }
        StringBuilder queryBuilder = new StringBuilder();
//        List<String> boostFields = PropertiesUtil.getListPropertiesSuffix(prop.getQueryProperties(),"field.boost.");
//        if(boostFields != null && boostFields.size() > 0)
//        {
//            for(String field: boostFields)
//            {
//                StringBuilder fieldBuilder = new StringBuilder();
//                float boostField = PropertiesUtil.getFloatProperty(prop.getQueryProperties(),"field.boost." + field);
//                boolean any = false;
//                for (TermQuery termQuery : terms)
//                {
//                    Map<String,List<String>> termProjections = getDocumentTermInFields(termQuery.getTerm().text(),boostFields);
//                    buildTermBoosted(termQuery,fieldBuilder,false);
//                    if(termQuery.getTerm().field().equals(field))
//                    {
//                        any = true;
//                        buildTermBoosted(termQuery,fieldBuilder,false);
//                    }
//                }
//                if(any && fieldBuilder.toString().trim().length()  > 0)
//                    queryBuilder.append(" ").append(field).append(":").append("(").append(fieldBuilder.toString()).append(")");//.append(boostField);
//            }


//        }
//        else
//        {
        for (TermQuery termQuery : terms)
        {
            buildTermBoosted(termQuery,queryBuilder,true);
        }
//        }
        logger.fine(queryBuilder.toString());
        query = LuceneVersionFactory.getLuceneVersion().parseQuery(queryBuilder.toString(), Defs.FLD_TEXT, analyzer);
        logger.fine(query.toString());
        return query;
    }

    /**
     * Extracts terms of the documents; Adds them to vector in the same order
     *
     * @param docsRelevantCount - number of the top documents to assume to be relevant
     * @param analyzer          - to extract terms
     * @return docsTerms docs must be in order
     */
    public Vector<QueryTermVector> getDocsTerms(Vector<Document> hits, int docsRelevantCount, Analyzer analyzer, String field) throws IOException
    {
        Vector<QueryTermVector> docsTerms = new Vector<QueryTermVector>();
        if (hits == null || hits.size() == 0)
            return docsTerms;
        // Process each of the documents
        for (int i = 0; ((i < docsRelevantCount) && (i < hits.size())); i++)
        {
            Document doc = hits.elementAt(i);
            // Get text of the document and append it
            StringBuffer docTxtBuffer = new StringBuffer();
            QueryTermVector qT = getQueryTermVector(doc, analyzer, field);
            docsTerms.add(qT);
        }
        return docsTerms;
    }

    /**
     * Return a map of one term representation in all fields
     * @param boostFields
     * @return
     * @throws IOException
     */
    private Map<String,List<String>> getDocumentTermInFields(String term, List<String> boostFields) throws IOException
    {
        Map<String,List<String>> docTermInFields = new HashMap<String,List<String>>();
        for (String field : boostFields)
        {
            List<String> stemmingTerms = new ArrayList<String>();

            TokenStream stream = analyzer.tokenStream(field, new StringReader(term));
            Token t;
            while((t=stream.next()) != null)
            {
                stemmingTerms.add(t.termText());
            }
            docTermInFields.put(field,stemmingTerms);
        }
        return docTermInFields;
    }

    /**
     * @param doc
     * @param analyzer
     * @param field
     * @author Jorge Machado New Method LGTE
     */
    private static QueryTermVector getQueryTermVector(Document doc, Analyzer analyzer, String field)
    {
        StringBuffer docTxtBuffer = new StringBuffer();
        String[] docTxtFlds = doc.getValues(field);
        if(docTxtFlds == null)
            logger.info("Doc " + doc.get("id") + " have zero fields: " + field);
        else
        {
            for (String docTxtFld : docTxtFlds)
            {
//            docTxtBuffer.append(docTxtFld.replace(":", " ").replace("-", " ")).append(" ");
                docTxtBuffer.append(docTxtFld.replace(":", " ")).append(" ");
            }
        }
        return new QueryTermVector(docTxtBuffer.toString(), analyzer, field);

    }

    /**
     * Sets boost of terms. boost = weight = factor(tf*idf)
     *
     * @param termVector
     */
    public Vector<TermQuery> setBoost(QueryTermVector termVector, float factor)
            throws IOException
    {
        Vector<QueryTermVector> v = new Vector<QueryTermVector>();
        v.add(termVector);
        return setBoost(v, factor, 0);
    }

    /**
     * Sets boost of terms. boost = weight = factor(tf*idf)
     *
     * @param docsTerms
     * @param factor    - adjustment factor ( ex. alpha or beta )
     */

    // JORGE MACHADO - Para Calcular a Entropia aqui
    public Vector<TermQuery> setBoost(Vector<QueryTermVector> docsTerms,
                                      float factor, float decayFactor) throws IOException
    {
        Vector<TermQuery> terms = new Vector<TermQuery>();

        // setBoost for each of the terms of each of the docs
        for (int g = 0; g < docsTerms.size(); g++)
        {
            QueryTermVector docTerms = docsTerms.elementAt(g);
            String field = docTerms.getField();
            String[] termsTxt = docTerms.getTerms();
            int[] termFrequencies = docTerms.getTermFrequencies();

            // Increase decay
            float decay = decayFactor * g;

            // Populate terms: with TermQuries and set boost
            for (int i = 0; i < docTerms.size(); i++)
            {
                // Create Term
                String termTxt = termsTxt[i];
                Term term = new Term(field, termTxt);
                TermQuery termQuery = new TermQuery(term);
                setBoost(termQuery,termFrequencies[i],factor,decay);
                terms.add(termQuery);
            }
        }
        // Get rid of duplicates by merging termQueries with equal terms
        merge(terms);

        return terms;
    }

    //set boost in one term query
    private void setBoost(TermQuery termQuery, int tf, float factor, float decay) throws IOException
    {
        List<String> boostFields = PropertiesUtil.getListPropertiesSuffix(prop.getQueryProperties(), "field.boost.");
        //Experimental Ranking Model 1
//        if(boostFields.size() > 0)
//        {
//            String term = termQuery.getTerm().text();
//            Map<String,List<String>> termProjections = getDocumentTermInFields(term,boostFields);
//
//            float sum = 0;
//            for (String field : boostFields)
//            {
//                List<String> termsField = termProjections.get(field);
//                if(termsField.size() > 0)
//                {
//                    float relativeWeight = 1 / termsField.size();
//                    for(String projectedTerm: termsField)
//                    {
//                        float fieldFactor = PropertiesUtil.getFloatProperty(prop,"field.boost." + field);
//                        Term t = new Term(field,projectedTerm);
//                        float idf = similarity.idf(t, searcher);
//                        float weight = (1.0f * tf) * idf;
//                        weight = weight - (weight * decay);
//                        sum += factor * relativeWeight * fieldFactor * weight;
//                    }
//                }
//            }
//            termQuery.setBoost(sum);
//        }
//        else
//        {
        String fieldBoostStr = prop.getProperty("field.boost." + termQuery.getTerm().field());
        float fieldFactor = 1.0f;
        if(fieldBoostStr != null && !fieldBoostStr.equals("field.boost." + termQuery.getTerm().field()))
            fieldFactor = Float.parseFloat(fieldBoostStr);
        float idf = similarity.idf(termQuery.getTerm(), searcher);
        float weight = (1.0f * tf) * idf;
        weight = weight - (weight * decay);
        termQuery.setBoost(factor * weight * fieldFactor);
//        }
        //				if(prop.getProperty("qe.field.boosted.termweight").equals("combineModelAndBoost"))
//				{
//					String boostQE = prop.getProperty("field.boost.qe." + field );
//					String boost = prop.getProperty( "field.boost." + field );
//					float boostF;
//					if(boostQE == null)
//						boostQE = boost;
//					if(boostQE == null)
//						boostQE = "1.0f";
//				 	weight = Float.parseFloat(boostQE) * tf * idf;
//				}
//				else
    }

    /**
     * Gets rid of duplicates by merging termQueries with equal terms
     *
     * @param terms
     */
    private void merge(Vector<TermQuery> terms)
    {
        for (int i = 0; i < terms.size(); i++)
        {
            TermQuery term = terms.elementAt(i);
            // Itterate through terms and if term is equal then merge: add the
            // boost; and delete the term
            for (int j = i + 1; j < terms.size(); j++)
            {
                TermQuery tmpTerm = terms.elementAt(j);
                // If equal then merge
                if (tmpTerm.getTerm().field().equals(term.getTerm().field()) && tmpTerm.getTerm().text().equals(term.getTerm().text()))
                {
                    // Add boost factors of terms
                    term.setBoost(term.getBoost() + tmpTerm.getBoost());
                    // delete uncessary term
                    terms.remove(j);
                    // decrement j so that term is not skipped
                    j--;
                }
            }
        }
    }

    /**
     * combine weights according to expansion formula
     */
    public Vector<TermQuery> combine(Vector<TermQuery> queryTerms,
                                     Vector<TermQuery> docsTerms)
    {
        Vector<TermQuery> terms = new Vector<TermQuery>();
        // Add Terms from the docsTerms
        terms.addAll(docsTerms);
        // Add Terms from queryTerms: if term already exists just increment its
        // boost
        for (int i = 0; i < queryTerms.size(); i++)
        {
            TermQuery qTerm = queryTerms.elementAt(i);
            TermQuery term = find(qTerm, terms);
            // Term already exists update its boost
            if (term != null)
            {
                float weight = qTerm.getBoost() + term.getBoost();
                term.setBoost(weight);
            }
            // Term does not exist; add it
            else
            {
                terms.add(qTerm);
            }
        }

        return terms;
    }

    /**
     * Finds term that is equal
     *
     * @return term; if not found -> null
     */
    public TermQuery find(TermQuery term, Vector<TermQuery> terms)
    {
        TermQuery termF = null;

        Iterator<TermQuery> iterator = terms.iterator();
        while (iterator.hasNext())
        {
            TermQuery currentTerm = iterator.next();
            if (term.getTerm().field().equals(currentTerm.getTerm().field()) && term.getTerm().text().equals(currentTerm.getTerm().text()))
            {
                termF = currentTerm;
                logger.finest("Term Found: " + term);
            }
        }

        return termF;
    }

    /**
     * Returns <code> QueryExpansion.TERM_NUM_FLD </code> expanded terms from
     * the most recent level1query
     *
     * @return
     */
    public Vector<TermQuery> getExpandedTerms()
    {
        int termNum = Integer.valueOf(
                prop.getProperty(QueryExpansion.TERM_NUM_FLD)).intValue();
        Vector<TermQuery> terms = new Vector<TermQuery>();

        // Return only necessary number of terms
        List<TermQuery> list;
        if (this.expandedTerms.size() > termNum)
            list = this.expandedTerms.subList(0, termNum);
        else
            list = this.expandedTerms.subList(0, this.expandedTerms.size());

        terms.addAll(list);

        return terms;
    }

    private void setExpandedTerms(Vector<TermQuery> expandedTerms)
    {
        this.expandedTerms = expandedTerms;
    }

}
