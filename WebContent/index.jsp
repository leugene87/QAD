<%@ page import="a00970495.ass1.Ass01Servlet" %>
<%@ page import="org.apache.logging.log4j.LogManager" %>
<%@ page import="org.apache.logging.log4j.Logger" %>
<%@ page import="a00970495.ass1.ApplicationException" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="a00970495.ass1.cookies.CookieUtilities" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="a00970495.ass1.dao.QuoteDao" %>
<!DOCTYPE HTML>
<html>

<head >
  <title >Quotes Analyzer</title>
  <meta name="og:image" content="http://forextraininggroup.com/wp-content/uploads/2016/01/Japanese-Candlestick-Scetch.png"/>
  <meta name="description" content="This simple web-site allows you to analyze market quotes. This version supports only basic functions (adding, editing, deleting, updating). However, a later version will allow you to download a file with a history of quotations provided by any official quotes supplier and receive statistical information about them. This web application is written entirely in Java, HTML5 and CSS3." />
  <meta name="keywords" content="website keywords, website keywords" />
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
</head>
<%!
private static Logger LOG = LogManager.getLogger();
private ResourceBundle resourceBundle;
private Locale locale;
private DateFormat dfmt;
private SimpleDateFormat sm;
%>   
<%
response.setContentType("text/html;charset=UTF-8");
LOG.debug("-----------------------");
LOG.debug("index.jsp: service()");
if (request.getAttribute("language") == null){
    if (CookieUtilities.getCookie(request, "language") != null){     
            LOG.debug("cookie set");
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
    <header class="header-site">
      <div id="logo">
        <div id="logo_text">
          <!-- class="logo_colour", allows you to change the colour of the text -->
          <h1><a href="index.jsp">Forex Quotes Analyzer<span class="logo_colour">_2018</span></a></h1>
          <h2><%=resourceBundle.getString("Ievgen_Lytvynenko")%></h2>
        </div>
      </div>
      
      <nav>
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
            <input type="hidden" name="page"   value="index.jsp">  
          
            <select style='width:90px;margin-top: 10px;' id="id" name="interfLang">
            <option value="en-CA"><%=resourceBundle.getString("English") %></option>
            <option value="fr-FR"><%=resourceBundle.getString("French") %></option>
            <option value="uk-UA"><%=resourceBundle.getString("Ukrainian") %></option>
            <option value="ru-RU"><%=resourceBundle.getString("Russian") %></option>
            </select>
           

           </div>
          </form>
        </div>
        
      </nav>
    </header>
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
        <h1><%=resourceBundle.getString("Welcome_To_Quotes_Analyzer")%></h1>
        <p><%=resourceBundle.getString("App_Written")%> <strong>Java</strong>, <strong>HTML5</strong>, <strong>CSS3</strong>.</p>
        <h2><%=resourceBundle.getString("Browser_Compatibility")%>:</h2>
        <ul>
          <li>Internet Explorer</li>
          <li>FireFox</li>
          <li>Google Chrome</li>
          <li>Safari</li>
        </ul>
      </div>
    </div>
    <div id="scroll">
      <a title="Scroll to the top" class="top" href="#"><img src="images/top.png" alt="top" /></a>
    </div>
    <footer>
      <p><img src="images/twitter.png" alt="twitter" />&nbsp;<img src="images/facebook.png" alt="facebook" />&nbsp;<img src="images/rss.png" alt="rss" /></p>
            <p><a href="index.jsp"><%=resourceBundle.getString("Home")%></a> | <a href="quotes.jsp"><%=resourceBundle.getString("Quotes")%></a> | 
         <a href="stat.jsp">Statistics</a> | <a href="summary.jsp"><%=resourceBundle.getString("Summary")%></a> | 
         <a href="about.jsp"><%=resourceBundle.getString("About")%></a> </p>
              <p>Copyright &copy; Quotes Analyzer | <a href="https://www.bcit.ca/study/courses/comp3613">COMP_3613 at BCIT</a></p>
    </footer>
  </div>
  <!-- javascript at the bottom for fast page loading
  <script type="text/javascript" src="js/myJS.js"></script>
   -->
</body>
</html>
