package ru.mtt.rservice.widgets;

import java.awt.Color;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.core.MAPIServiceHandler;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.IWidgetCommand;
import ru.mtt.webapi.utils.XUtils;

/**
 *  Registry Service Diagnostic web-page constucting widget
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */

public class StatusPageWidgetCommand  implements IWidgetCommand {
    
    Logger logger = Logger.getLogger(StatusPageWidgetCommand.class);
    String[] cols = new String[] {"Red","Blue","Green","Pink","Magenta"};
    Color[]  colss = new Color[] {Color.red, Color.blue, Color.green, Color.pink, Color.magenta};
    boolean useFake = false;
    int w = 600;
    int h = 400;
    String statisticsPane = "";
    
    public StatusPageWidgetCommand() {
           super();
    }
   
    public String compose_dhtml5_diagr (int w, int h, SortedMap<Long,Double> D1, String styleColor, double MYVAL) {

          String s = "context.strokeStyle='" + styleColor + "';\n";
          s += "context.beginPath();\n";

          Collection <Long> ls = D1.keySet();
          boolean start = false;
          Long mx = 0L;
          Long mn = 0L;
          for (Long x: ls) {
              if (!start) {
               mn = x;
               mx = x;
               start = true;   
              } else {
               if (x.longValue()<mn) mn = x;
               if (x.longValue()>mx) mx = x;
              }
          }
 
          double DDT = mx-mn;

          double dx = w/DDT; 
          double dy = h/MYVAL;
          start = false;
          double x0 = 0; 
          double y0 = 0;
          double xs;   
          double ys;   
          for (Long x: ls) {
               
              Double val = D1.get (x);              
              xs = (x-mn)*dx;    
              ys = h - val*dy;    
              int x1 = (int)(xs - 2);
              int y1 = (int)(ys - 2);
              int x2 = (int)(xs + 2);
              int y2 = (int)(ys + 2);
              s+="context.strokeRect("+x1+","+y1+","+4+","+4+");\n";
                  
              if (!start) {
                  start = true;
              } else {
                  s+="context.moveTo("+(int)x0+","+(int)y0+");\n";
                  s+="context.lineTo("+(int)xs+","+(int)ys+");\n";
              }
              
              x0 = xs;
              y0 = ys;
                 
          }

          s+="context.stroke();\n";
          s+="context.closePath();\n";
    
          return s;
    
    };
   
   
    public void execute(Map<String, List<String>> pars) {
        
           List<String> xs1 =  pars.get ("WAPIId");
           List<String> xss =  pars.get ("WAPIIds");
           List<String> modes =  pars.get ("MODE");
           String mode = "GRAPH";
           List<String> Ds1 =  pars.get ("DATESTART");
           List<String> Ds2 =  pars.get ("DATEEND");
           Date d1 = null;
           Date d2 = null;
      
           if (Ds2 != null) {
            
               String xs = Ds2.get (0);
               if (!XUtils.isEmpty(xs)) {
               try {
               
               d2 = IConstants.frmt.parse(xs);
               
               } catch (Throwable ee) {
                 logger.info(ee.getMessage());    
               }
               }
           }

           if (d2 == null) d2 = new Date();

           if (Ds1 != null) {

               String xs = Ds1.get (0);
               if (!XUtils.isEmpty(xs)) {
               try {
               
               d1 = IConstants.frmt.parse(xs);
               
               } catch (Throwable ee) {
                 logger.info(ee.getMessage());    
               }
               }
           }
           
           
           if (d1 == null)  {
               
               Calendar c = Calendar.getInstance();
               c.setTime(new Date());
               c.add(Calendar.SECOND,(int)(0-IConstants._TERM));
               d1 = c.getTime();
           
           }

           double ms1 = 0.0;
           double ms2 = 0.0;
           
           if (modes != null && modes.size()>0) mode = modes.get(0).trim();
           
           logger.debug("Type of panel:    " + mode);
           
           statisticsPane = "";
           String method = null;
           String diagr = "No Data";
           String diagr1 = null;
           String diagr2 = null;
           String draw_s1 = "// draw for Frequency diagramma.\n";
           String draw_s2 = "// draw for Medium Time diagramma.\n";
           
           List<String> meth = pars.get("method");
           if (meth != null && meth.size()>0 && meth.get(0).trim().length()>0) {
               method = meth.get(0).trim();
           }
           
           String ps = null;
           String tbl = null;
           
           if (method != null) { 
               
           if (xs1 != null && xs1.size()>0 && xs1.get(0).trim().length()>0) {
               
               String hst = xs1.get(0).trim();
               SortedMap<Long,Double> D1 = getDataGramma (method, hst, IConstants._FREQ);
               SortedMap<Long,Double> D2 = getDataGramma (method, hst, IConstants._RESPT);
               
               if ("TBL".equals(mode)) {
               tbl = toTABUL(method, IConstants._FREQ, D1, IConstants._RESPT, D2, d1, d2, hst);
               }
               
               draw_s1 += compose_dhtml5_diagr (w,h, D1, "rgb(100,100,100)",IConstants._FREQ_MAX);
               draw_s2 += compose_dhtml5_diagr (w,h, D2, "rgb(100,100,100)",IConstants._RESPT_MAX);
               ps = "There&nbsp;is&nbsp;the&nbsp;Chart&nbsp;for&nbsp;" + xs1.get(0) + "&nbsp;";
               
           } else if (xss != null && xss.size()>0) {
               
               int ix = 0;
               
               HashMap<String, SortedMap<Long,Double>> data_block = new HashMap<String, SortedMap<Long,Double>>();
               
               
               for (String x: xss) {

                   SortedMap<Long,Double> D1 = getDataGramma (method, x, IConstants._FREQ);
                   SortedMap<Long,Double> D2 = getDataGramma (method, x, IConstants._RESPT);
                   data_block.put (x+"_"+IConstants._FREQ, D1);
                   data_block.put (x+"_"+IConstants._RESPT, D2);
                   
                   Collection <Double> xss1  = D1.values();
                   Collection <Double> xss2  = D2.values();
                   
                   for (Double vi: xss1) {
                        if (ms1 < vi) ms1 = vi;                 
                   }

                   for (Double vi: xss2) {
                        if (ms2 < vi) ms2 = vi;                 
                   }
               
               }           
               
               if (ms1 == 0.0) ms1 = IConstants._FREQ_MAX;
               if (ms2 == 0.0) ms2 = IConstants._RESPT_MAX;

               for (String x: xss) {

                   SortedMap<Long,Double> D1 = data_block.get(x+"_"+IConstants._FREQ);  
                   SortedMap<Long,Double> D2 = data_block.get(x+"_"+IConstants._RESPT);  
                   
                   
                   if ("TBL".equals(mode)) {
                       if (tbl==null) {    
                           tbl = "";
                       }
                       tbl += toTABUL(method,IConstants._FREQ, D1, IConstants._RESPT, D2, d1, d2, x);
                   }
                   
                   String rgb = "rgb("+colss[ix].getRed()+","+colss[ix].getGreen()+","+colss[ix].getBlue()+")";

                   draw_s1 += compose_dhtml5_diagr (w, h, D1, rgb, ms1);
                   draw_s2 += compose_dhtml5_diagr (w, h, D2, rgb, ms2);

                   if (ps == null) {
                       
                       ps = "<table><tr><td>There&nbsp;is&nbsp;the&nbsp;Chart&nbsp;for:&nbsp;</td>\n";
                       
                   }
                   
                   ps += "<td bgcolor=\""+cols[ix]+"\">&nbsp;&nbsp;</td><td>"+x+"</td>"+"<td>&nbsp;</td>\n";                     
                   ix++;
               
               }
               
               } else {
                
               }
               
               diagr1 = "<canvas style=\"visibility:visible;display:block;\" id=\"BI_001\" width="+w+" height="+h+" >\n";
               diagr1+="</canvas>\n";
               diagr2 = "<canvas style=\"visibility:hidden;display:none;\" id=\"BI_002\" width="+w+" height="+h+" >\n";
               diagr2+="</canvas>\n";

               diagr1+="<script language=\"JavaScript\">\n";
               diagr1+="var canvas = document.getElementById(\"BI_001\");\n" + 
                       "var context = canvas.getContext(\"2d\");\n"+
                       "context.rect(2,2,"+(w-4)+","+(h-4)+");\n"+  
                        draw_s1+
                       "context.strokeRect(2,2,"+(w-4)+","+(h-4)+");\n"+
                       "context.font = 'Bold 16px Sans-Serif';\n"+
                       "context.strokeStyle='rgb(125,100,142)';\n" +
                       "context.strokeText('Freq: CPMin/"+method+"', 40, 50);\n";
             
               String pos_max = "context.font = 'Bold 10px Sans-Serif';\n"+
                                "context.strokeStyle='rgb(155,120,145)';\n" +
                                "context.strokeText('"+ms1+"',"+ (w-40)+", 16);\n";
               
               String pos_min = "context.font = 'Bold 10px Sans-Serif';\n"+
                                "context.strokeStyle='rgb(155,120,145)';\n" +
                                "context.strokeText('0.0',"+(w-40)+","+(h-16)+");\n";
                  
               diagr1+=pos_max;
               diagr1+=pos_min;
                         
               diagr1+="</script>\n";
            
               diagr2+="<script language=\"JavaScript\">\n";
               diagr2+="var canvas = document.getElementById(\"BI_002\");\n" + 
                       "var context = canvas.getContext(\"2d\");\n"+
                       "context.rect(2,2,"+(w-4)+","+(h-4)+");\n"+  
                        draw_s2 + 
                       "context.strokeStyle='rgb(255,120,145)';\n" +
                       "context.strokeRect(2,2,"+(w-4)+","+(h-4)+");\n"+
                       "context.font = 'Bold 16px Sans-Serif';\n"+
                       "context.strokeStyle='rgb(255,120,145)';\n" +
                       "context.strokeText('MRT: MediumResp.Time/"+method+"', 40, 50);\n";
                         
               pos_max = "context.font = 'Bold 10px Sans-Serif';\n"+
                         "context.strokeStyle='rgb(155,120,145)';\n" +
                         "context.strokeText('"+ms2+"', "+(w-40)+",16);\n";

               pos_min = "context.font = 'Bold 10px Sans-Serif';\n"+
                         "context.strokeStyle='rgb(155,120,145)';\n" +
                         "context.strokeText('0.0',"+(w-40)+","+(h-16)+");\n";

                         
               diagr2+=pos_max;
               diagr2+=pos_min;
               
               diagr2+="</script>\n";
               
               if (ps != null) ps += "</table>";
               
               String bookmarklist = "<table><tr>" +
               "<td><td id=\"icon01\" style=\"background-color:'CornSilk';\">&nbsp;&nbsp;<a href=\"javascript:cleanColor('icon02');fillColor('icon01');displayOff('BI_002');displayOn('BI_001');\">"+IConstants._FREQ+"&nbsp;&nbsp;</a></td>" +
               "<td id=\"icon02\">&nbsp;&nbsp;<a href=\"javascript:cleanColor('icon01');fillColor('icon02');displayOff('BI_001');displayOn('BI_002');\">"+IConstants._RESPT+"</a>&nbsp;&nbsp;</td>" +
               "</tr></table>";
    
               logger.debug(tbl);
               
               if ("TBL".equals(mode)) {
                   if (tbl != null) {
                   statisticsPane = tbl;
                   }
               } else  {
                   if (ps != null) {    
                   statisticsPane = diagr1 + diagr2 + bookmarklist + ps + " ";
                   }
               }
           
           
           
           }
               
        
         
    }

