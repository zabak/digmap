/**
 * 
 */
package nmaf.digmap;

public enum BibDbType {
	
	ISO2709, ISO2709_Marc21, ISO2709_Marc21_UNICODE, MarcXchange, FlorenceUnimarc, MarcXml;


	public boolean isMarc21() {
		switch (this) {
		case ISO2709:
		case MarcXchange:
		case FlorenceUnimarc:
			return false;
		case ISO2709_Marc21:
		case ISO2709_Marc21_UNICODE:
		case MarcXml:
			return true;
		}
		return false;
	}
}