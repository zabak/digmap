# Introduction #

The DIGMAP thumbnails service supports the generation of thumbnail images. The thumbnails can either be reference maps with place markers added to them, or screenshot renderings of online resources (i.e. Web page thumbnails).

When generating thumbnails from online resources, the service takes as input an URL for an image or an HTML Web page, producing as output a PNG image with a given height, weight and transparency.

# Details #

The service is packaged as a standard WAR (Web application ARchive) file. The WAR file can be deployed on most J2EE application servers by simply copying it to an appropriate location. In a typical installation, in addition to the WAR file, you will need:

  * Sun Java 2 Platform
  * Apache Jakarta Tomcat (Open Source Java Servlet / JSP application server)

It is assumed that you already know how to install Java and Tomcat, and know the basics of deploying webapps on the application server. If not, please contact your system administrator to assist you.

If installing in a local machine with the default parameters, you can procede as follows.

  * Copy the thumbnails.war file to the <Tomcat directory>/webapps directory.
  * Test the service at http://127.0.0.1:8080/nail.map

If installing in a remote machine, you will additionally require Ant 1.3 or later to deploy the service. Execute the following commands:

  * unexplode the WAR file with the application.
  * find the Ant build script in the WEB-INF directory.
  * edit the 'build.xml' file and change the required parameters:
    * the location of the application server
    * other application specific parameters (i.e. Google Maps keys)
  * also edit WEB-INF/classes/htmlthumbnail.properties in a text editor
  * execute 'ant all' to compile the application and deploy it to the application server.