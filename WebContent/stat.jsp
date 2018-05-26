<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="a00970495.ass1.quote.Quote" %>
<%@ page import="a00970495.ass1.dao.QuoteDao" %>
<%@ page import="a00970495.ass1.dataBase.DataBase" %>
<%@ page import="a00970495.ass1.Ass01Servlet" %>
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


double sumHighLow;

double medium;
double max;
double min;
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
            LOG.debug("user language red from cokies: "+userLanguage);
            LOG.debug("user country red from cokies:  "+userCountry); 
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
         
      <h1>The statistical data about last uploaded quotes file</h1>

            <table style="width:50%; border-spacing:0;">
                <tr>
                    <th class="headr" style="width:30%;">Calculated parameter     </th>
                    <th class="bulls" style='width:10%;'>Value BULLS</th>
                    <th class="bears" style='width:10%;'>Value BEARS</th>
                </tr>
               <tr>   
                    <td colspan="3"  class="top">Candle SIZE (high-low)</td>
                </tr>  
                <tr>   
                    <td>Average, pips</td>
                    <td><%=session.getAttribute("aveHLBull")%></td>
                    <td><%=session.getAttribute("aveHLBear")%></td>                    
                </tr>  
                <tr>   
                    <td>less then 20, %</td>
                    <td><%=session.getAttribute("tweHLBull")%></td>
                    <td><%=session.getAttribute("tweHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>21-40, %</td>
                    <td><%=session.getAttribute("forHLBull")%></td>
                    <td><%=session.getAttribute("forHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>41-60, %</td>
                    <td><%=session.getAttribute("sixHLBull")%></td>
                    <td><%=session.getAttribute("sixHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>61 and up, %</td>
                    <td><%=session.getAttribute("maxHLBull")%></td>
                    <td><%=session.getAttribute("maxHLBear")%></td> 
                </tr> 
                <tr>   
                    <td colspan="3"  class="top">Candle UP MOVEMENT (high-open)</td>
                </tr>  
                                <tr>   
                    <td>Average, pips</td>
                    <td><%=session.getAttribute("aveUpMoveBull")%></td>
                    <td><%=session.getAttribute("aveUpMoveBear")%></td>                    
                </tr>  
                <tr>   
                    <td>less then 20, %</td>
                    <td><%=session.getAttribute("tweUpBull")%></td>
                    <td><%=session.getAttribute("tweHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>21-40, %</td>
                    <td><%=session.getAttribute("forUpBull")%></td>
                    <td><%=session.getAttribute("forHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>41-60, %</td>
                    <td><%=session.getAttribute("sixUpBull")%></td>
                    <td><%=session.getAttribute("sixHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>61 and up, %</td>
                    <td><%=session.getAttribute("maxUpBull")%></td>
                    <td><%=session.getAttribute("maxHLBear")%></td> 
                </tr> 
                <tr>                
                <td colspan="3"  class="top">Candle DOWN MOVEMENT (high-open)</td>
                </tr>  
                                <tr>   
                    <td>Average, pips</td>
                    <td><%=session.getAttribute("aveDownMoveBull")%></td>
                    <td><%=session.getAttribute("aveDownMoveBear")%></td>                    
                </tr>  
                <tr>   
                    <td>less then 20, %</td>
                    <td><%=session.getAttribute("tweDownBull")%></td>
                    <td><%=session.getAttribute("tweHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>21-40, %</td>
                    <td><%=session.getAttribute("forDownBull")%></td>
                    <td><%=session.getAttribute("forHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>41-60, %</td>
                    <td><%=session.getAttribute("sixDownBull")%></td>
                    <td><%=session.getAttribute("sixHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>61 and up, %</td>
                    <td><%=session.getAttribute("maxDownBull")%></td>
                    <td><%=session.getAttribute("maxHLBear")%></td> 
                </tr> 
                <tr>
                <td colspan="3"  class="top">Candle BODY (high-open)</td>
                </tr>  
                                <tr>   
                    <td>Average, pips</td>
                    <td><%=session.getAttribute("aveOpenCloseBull")%></td>
                    <td><%=session.getAttribute("aveOpenCloseBear")%></td>                    
                </tr>  
                <tr>   
                    <td>less then 20, %</td>
                    <td><%=session.getAttribute("tweBodyBull")%></td>
                    <td><%=session.getAttribute("tweHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>21-40, %</td>
                    <td><%=session.getAttribute("forBodyBull")%></td>
                    <td><%=session.getAttribute("forHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>41-60, %</td>
                    <td><%=session.getAttribute("sixBodyBull")%></td>
                    <td><%=session.getAttribute("sixHLBear")%></td> 
                </tr> 
                <tr>   
                    <td>61 and up, %</td>
                    <td><%=session.getAttribute("maxBodyBull")%></td>
                    <td><%=session.getAttribute("maxHLBear")%></td> 
                </tr> 
                <tr>   
                    <td colspan="3"  class="top">CANDLES RATIO</td>
                </tr> 
                <tr>   
                    <td>Candle percentage, %</td>
                    <td><%=session.getAttribute("RatioBull")%></td>
                    <td><%=session.getAttribute("RatioBear")%></td>
                </tr>  
           </table>




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
  