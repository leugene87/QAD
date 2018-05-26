<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="a00970495.ass1.quote.Quote" %>
<%@ page import="a00970495.ass1.dao.QuoteDao" %>
<%@ page import="a00970495.ass1.dataBase.DataBase" %>
<%@ page import="a00970495.ass1.dataBase.DbConstants" %>
<%@ page import="a00970495.ass1.Ass01Servlet" %>
<%@ page import="a00970495.ass1.quote.QuoteReader" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="a00970495.ass1.ApplicationException" %>
<%@ page import="a00970495.ass1.dao.QuoteDao" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.DayOfWeek" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="a00970495.ass1.cookies.CookieUtilities" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <title>Quotes Analyzer</title>
  <meta name="description" content="website description" />
  <meta name="keywords" content="website keywords, website keywords" />
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <!-- modernizr enables HTML5 elements and feature detects -->

</head>
<%!
private static Logger LOG = LogManager.getLogger();
private ResourceBundle resourceBundle;
private Locale locale;
private DateFormat dfmt;
private SimpleDateFormat sm;
private static String driver;
private static String url;
private static String user;
private static String pass;
private static final int MAX_QUOTES_TO_SHOW = QuoteReader.MAX_QUOTES_TO_READ;

double sumHighLow=0;
double aveHighLow=0;
double sumUpMove=0;
double aveUpMove=0;
double sumDownMove=0;
double aveDownMove=0;
double sumOpenClose=0;
double aveOpenClose=0;
double RatioBull=0;
double RatioBear=0;
double sumBull=0;
double sumBear=0;

DecimalFormat df = new DecimalFormat("#.####");

//@SuppressWarnings("static-access")
public void jspInit() { 
    ServletConfig config = getServletConfig();
    LOG.debug("-----------------------");
    LOG.debug("quotes.jsp: jspInit() started empty:");
}
%>
<%
response.setContentType("text/html;charset=UTF-8");
LOG.debug("-----------------------");
LOG.debug("quotes.jsp: service()");
if (request.getAttribute("language") == null){
    if (CookieUtilities.getCookie(request, "language") != null){     
            LOG.debug("cookies set");
            String userLanguage = CookieUtilities.getCookieValue(request, "language", "en");
            String userCountry = CookieUtilities.getCookieValue(request, "country", "CA");
 
            locale = new Locale(userLanguage, userCountry);
            LOG.debug("user language red from cokie: "+userLanguage);
            LOG.debug("user country red from cokie:  "+userCountry); 
   } 

    else {
            LOG.debug("cookie not yet set");
             locale = new Locale("en", "CA");
             LOG.debug("user language default: "+locale.getLanguage());
             LOG.debug("user country default:  "+locale.getCountry()); 
    
   }     

} else{
    locale = new Locale((String)request.getAttribute("language"), (String)request.getAttribute("country"));
    LOG.debug("new user language set: "+locale.getLanguage());
    LOG.debug("new user country set:  "+locale.getCountry()); 

    
}
dfmt = DateFormat.getDateInstance(DateFormat.LONG, locale);
resourceBundle = ResourceBundle.getBundle("MyResource", locale);
sm = new SimpleDateFormat("mm-dd-yyyy");

 %>
