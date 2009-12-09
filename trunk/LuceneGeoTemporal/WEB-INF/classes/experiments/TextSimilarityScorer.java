package experiments;

import java.util.Iterator;
import com.wcohen.ss.AbstractStatisticalTokenDistance;
import com.wcohen.ss.PrintfFormat;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.StringWrapperIterator;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.api.Tokenizer;

public class TextSimilarityScorer extends AbstractStatisticalTokenDistance {
		
        public TextSimilarityScorer(Tokenizer tokenizer) { super(tokenizer); }

        public TextSimilarityScorer() { super(); }

        public double tf(String s, String t) {
        	return score(prepareTF(s),prepareTF(t));
        }
        
        public double idf(String s, String t) {
        	return score(prepareIDF(s),prepareIDF(t));
        }
        
        public double tfidf(String s, String t) {
        	return score(prepare(s),prepare(t));
        }
        
        public double length(String s) {
        	BagOfTokens bags = new BagOfTokens(s, tokenizer.tokenize(s));
        	return bags.size();
        }

        public double bm25(String t,String s) {
        	double numDocs = collectionSize;
        	double score = 0;
        	BagOfTokens bagt = new BagOfTokens(t, tokenizer.tokenize(t));
            BagOfTokens bags = new BagOfTokens(s, tokenizer.tokenize(s));           
            Iterator<Token> it = bagt.tokenIterator();
            double k1 = 2.0;
            double b = 0.75;
            while (it.hasNext()) {
            	Token tok = (Token)it.next();
            	Integer dfInteger = (Integer)documentFrequency.get(tok);
                double df = dfInteger==null ? 0.0 : dfInteger.intValue();            	
            	double idf = Math.log((numDocs+0.5)/(df+0.5));
            	score += idf * ( (bags.getWeight(tok) * (k1+1)) / (bags.getWeight(tok) + k1 * ((1.0-b)+b*(bags.size()/((totalTokenCount+1.0) / numDocs)))) );
            }
            return score;
        }

        public double score(com.wcohen.ss.api.StringWrapper s,StringWrapper t) {
                BagOfTokens sBag = (BagOfTokens)s;
                BagOfTokens tBag = (BagOfTokens)t;
                double sim = 0.0;
                for (Iterator i = sBag.tokenIterator(); i.hasNext(); ) {
                  Token tok = (Token)i.next();
                  if (tBag.contains(tok)) {
                                sim += sBag.getWeight(tok) * tBag.getWeight(tok);
                  }
                }
                return sim;
        }

        public StringWrapper prepare(String s) {
                BagOfTokens bag = new BagOfTokens(s, tokenizer.tokenize(s));
                double normalizer = 0.0;
                for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                        com.wcohen.ss.api.Token tok = (Token)i.next();
                        if (collectionSize>0) {
                                Integer dfInteger = (Integer)documentFrequency.get(tok);
                                // set previously unknown words to df==1, which gives them a high value
                                double df = dfInteger==null ? 1.0 : dfInteger.intValue();
                                double w = Math.log( bag.getWeight(tok) + 1) * Math.log( collectionSize/df );
                                bag.setWeight( tok, w );
                                normalizer += w*w;
                        } else {
                                bag.setWeight( tok, 1.0 );
                                normalizer += 1.0;
                        }
                }
                normalizer = Math.sqrt(normalizer);
                for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                        Token tok = (Token)i.next();
                        bag.setWeight( tok, bag.getWeight(tok)/normalizer );
                }
                return bag;
        }
        
        public StringWrapper prepareIDF(String s) {
            BagOfTokens bag = new BagOfTokens(s, tokenizer.tokenize(s));
            double normalizer = 0.0;
            for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                    com.wcohen.ss.api.Token tok = (Token)i.next();
                    if (collectionSize>0) {
                            Integer dfInteger = (Integer)documentFrequency.get(tok);
                            double df = dfInteger==null ? 1.0 : dfInteger.intValue();
                            double w = Math.log( collectionSize/df );
                            bag.setWeight( tok, w );
                            normalizer += w*w;
                    } else {
                            bag.setWeight( tok, 1.0 );
                            normalizer += 1.0;
                    }
            }
            normalizer = Math.sqrt(normalizer);
            for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                    Token tok = (Token)i.next();
                    bag.setWeight( tok, bag.getWeight(tok)/normalizer );
            }
            return bag;
        }
        
        public StringWrapper prepareTF(String s) {
            BagOfTokens bag = new BagOfTokens(s, tokenizer.tokenize(s));
            double normalizer = 0.0;
            for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                    com.wcohen.ss.api.Token tok = (Token)i.next();
                    if (collectionSize>0) {
                            double w = Math.log( bag.getWeight(tok) + 1);
                            bag.setWeight( tok, w );
                            normalizer += w*w;
                    } else {
                            bag.setWeight( tok, 1.0 );
                            normalizer += 1.0;
                    }
            }
            normalizer = Math.sqrt(normalizer);
            for (Iterator i=bag.tokenIterator(); i.hasNext(); ) {
                    Token tok = (Token)i.next();
                    bag.setWeight( tok, bag.getWeight(tok)/normalizer );
            }
            return bag;
        }

        public String explainScore(StringWrapper s, StringWrapper t) {
                BagOfTokens sBag = (BagOfTokens)s;
                BagOfTokens tBag = (BagOfTokens)t;
                StringBuffer buf = new StringBuffer("");
                PrintfFormat fmt = new PrintfFormat("%.3f");
                buf.append("Common tokens: ");
                for (Iterator i = sBag.tokenIterator(); i.hasNext(); ) {
                		Token tok = (Token)i.next();
                        if (tBag.contains(tok)) {
                                buf.append(" "+tok.getValue()+": ");
                                buf.append(fmt.sprintf(sBag.getWeight(tok)));
                                buf.append("*");
                                buf.append(fmt.sprintf(tBag.getWeight(tok)));
                        }
                }
                buf.append("\nscore = "+score(s,t));
                return buf.toString();
        }
        
        public String toString() { return "[TextSimilarityScorer]"; }

        static public void main(String[] argv) {
                doMain(new TextSimilarityScorer(), argv);
        }
}