    SortedMap<Long,Double> getDataGramma (String method, String mapiId, String metric) {
        
                           SortedMap<Long,Double> ds = new TreeMap<Long,Double> ();
        
                           MAPIServiceFarmHandler m = MAPIServiceFarmHandler.getInstance();
                           MAPIServiceHandler s = m.getMAPIServiceHandler(mapiId);
                           Date d1 = null;
                           Date d2 = null;
                           
                           if (!useFake) {
                           
                           ds  = s.getData(metric, method, d1, d2);
                           
                           
                           } else {
                               
                           ds.put (1000L, 220.0);
                           ds.put (2000L, 120.0);
                           ds.put (3000L, 340.0);
                           ds.put (4000L, 230.0);
                           ds.put (5000L, 120.0);
                           ds.put (6000L, 139.0);
                           ds.put (7000L, 110.0);
                           ds.put (8000L, 85.0);
                           ds.put (9000L, 300.0);
                               
                           }
                           
                           
                           return ds;
        
    }
    

    double getAvFactor (String mapiId) {
        
                           double ds = 0.0;
        
                           MAPIServiceFarmHandler m = MAPIServiceFarmHandler.getInstance();
                           MAPIServiceHandler s = m.getMAPIServiceHandler(mapiId);
                           
                           if (!useFake) {
                           ds  = s.getAvailabilityFactor();
                           
                           logger.debug (mapiId+"  Test:  "+ds);
                           
                           } else {
                           
                           }
                           
                           
                           return ds;
        
    }

    
    public String print () {
        
           String s = "<html><body>" +
                      "<center><h1>WebApi.&nbsp;Service&nbsp;Farm&nbsp;management&nbsp;Console.</h1>\n" +
                      
                      "<script language=\"JavaScript\">\n" +
          
                      "function displayOff(iddiv) { \n" +
                      "         document.getElementById(iddiv).style.visibility='hidden';\n" +
                      "         document.getElementById(iddiv).style.display='none';\n" +
                      "}\n" +
               
               
                      "function cleanColor(iddiv) { \n" + 
                      "         document.getElementById(iddiv).style.backgroundColor='White'; \n" + 
                      "}\n" +
                      
                      "function fillColor(iddiv) { \n" + 
                      "         document.getElementById(iddiv).style.backgroundColor='CornSilk'; \n" + 
                      "}\n" +
                      
               
                      "function displayOn(iddiv) { \n" +
                      "         document.getElementById(iddiv).style.visibility='visible';\n" +
                      "         document.getElementById(iddiv).style.display='block';\n" +
                      "}\n" +
        
          
                      "function loadstatistic(sId) {\n" +
                      "         document.forms['jmapi'].WAPIId.value=sId;\n"+
                      "         document.forms['jmapi'].submit();\n"+
                      "}\n\n"+

                      "function loadAsTABUL() {\n" +
                      "         document.forms['jmapi'].MODE.value='TBL';\n"+
                      "         document.forms['jmapi'].submit();\n"+
                      "}\n\n"+

                      "</script>\n" +
                      
                      "<form name=\"jmapi\">" +
                      "<input type=\"hidden\" name=\"MODE\" value=\"GRAPH\" >\n" +
                      "<input type=\"hidden\" name=\"WAPIId\" >\n" +
                      "<table><tr><th>MapiServiceId</th><th>Statistics</th></tr><tr><td>\n";
             
                      MAPIServiceFarmHandler vs  =  MAPIServiceFarmHandler.getInstance();
                      ConcurrentHashMap<String, MAPIServiceHandler>  mapis = vs.getMapis();
                      Set<Map.Entry<String, MAPIServiceHandler>> xxss  =  mapis.entrySet();
                      double af = 0.0;

                      s += "<table>\n";
                      for (Map.Entry<String, MAPIServiceHandler> x: xxss) {
                          
                          String sId = x.getKey();
                          af = getAvFactor (sId); 
                          MAPIServiceHandler sHdl = x.getValue();
                          if (sHdl.isEnabled()) {
                              s+="<tr><td><input type=\"checkbox\" name=\"WAPIIds\" value=\""+sId+"\"/></td><td><a href=\"javascript:loadstatistic('"+sId+"');\">"+sId+"(AF:"+af+")</a></td></tr>\n"; 
                          }
                          
                      }

                      s += "</table></td><td><div id=\"chart\"/>"+statisticsPane+"</td></tr>\n";
                      
                      String [] ms = MAPIServiceHandler.getMethods();
            
                      s += "<tr><td>MethodName:</td><td><select name=\"method\">\n";
                      for (String m: ms) {
                           s += "<option value=\""+m+"\">"+m+"</option>\n";
                      }
                      
                      s += "</select></td></tr>\n";
                      s += "<tr><td colspan=2><input type=\"submit\" value=\"StatisticsForChoice\"/>  </td></tr>\n";
                      s += "</table>\n";
                      s += "<br/>\n";
                      s += "<br/>\n";

                      s += "<table><tr>\n";
                      String  d1s  =  "<input type=\"textfield\" name=\"DATESTART\" />\n";
                      String  d2s  =  "<input type=\"textfield\" name=\"DATEEND\" />\n";
                      s += "<td>Since&nbsp;(YYYY-MM-dd&nbsp;HH:mm:ss):</td> <td>"  +  d1s + "</td><td>till&nbsp;(YYYY-MM-dd&nbsp;HH:mm:ss):</td>" + "<td>"  + 
                      d2s + "</td><td>";
                      s += "<a href=\"javascript:loadAsTABUL();\">&nbsp;Load&nbsp;Statistics&nbsp;In&nbsp;Tabular&nbsp;Mode&nbsp;</a>\n";
                      s += "</table></form>\n";
                      
                      s += "</center></body></html>\n";
                      return s;          
        
    }
    
