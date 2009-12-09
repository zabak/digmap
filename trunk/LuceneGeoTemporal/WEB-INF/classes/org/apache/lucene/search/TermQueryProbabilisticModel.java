package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;

import java.io.IOException;

import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.ModelManager;

/** A Query that matches documents containing a term.
 This may be combined with other terms with a {@link org.apache.lucene.search.BooleanQuery}.
 */
public class TermQueryProbabilisticModel extends TermQueryImpl {

    private Term term;

    private class TermWeight implements Weight {
        private Searcher searcher;
        private float value;
        private float queryWeight = 1.0f;

        public TermWeight(Searcher searcher) {
            this.searcher = searcher;
        }

        public String toString() { return "weight(" + TermQueryProbabilisticModel.this + ")"; }

        public Query getQuery() { return TermQueryProbabilisticModel.this; }
        public float getValue() { return value; }

        public float sumOfSquaredWeights() throws IOException
        {
//      idf = getSimilarity(searcher).idf(term, searcher); // compute idf
//      queryWeight = idf * getBoost();             // compute query weight
//      return queryWeight * queryWeight;           // square it
            return 1.0f;
        }

        public void normalize(float queryNorm)
        {
            queryWeight *= queryNorm;                   // normalize query weight
            value = queryWeight * getBoost();                  // idf for document
        }

        public Scorer scorer(IndexReader reader) throws IOException {
            TermDocs termDocs = reader.termDocs(term);

            Model model = ModelManager.getInstance().getModel();
            if(model == null)
            {
                String retModel = System.getProperty("RetrievalModel");
                model = Model.parse(retModel);

                if (model == null)
                {
                    System.err.println("No retrieval model selected - defaulting to VectorSpace");
                    System.setProperty("RetrievalModel", "VectorSpace");
                    model = Model.VectorSpaceModel;
                }
            }

            if (termDocs == null)
                return null;

            switch ( model ) {
                case LanguageModel: return new TermScorerLanguageModel(this, termDocs, getSimilarity(searcher), reader.norms(term.field()), reader);
                case LanguageModelHiemstra: return new TermScorerLanguageModelHiemstra(this, termDocs, getSimilarity(searcher), reader.norms(term.field()), reader);
                case DLHHypergeometricDFRModel:
                case InExpC2DFRModel :
                case InExpB2DFRModel :
                case IFB2DFRModel :
                case InL2DFRModel :
                case PL2DFRModel :
                case BB2DFRModel :
                case OkapiBM25Model :
                case BM25b:
                default:return new TermScorerDFR(this, termDocs, getSimilarity(searcher), reader.norms(term.field()), reader, model);
            }
        }

        public Explanation explain(IndexReader reader, int doc)
                throws IOException {

            Explanation result = new Explanation();
            result.setDescription("weight("+getQuery()+" in "+doc+"), product of:");

//      Explanation idfExpl =
//        new Explanation(idf, "idf(docFreq=" + searcher.docFreq(term) + ")");

            // explain query weight
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + getQuery() + "), product of:");

            Explanation boostExpl = new Explanation(getBoost(), "boost");
            if (getBoost() != 1.0f)
                queryExpl.addDetail(boostExpl);
//      queryExpl.addDetail(idfExpl);

//      Explanation queryNormExpl = new Explanation(queryNorm,"queryNorm");
//      queryExpl.addDetail(queryNormExpl);

            queryExpl.setValue(boostExpl.getValue() //*
//                         idfExpl.getValue() *
//                         queryNormExpl.getValue()
            );

            result.addDetail(queryExpl);

            // explain field weight
            String field = term.field();
            Explanation fieldExpl = new Explanation();
            fieldExpl.setDescription("fieldWeight("+term+" in "+doc+
                    "), product of:");

            Explanation tfExpl = scorer(reader).explain(doc);
            fieldExpl.addDetail(tfExpl);
//      fieldExpl.addDetail(idfExpl);

            Explanation fieldNormExpl = new Explanation();
            byte[] fieldNorms = reader.norms(field);
            float fieldNorm =
                    fieldNorms!=null ? Similarity.decodeNorm(fieldNorms[doc]) : 0.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field="+field+", doc="+doc+")");
            fieldExpl.addDetail(fieldNormExpl);

            fieldExpl.setValue(tfExpl.getValue() *
//                         idfExpl.getValue() *
                    fieldNormExpl.getValue());

            result.addDetail(fieldExpl);

            // combine them
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());

            if (queryExpl.getValue() == 1.0f)
                return fieldExpl;

            return result;
        }
    }

    /** Constructs a query for the term <code>t</code>. */
    public TermQueryProbabilisticModel(Term t) {
        term = t;
    }

    /** Returns the term of this query. */
    public Term getTerm() { return term; }

    protected Weight createWeight(Searcher searcher) {
        return new TermQueryProbabilisticModel.TermWeight(searcher);
    }

    /** Prints a user-readable version of this query. */
    public String toString(String field) {
        StringBuffer buffer = new StringBuffer();
        if (!term.field().equals(field)) {
            buffer.append(term.field());
            buffer.append(":");
        }
        buffer.append(term.text());
        if (getBoost() != 1.0f) {
            buffer.append("^");
            buffer.append(Float.toString(getBoost()));
        }
        return buffer.toString();
    }

    /** Returns true iff <code>o</code> is equal to this. */
    public boolean equals(Object o) {
        if (!(o instanceof TermQueryProbabilisticModel))
            return false;
        TermQueryProbabilisticModel other = (TermQueryProbabilisticModel)o;
        return (this.getBoost() == other.getBoost())
                && this.term.equals(other.term);
    }

    /** Returns a hash code value for this object.*/
    public int hashCode() {
        return Float.floatToIntBits(getBoost()) ^ term.hashCode();
    }

}
