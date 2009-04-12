package nmaf.digmap;

import java.io.File;

//import nmaf.digmap.authorityfile.AuthorityFile;
import nmaf.digmap.ingest.BibliographicFile;

public class DigmapServices {

//	public AuthorityFile authorityFile;
	public BibliographicFile bibliographicFile;
	public boolean testing=false;
	

//
	public DigmapServices(File bibRepository, File autRepository) {
//		authorityFile=new AuthorityFile(autRepository);
		bibliographicFile=new BibliographicFile(bibRepository);
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
	
	
	
	private static DigmapServices instance=null;
//	private static DigmapServices instance=new DigmapServices(RepoxRestClient.testBaseUrl);

	public static void setInstance(DigmapServices serv) {
		instance=serv;
	}
	
//	public static AuthorityFile getAuthorityFile() {
//		return instance.authorityFile;
//	}
	
	public static BibliographicFile getBibliographicFile() {
		return instance.bibliographicFile;
	}
	
	public static boolean isTesting() {
		return instance.testing;
	}

	
}