    String toTABUL(String method, String m1, SortedMap<Long,Double> D1, String m2, SortedMap<Long,Double> D2, Date d1, Date d2, String host) {
        
           String s = "<table border=1><tr><td&nbsp;colspan=2>Statistics&nbsp;report&nbsp;for&nbsp;jWebApi&nbsp;function:&nbsp;" + method +
                      "&nbsp;c&nbsp;" +IConstants.frmt.format(d1)+"&nbsp;по&nbsp;"+IConstants.frmt.format(d2)+"&nbsp;by&nbsp;node&nbsp;"+host+"</td></tr>";
                       s+="<tr><td>QuantileTime</td><td>"+m1+"</td><td>"+m2+"</td></tr>";
                  
                       Calendar c = Calendar.getInstance();
                       Set<Long>  ks1 = D1.keySet();
                       for (Long x: ks1) {
                           
                            c.setTimeInMillis(x);
                            Date d = c.getTime();
                            String xQuantileTime = IConstants.frmt.format(d);
                            Double v1 = D1.get(x);
                            Double v2 = D2.get(x);
                            s+="<tr><td>"+xQuantileTime+"</td><td>"+v1+"</td><td>"+v2+"</td></tr>";
                       
                       }
           
           s+="</table><br/>";
           return s;
    }
    
}