<body>
  <div id="main">
  <%
    out.println("<header>"); 
  %>
      <div id="logo">
        <div id="logo_text">
          <!-- class="logo_colour", allows you to change the colour of the text -->
          <h1><a href="index.jsp">Forex Quotes Analyzer<span class="logo_colour">_2018</span></a></h1>
          <h2><%=resourceBundle.getString("Ievgen_Lytvynenko")%></h2>
        </div>
      </div>
  <%
    out.println("<nav>"); 
  %>
        <div id="menu_container">
          <ul style='width:80%;' class="sf-menu" id="nav">
            <li style='width:130px;'><a href="index.jsp">  <%=resourceBundle.getString("Home")%>   </a></li>
            <li style='width:130px;'><a href="quotes.jsp"> <%=resourceBundle.getString("Quotes")%> </a></li>
            <li style='width:130px;'><a href="stat.jsp"> Statistics </a></li>
            <li style='width:155px;'><a href="summary.jsp"><%=resourceBundle.getString("Summary")%></a></li> 
            <li style='width:155px;'><a href="about.jsp">  <%=resourceBundle.getString("About")%>  </a></li>                 
          </ul>
          <form class="form_settings" action="Ass01_Servlet" method="post">
          <div>
                      <span>&nbsp;</span>
            <input style='width:110px;' class="submit" type="submit" name="change" value="<%=resourceBundle.getString("Change_Language")%>" alt="submit"/>
            <input type="hidden" name="language"  value="<%=resourceBundle.getLocale().getLanguage() %>"> 
            <input type="hidden" name="page"   value="quotes.jsp">  
          
            <select style='width:90px;margin-top: 10px;' id="id" name="interfLang">
            <option value="en-CA"><%=resourceBundle.getString("English") %></option>
            <option value="fr-FR"><%=resourceBundle.getString("French") %></option>
            <option value="uk-UA"><%=resourceBundle.getString("Ukrainian") %></option>
            <option value="ru-RU"><%=resourceBundle.getString("Russian") %></option>
            </select>
           

           </div>
          </form>
        </div>
  <%
    out.println("</nav>"); 
    out.println("</header>"); 
  %>
    <div id="site_content">
          <div id="sidebar_container">
        <div class="sidebar">
          <h3><%=resourceBundle.getString("Site_Renovation_Expected") %>.</h3>
          <h4><%=resourceBundle.getString("Due_To") %>:</h4>
          <h5><%=dfmt.format(sm.parse("11-28-2018")) %></h5>
          <p><%=resourceBundle.getString("Message1") %>.</p>
        </div>
        <div class="sidebar">
          <h3>Links (stock market):</h3>
          <ul>
            <li><a href="https://en.wikipedia.org/wiki/Foreign_exchange_market">What is FOREX</a></li>
            <li><a href="https://en.wikipedia.org/wiki/Stock_market">What is stock market</a></li>
            <li><a href="https://www.stocktrader.com/learn-stock-trading/">Where to learn</a></li>
          </ul>
        </div>
        <div class="sidebar">
          <h3>Links (Technical Analysis) :</h3>
          <ul>
            <li><a href="https://en.wikipedia.org/wiki/Technical_analysis">What is technical analysis</a></li>
            <li><a href="http://www.investopedia.com/university/technical/">Basics Of Technical Analysis</a></li>
            <li><a href="https://www.stocktrader.com/2009/05/18/best-stock-chart-patterns-investing-technical-analysis/">5 Great Stock Chart Patterns</a></li>
          </ul>
        </div>
      </div>
      <div class="content">
         <h1>Upload your quotes file below:</h1>    
         <div style="margin:40px">
            <form method="post" action="uploadFile" enctype="multipart/form-data">
            Select file to upload:
            <input type="file" name="uploadFile" />
            <br/><br/>
            <input type="submit" value="Upload" />
            <%
            if (request.getAttribute("message")!=null){
            	out.println("<h3><i style='color:green'>"+request.getAttribute("message")+"</i></h3>");
                request.setAttribute("message", null);
            }%>
           </form>
         </div>
         <h1><%=resourceBundle.getString("Table") %>: <b> <%=DbConstants.TABLE_NAME %></b></h1>     
         <div style="margin:40px">
          <form class="form_settings" action="Ass01_Servlet" method="post">
         <h2>Data red from file: <b> <%=QuoteDao.QUOTE_DATA_FILENAME %></b></h2>     
           
             <%
                      out.println("<input style='width:150px;' class='submit' type='submit' name='task' value='Outofill'    />");
          out.println("<tr><td colspan='2'><input style='width:150px;' class='submit' type='submit' name='task' value='Drop Table'  /></td></tr>");
          %>
            <table style="width:100%; border-spacing:0;">

              <tr>
              <th style="width:3%;">#     </th>
              <th style='width:12%;'>Date </th>
              <th style='width:6%;'>Open  </th>
              <th style='width:6%;'>High  </th>
              <th style='width:6%;'>Low   </th>
              <th style='width:6%;'>Close </th>
              <th style='width:3%;'>Volume</th>
              <th style='width:20%;' colspan="2">Action</th>
              </tr>
              
              <tr>
<%!
double aveHLBull;

private int getRatioSize(ArrayList<Quote> q, int min, int max){
	int quant=0;
		for(int i = 0; i < q.size(); i++){
		if ( (q.get(i).getCandleSize()*10000 >= min) && ( q.get(i).getCandleSize()*10000 < max) ) {
			++quant;
		}
	}
	return quant;
}

private int getRatioBody(ArrayList<Quote> q, int min, int max){
    int quant=0;
    for(int i = 0; i < q.size(); i++){
    if ( (q.get(i).getBodySize()*10000 >= min) && ( q.get(i).getBodySize()*10000 < max) ) {
        ++quant;
    }
}
    
return quant;
}
private int getRatioMoveUp(ArrayList<Quote> q, int min, int max){
    int quant=0;
    for(int i = 0; i < q.size(); i++){
    if ( (q.get(i).getUpMove()*10000 >= min) && ( q.get(i).getUpMove()*10000 < max) ) {
        ++quant;
    }
}
return quant;
}
private int getRatioMoveDown(ArrayList<Quote> q, int min, int max){
    int quant=0;
    for(int i = 0; i < q.size(); i++){
    if ( (q.get(i).getDownMove()*10000 >= min) && ( q.get(i).getDownMove()*10000 < max) ) {
        ++quant;
    }
}
return quant;
}
private int getAverageSize(ArrayList<Quote> q, String dimention){
    int sum=0;
    for(int i = 0; i < q.size(); i++){
	    switch(dimention){
	    case "Up":
	      sum+= q.get(i).getUpMove()*10000;

	    case "Down":
	        sum+= q.get(i).getDownMove()*10000;

	    case "Candle":
	        sum+= q.get(i).getCandleSize()*10000;

	    case "Body":
	        sum+= q.get(i).getBodySize()*10000;
   
	    }

	}
return sum/q.size();

}

