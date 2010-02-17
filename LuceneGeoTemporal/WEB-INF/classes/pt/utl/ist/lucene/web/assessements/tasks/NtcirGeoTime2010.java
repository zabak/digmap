package pt.utl.ist.lucene.web.assessements.tasks;

import pt.utl.ist.lucene.web.assessements.dao.DBServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NtcirGeoTime2010 {
    public static void main(String[] args) throws SQLException
    {

        Connection conn = DBServer.getConnection();

        HashMap<String, String> topicsDesc = new HashMap<String, String>();
        HashMap<String, String> topicsNarr = new HashMap<String, String>();

        //meter os topicos do NTCIR
        topicsDesc.put("GeoTime-0001","When and where did Astrid Lindgren die");
        topicsNarr.put("GeoTime-0001","The user wants to know when and in what city the children's author Astrid Lindgren died.");

        topicsDesc.put("GeoTime-0002","When and where did Hurricane Katrina make landfall in the United States?");
        topicsNarr.put("GeoTime-0002","The user would like to know where and when the hurricane named Katrina, which caused extensive damage in the south-western United States, made landfall. The names of states where landfall came is acceptable, but the year alone is insufficient.");

        topicsDesc.put("GeoTime-0003","When and where did Paul Nitze die?");
        topicsNarr.put("GeoTime-0003","The user wants to know when Paul Nitze, former USA Defense Secretary died and where he died.");

        topicsDesc.put("GeoTime-0004","When and where did the SARS epidemic begin?");
        topicsNarr.put("GeoTime-0004","The user is investigating the SARS (Severe acute respiratory syndrome) epidemic caused by a new strain of virus. The user wants to know when the virus was first detected and in what province of China.");

        topicsDesc.put("GeoTime-0005","When and where did Katharine Hepburn die?");
        topicsNarr.put("GeoTime-0005","The user is investigating the actress Katharine Hepburn and wants to know when and where she died.");

        topicsDesc.put("GeoTime-0006","When and where did anti-government demonstrations occur in Uzbekistan?");
        topicsNarr.put("GeoTime-0006","The user wants to know what month and year an anti-government riot took place in Uzbekistan that was put down by military force. The user also wants to know where in Uzbekistan this took place.");

        topicsDesc.put("GeoTime-0007","How old was Max Schmeling when he died, and where did he die?");
        topicsNarr.put("GeoTime-0007","The user wants to know where the German boxer Max Schmeling died and how old he was when he died.");

        topicsDesc.put("GeoTime-0008","When and where did Chechen rebels take Russians hostage in a theatre?");
        topicsNarr.put("GeoTime-0008","The user would like to know when Chechen rebels terrorized and held hostage Russian theatre-goers. Also where did the hostage incident take place?");

        topicsDesc.put("GeoTime-0009","When and where did Rosa Parks die?");
        topicsNarr.put("GeoTime-0009","The user would like to know where and when the American civil rights activist Rosa Parks died.");

        topicsDesc.put("GeoTime-0010","When was the decision made on siting the ITER and where is it to be built?");
        topicsNarr.put("GeoTime-0010","The ITER (International Thermonuclear Experimental Reactor) is an experimental facility for conducting international joint research on the feasibility of fusion energy. When was the decision made on where to build the facility and where is it to be sited?");

        topicsDesc.put("GeoTime-0011","Describe when and where train accidents occurred which had fatalities in the period 2002 to 2005.");
        topicsNarr.put("GeoTime-0011","The user wants to know about train accidents in which people died as a result. Where did these train disasters take place and when, between 2002 and 2005?");

        topicsDesc.put("GeoTime-0012","When and where did Yasser Arafat die?");
        topicsNarr.put("GeoTime-0012","The user wants to know about Palestinian leader Yasser Arafat, in particular when he died and where.");

        topicsDesc.put("GeoTime-0013","What Portuguese colony was transferred to China and when?");
        topicsNarr.put("GeoTime-0013","Sometime in the past few years a former Portuguese colony had its sovereignty transferred to China. The user wants to know the name of the colony and when the transfer took place.");

        topicsDesc.put("GeoTime-0014","When and where did a volcano erupt in Africa during 2002?");
        topicsNarr.put("GeoTime-0014","The user would like to know the date in 2002 in which a volcano erupted in Africa. What was the name of the volcano and in which country is it located?");

        topicsDesc.put("GeoTime-0015","What American football team won the Superbowl in 2002, and where was the game played?");
        topicsNarr.put("GeoTime-0015","The user is interested in the American football Superbowl game in 2002.  In what city was it played?");

        topicsDesc.put("GeoTime-0016","When and where were the last three Winter Olympics held?");
        topicsNarr.put("GeoTime-0016","The Winter Olympics are held every four years.  In which year and in what city were the last three olympics held?");

        topicsDesc.put("GeoTime-0017","When and where was a candidate for president of a democratic South American country kidnapped by a rebel group?");
        topicsNarr.put("GeoTime-0017","The user wants to know  in which country and what date a presidential candidate in South America was kidnapped?");

        topicsDesc.put("GeoTime-0018","What date was a country was invaded by the United States in 2002?");
        topicsNarr.put("GeoTime-0018","The United States invaded another country in 2002.  The user wants to know the exact date of the invasion and what country was invaded");

        topicsDesc.put("GeoTime-0019","When and where did the funeral of Queen Elizabeth (the Queen Mother) take place?");
        topicsNarr.put("GeoTime-0019","The Queen mother, Elizabeth, of the United Kingdom died in 2002.  The user wants to know the exact date and location of her funeral");

        topicsDesc.put("GeoTime-0020","What country is the most recent to join the UN and when did it join?");
        topicsNarr.put("GeoTime-0020","A European country finally joined the United Nations.  The user wants to know which country it is and when it joined.");

        topicsDesc.put("GeoTime-0021","When and where were the 2010 Winter Olympics host city location announced?");
        topicsNarr.put("GeoTime-0021","The International Olympic Committee decides when and where the next Winter Olympics is held.  When was this announcement made for the next Winter Olympics,  and from what city was it made?");

        topicsDesc.put("GeoTime-0022","When and where did a massive earthquake occur in December 2003?");
        topicsNarr.put("GeoTime-0022","A massive earthquake occurred in December 2003.  The user would like to know when this took place and what city and country suffered this quake");

        topicsDesc.put("GeoTime-0023","When did the largest expansion of the European Union take place, and which countries became members?");
        topicsNarr.put("GeoTime-0023","The European Union added ten new member states.  Which countries were added and when did this addition take place?]");

        topicsDesc.put("GeoTime-0024","When and what country has banned cell phones?");
        topicsNarr.put("GeoTime-0024","Only one country in the world forbids mobile phones.  Which one is it and when did the ban take place?");

        topicsDesc.put("GeoTime-0025","How long after the Sumatra earthquake did the tsunami hit Sri Lanka?");
        topicsNarr.put("GeoTime-0025","The largest earthquake in recent times occurred off the coast of Sumatra in 2005.  The earthquake caused a massive tsunami which spread across the Indian Ocean.  The user would like to know how long it took the tsunami to reach Sri Lanka.  An somewhat indefinite answer like 'a few days' is acceptable.");


        for(Map.Entry<String,String> topic: topicsDesc.entrySet())
        {
            PreparedStatement ps = conn.prepareStatement("insert into topic (id_topic,description,narrative,task) values (?,?,?,'NtcirGeoTime2010')");
            ps.setString(1,topic.getKey());
            ps.setString(2,topic.getValue());
            ps.setString(3,topicsNarr.get(topic.getKey()));
            ps.execute();
            ps.close();
        }
        conn.close();
    }

}
