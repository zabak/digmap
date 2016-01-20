# Introduction #

This is a simple Web-based repository build on top of the [FeatherDB](http://www.fourspaces.com/blog/2008/4/11/FeatherDB_Java_JSON_Document_database) Java document database. It has an all HTTP/REST interface and supports querying by JavaScript (uses Java6's JavaScript support). In theory you could also search by any language that is on the JVM and implements the correct interface.

FeatherDB allows for flexibility in the backend storage and we've added support for a distributed file system that provides fail-safety through replications of files across multiple machines, as well as fast recovery from crashes (http://freshmeat.net/projects/weta-dfs/)

# Details #

# Important URIs #

GET /_all\_dbs -> list all databases
GET /_auth -> authenticate
You can authenticate via HTTP-Auth, or by passing the request parameters "username" and "password" (?username=sa&password=pass)
If anonymous access is enabled, all requests are treated as if authenticated as an administrator
GET /_invalidate -> invalidate the current credentials
GET /_sessions -> show active authenticated sessions (not in anonymous mode)
GET /_shutdown -> shutsdown the server (must be admin)
Databases
GET /{dbnme} -> db stats
PUT /{dbname} -> add a database (must be authenticated as admin)
DELETE /{dbname} -> remove a database (must be authenticated as admin)
Documents
GET /{dbname}/{documentid} -> Get the document's current revision
If this isn't found, but /db/docid/index is found, this will be returned instead
Optional parameters:
showMeta=true -> returns the meta-information for the document (see above)
showRevisions=true -> includes a list of available revisions in the meta-information
GET /{dbname}/{documentid}/{revision} -> Get the document's content (see above)
POST or PUT /{dbname}/{documentid} -> write the request's body as a new revision of the given document
If the documentid doesn't exist, it is created
POST /{dbname} -> write a new document, but use a generated id
DELETE /{dbname}/{documentid} -> delete the document (must be able to write to db)
Views
POST /{dbname}/_temp\_view -> perform an adhoc query
The contents of the POST should be in the format of a javascript function
Ex:
function(doc) { if (doc.value=='foo') {map(doc.id,doc.value); }}
POST or PUT /{dbname}/viewname/functionname -> add/update a new view.
Note: the view name must start with an underscore ('_').
The default functionname is "default"
GET /{dbname}/_all\_docs -> returns a list of all document ids
This actually calls an included view named "_all\_docs"
Views can be either written in either Java or JavaScript.  View documents are JSON documents that have the following attributes:
'view\_type': 'application/javascript' or 'java:fully.qualified.class.Name'
Java views must implement the interface: com.fourspaces.featherdb.views.View
JavaScript views are implemented as JSON documents in the format:
> {
'view\_type': 'application/javascript',_

'view1': function(doc) {
> // your code here
> if (doc.val='foo') {
> > return doc;

> } ,
'view2': function(doc) {
> // your code here
> if (doc.val='foo') {
> > map('key',doc.val);

> }
}
So, as you see, JavaScript views are functions that take a JavaScript object as input, and either returns a JSON object, or builds a map with a key and value. (See CouchDB docs for more information).
There is another associated JavaScript method that you can call, and that is: function get(id,rev,db).  From JavaScript, you can retrieve other documents by id, revisions (optional), and database (optional).
Views are maintained by a "ViewRunner".  The ViewRunner is responsible for maintaining the index of results for documents in the database.  The only included ViewRunner doesn't store an index, but instead iterates over all of the documents in the database on each request.