private void setAttributesForStat(String attrSufix, ArrayList<Quote> quotesList, HttpSession session){
    session.setAttribute("tweHL"+attrSufix, df.format(getRatioSize(quotesList, 0, 20)));
    session.setAttribute("forHL"+attrSufix, df.format(getRatioSize(quotesList, 20, 40)));
    session.setAttribute("sixHL"+attrSufix, df.format(getRatioSize(quotesList, 40, 60)));
    session.setAttribute("maxHL"+attrSufix, df.format(getRatioSize(quotesList, 60, 99999)));
    
    session.setAttribute("tweBody"+attrSufix, df.format(getRatioBody(quotesList, 0, 20)));
    session.setAttribute("forBody"+attrSufix, df.format(getRatioBody(quotesList, 20, 40)));
    session.setAttribute("sixBody"+attrSufix, df.format(getRatioBody(quotesList, 40, 60)));
    session.setAttribute("maxBody"+attrSufix, df.format(getRatioBody(quotesList, 60, 9999)));
    
    session.setAttribute("tweDown"+attrSufix, df.format(getRatioMoveDown(quotesList, 0, 20)));
    session.setAttribute("forDown"+attrSufix, df.format(getRatioMoveDown(quotesList, 20, 40)));
    session.setAttribute("sixDown"+attrSufix, df.format(getRatioMoveDown(quotesList, 40, 60)));
    session.setAttribute("maxDown"+attrSufix, df.format(getRatioMoveDown(quotesList, 60, 9999)));
    
    session.setAttribute("tweUp"+attrSufix, df.format(getRatioMoveUp(quotesList, 0, 20)));
    session.setAttribute("forUp"+attrSufix, df.format(getRatioMoveUp(quotesList, 20, 40)));
    session.setAttribute("sixUp"+attrSufix, df.format(getRatioMoveUp(quotesList, 40, 60)));
    session.setAttribute("maxUp"+attrSufix, df.format(getRatioMoveUp(quotesList, 60, 9999)));
    
    
}

