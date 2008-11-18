<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="eu.digmap.thumbnails.*"%>
<%@ page import="javazoom.upload.*"%>
<%
    if (MultipartFormDataRequest.isMultipartFormData(request)) {
		MultipartFormDataRequest mrequest = new MultipartFormDataRequest(request);
		Hashtable files = mrequest.getFiles();
		if ((files != null) && (!files.isEmpty())) {
			UploadFile file = (UploadFile) files.get("file");
			if (file != null) try {
				BufferedReader reader = new BufferedReader(new InputStreamReader ( file.getInpuStream() ) );
				String line = null;
				while ( (line = reader.readLine()) !=null ) try {
					String params[] = line.split("\t+");
					String url = params[0];
					int height = Integer.parseInt( params.length>1 ? params[1] : "-1");
					int width = Integer.parseInt( params.length>2 ? params[2] : "-1" );
					byte transparency = Byte.parseByte(params.length>3 ? params[3] : "255" );
					float rotation = Float.parseFloat( params.length>4 ? params[4] : "0" );
					ByteArrayOutputStream trash = new ByteArrayOutputStream();
					ThumbnailParams tparams = new ThumbnailParams(url, null, width, height, transparency, rotation);
					ThumbnailMakerFactory.getThumbnailMaker(tparams).make(true, true, trash);	
				} catch ( Exception e ) { }
			} catch ( Exception e2 ) { }
		}
	}
%>