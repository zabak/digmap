package pt.utl.ist.lucene.treceval.geotime.queries;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Jorge Machado
 * @date 24/Jan/2010
 * @time 11:44:39
 * @email machadofisher@gmail.com
 */
public class Query
{
    String id;
    FilterChain filterChain = new FilterChain();
    String originalDesc;
    String originalNarr;
    String originalDescClean;
    String originalNarrClean;

    Times times = new Times();
    Places places = new Places();
    Terms terms = new Terms();


    public Query() {
    }

    public String getOriginalDesc() {
        return originalDesc;
    }

    public void setOriginalDesc(String originalDesc) {
        this.originalDesc = originalDesc;
    }

    public String getOriginalNarr() {
        return originalNarr;
    }

    public void setOriginalNarr(String originalNarr) {
        this.originalNarr = originalNarr;
    }

    public String getOriginalNarrClean() {
        return originalNarrClean;
    }

    public void setOriginalNarrClean(String originalNarrClean) {
        this.originalNarrClean = originalNarrClean;
    }

    public String getOriginalDescClean() {
        return originalDescClean;
    }

    public void setOriginalDescClean(String originalDescClean) {
        this.originalDescClean = originalDescClean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

    public Times getTimes() {
        return times;
    }

    public Places getPlaces() {
        return places;
    }

    public Terms getTerms() {
        return terms;
    }

    public static class FilterChain
    {
        BooleanClause booleanClause = new BooleanClause();


        private FilterChain() {
        }

        public BooleanClause getBooleanClause() {
            return booleanClause;
        }

        public static abstract class BooleanTerm
        {

        }
        public static class BooleanClause extends BooleanTerm
        {

            List<BooleanTerm> terms = new ArrayList<BooleanTerm>();
            LogicValue logicValue = LogicValue.AND;

            private BooleanClause() {
            }

            public BooleanClause createBooleanClause()
            {
                BooleanClause booleanClause = new BooleanClause();
                terms.add(booleanClause);
                return booleanClause;
            }

            public Term createTerm()
            {
                Term t = new Term();
                terms.add(t);
                return t;
            }

            public List<BooleanTerm> getTerms() {
                return terms;
            }

            public LogicValue getLogicValue() {
                return logicValue;
            }

            public void setLogicValue(LogicValue logicValue) {
                this.logicValue = logicValue;
            }

            public static class Term extends BooleanTerm
            {
                String field;
                String value;
                List<String> woeid = new ArrayList<String>();


                private Term() {

                }


                public String getField() {
                    return field;
                }

                public void setField(String field) {
                    this.field = field;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public List<String> getWoeid() {
                    return woeid;
                }

                public void setWoeid(List<String> woeid) {
                    this.woeid = woeid;
                }
            }
            public enum LogicValue
            {
                AND,
                OR
            }
        }
    }

    public static class Times
    {
        List<Term> terms = new ArrayList<Term>();

        private Times() {
        }

        public List<Term> getTerms() {
            return terms;
        }

        public static class Term
        {
            String time;

            public Term() {
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
    public static class Places
    {
        List<Term> terms = new ArrayList<Term>();

        private Places() {
        }

        public List<Term> getTerms() {
            return terms;
        }

        public static class Term
        {
            String place;
            List<String> woeid = new ArrayList<String>();


            public Term() {
            }

            public String getPlace() {
                return place;
            }

            public void setPlace(String place) {
                this.place = place;
            }

            public List<String> getWoeid() {
                return woeid;
            }

            public void setWoeid(List<String> woeid) {
                this.woeid = woeid;
            }
        }
    }

    public static class Terms
    {

        String desc;
        String narr;


        private Terms() {
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getNarr() {
            return narr;
        }

        public void setNarr(String narr) {
            this.narr = narr;
        }
    }
}