%>

 <%
 try {
     
     LOG.debug("quotes.jsp: filling up the table from database");
     List<Integer> indexes = new ArrayList<Integer>();
     indexes=  QuoteDao.getTheinstance().getQuoteIndexes();
     ArrayList<Quote> quotes = new ArrayList<>();
     ArrayList<Quote> bullQuotes = new ArrayList<>();
     ArrayList<Quote> bearQuotes = new ArrayList<>();

     if(indexes != null){
         for (Integer index : indexes) {
            Quote q = QuoteDao.getTheinstance().getQuoteByIndex(index);

            sumHighLow = sumHighLow + q.getCandleSize();
            //sumHighLow = Math.round(sumHighLow*10000)/10000.0d;
            sumUpMove = sumUpMove + q.getUpMove();
            sumDownMove = sumDownMove + q.getDownMove();
            sumOpenClose = q.getBodySize();
            
            if (q.getIsBearCandle()){
            sumBear++;
            bearQuotes.add(q);
            } else {
            sumBull++;
            bullQuotes.add(q);
            }
            quotes.add(q);
         }

     setAttributesForStat("Bull", bullQuotes, session);
     setAttributesForStat("Bear", bearQuotes, session);  
     
    
     session.setAttribute("aveHLBear", df.format(getAverageSize(bearQuotes, "Candle")));
     session.setAttribute("aveHLBull", df.format(getAverageSize(bullQuotes, "Candle"))); // ch
     session.setAttribute("aveUpMoveBear", df.format(getAverageSize(bearQuotes, "Up")));
     session.setAttribute("aveUpMoveBull", df.format(getAverageSize(bullQuotes, "Up")));
     session.setAttribute("aveDownMoveBear", df.format(getAverageSize(bearQuotes, "Down")));
     session.setAttribute("aveDownMoveBull", df.format(getAverageSize(bullQuotes, "Downe")));     
     session.setAttribute("aveOpenCloseBear", df.format(getAverageSize(bearQuotes, "Body")));
     session.setAttribute("aveOpenCloseBull", df.format(getAverageSize(bullQuotes, "Body")));     
     session.setAttribute("RatioBull", df.format((sumBear/quotes.size())*100));
     session.setAttribute("RatioBear", df.format((sumBull/quotes.size())*100));   
     
     System.out.print("h-l: "+df.format(aveHighLow)+" o-h: " +df.format(aveUpMove)+" h-c: "+df.format(aveDownMove)+" o-c: "+df.format(aveOpenClose)+" bea: "+df.format(RatioBull));
     
     sumHighLow = 0;
     sumUpMove = 0;
     sumDownMove = 0;
     sumOpenClose = 0;
     sumBear = 0;
     sumBull = 0;
     }

  } catch (Exception e) {
      LOG.debug(e.getMessage());
  }  

  
 
     try {
    	 
    	 LOG.debug("quotes.jsp: filling up the table from database");
    	 List<Integer> indexes = new ArrayList<Integer>();
    	 indexes=  QuoteDao.getTheinstance().getQuoteIndexes();
    	 if(indexes != null){
 		 
             for (Integer index : indexes) {
            	 if (index >= MAX_QUOTES_TO_SHOW){
            		 break;
            	 }
                Quote q = QuoteDao.getTheinstance().getQuoteByIndex(index);
                String colorD="black", colorO= "black", colorH="black", colorL= "black", colorC="black", colorV= "black";

               if((Integer)session.getAttribute("errDate") == index){
                   colorD = "red"; 
                   session.removeAttribute("errDate");
               }
               if((Integer)session.getAttribute("errOpen") == index){
                   colorO = "red"; 
                   session.removeAttribute("errOpen");
               } 
               if((Integer)session.getAttribute("errHigh") == index){
            	   colorH = "red"; 
                   session.removeAttribute("errHigh");
               } 
               if((Integer)session.getAttribute("errLow") == index){
            	   colorL = "red"; 
                   session.removeAttribute("errLow");
               } 
               if((Integer)session.getAttribute("errClose") == index){
            	   colorC = "red"; 
                   session.removeAttribute("errClose");
               } 
               if((Integer)session.getAttribute("errVolume") == index){
                   colorV = "red"; 
                   session.removeAttribute("errVolume");
               } 
               
                out.println("<tr>");  
                
                out.println("<td><input type='text' style='width:90%;'                  name='id"     +index.toString()+"' value='" + index +     "' readonly /></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorD+"'  name='Date"   +index.toString()+"' value='" +q.getDateTimeForSQL()+ "'/></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorO+"'  name='Open"   +index.toString()+"' value='" +q.getOpen()          + "'/></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorH+"'  name='High"   +index.toString()+"' value='" +q.getHigh()          + "'/></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorL+"'  name='Low"    +index.toString()+"' value='" +q.getLow()           + "'/></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorC+"'  name='Close"  +index.toString()+"' value='" +q.getClose()         + "'/></td>");
                out.println("<td><input type='text' style='width:90%;color:"+colorV+"'  name='Volume" +index.toString()+"' value='" +q.getVolume()        + "'/></td>");
                          
                out.println("<td><input type='submit'  class='submit'    name='task'   value='" + "Update" +index.toString()     + "'/></td>");
                out.println("<td><input type='submit'  class='submit'    name='task'   value='" + "Delete" +index.toString()     + "'/></td>");
                out.println("</tr>"); 
             }
    	  }
    	 
          int nextIndex =   indexes.size()+1;   

          out.println("<tr><td colspan='2'><input style='width:150px;' class='submit' type='submit' name='task' value='Outofill'    /></td></tr>");
          out.println("<tr><td colspan='2'><input style='width:150px;' class='submit' type='submit' name='task' value='Drop Table'  /></td></tr>");
      } catch (Exception e) {
    	  LOG.debug(e.getMessage());
      }     
 
 %>
      </tr>
     </table>
    </form>
    </div>
   </div>
  </div>
  <div id="scroll">
      <a title="Scroll to the top" class="top" href="#"><img src="images/top.png" alt="top" /></a>
   </div>
  <%
    out.println("<footer>"); 
  %>
    
      <p><img src="images/twitter.png" alt="twitter" />&nbsp;<img src="images/facebook.png" alt="facebook" />&nbsp;<img src="images/rss.png" alt="rss" /></p>
            <p><a href="index.jsp"><%=resourceBundle.getString("Home")%></a> | <a href="quotes.jsp"><%=resourceBundle.getString("Quotes")%></a> | 
         <a href="stat.jsp">Statistics</a> | <a href="summary.jsp"><%=resourceBundle.getString("Summary")%></a> | 
         <a href="about.jsp"><%=resourceBundle.getString("About")%></a> </p>
         <p>Copyright &copy; Quotes Analyzer | <a href="https://www.bcit.ca/study/courses/comp3613">COMP_3613 at BCIT</a></p>
      
 <%
          out.println("</footer>");  
 %>
  </div>
  <!--   javascript at the bottom for fast page loading -->

</body>
</html>
  