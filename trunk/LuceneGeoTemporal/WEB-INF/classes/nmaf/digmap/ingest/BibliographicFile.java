package nmaf.digmap.ingest;

import org.dom4j.Document;
import pt.utl.ist.lucene.utils.Dom4jUtil;

import java.io.File;
import java.util.ArrayList;

public class BibliographicFile {
//	RepoxRestClient unionAutFile;	
//	ArrayList<ExternalAuthorityFile> externalAutFiles;
	
    File outDir;
	

	
	public BibliographicFile(File outDir) {
		this.outDir = outDir;
	}
	
	
	public boolean createRecord(String library, Document dom, String id, String format) throws Exception{
		if(outDir!=null) {
			File outRec=new File(outDir, library+"/"+format.toLowerCase()+"/"+id+"."+format.toLowerCase()+".xml");
			outRec.getParentFile().mkdirs();
			Dom4jUtil.write(dom, outRec);
		}
		return true;
	}
	
	public void clean(String library) {
		for(String format: new String[] {"Digmap","MarcXchange"}) {
			if(outDir!=null) {
				File[] files=new File(outDir, library+"/"+format.toLowerCase()).listFiles();
				if (files==null)
					continue;
				for(File f: files) {
					if (f.getName().endsWith(".xml"))
						f.delete();
				}
			}
		}		
	}


	public Iterable<Document> allRecords(String library, String format){
		return new IteratorXmlRecords(new File(outDir, library+"/"+format.toLowerCase()).listFiles());
	}
	
	public static String getIdFor(String libAcronym, String personId) {
		return "urn:digmap.eu:" + libAcronym + "Digmap:"+personId;
	}
	public static String getIdFromUrn(String urn) {
		return urn.substring(urn.lastIndexOf(':')+1);
	}
	
	public ArrayList<String> getCollections(){
		ArrayList<String> ret=new ArrayList<String>();
		if(outDir!=null) {
			File[] files=outDir.listFiles();
			if (files!=null) {
				for(File f: files) {
					if (f.isDirectory())
						ret.add(f.getName());
				}
			}
		}else {
			throw new RuntimeException("not imlemented");
		}
		return ret;
	}
	
	
}
