package ru.mtt.webapi.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Utilities for XML-operations
 * 
 * 
 * @author rnasibullin@mtt.ru
 */
public class XMLUtils {

    /**
     * Получение списка чайлдов
     * @param itemName - имя подузла
     * @param n - корневой узел
     * @return список узлов
     */
       public static List<Node> getChilds (String itemName, Node n) {

              ArrayList<Node> lst = new ArrayList<Node>();
              NodeList xn = n.getChildNodes();
              int nz = xn.getLength();
              for (int i=0; i<nz; i++) {
                   Node x = xn.item(i);
                   if (x.getNodeType()==Node.TEXT_NODE || x.getNodeType()==Node.COMMENT_NODE) continue;
                   if (itemName != null && itemName.equals(x.getNodeName()))  {
                     lst.add (x);
                   } else if (itemName == null) {
                      lst.add (x);
                   }
              }
              return lst;

       }

    public static Node getTextChild (Node n) {

        NodeList xn = n.getChildNodes();
        int nz = xn.getLength();
        for (int i=0; i<nz; i++) {
            Node x = xn.item(i);
            if (x.getNodeType()==Node.TEXT_NODE) return x;
        }
        return null;

    }

    /**
     * Получение определенного аттрирбута
     * @param ns узел
     * @param nm название аттрибута
     * @return значение аттрибута
     */
     public static String getAttr(Node ns, String nm) {

            Node x = ns.getAttributes().getNamedItem(nm);
            if (x != null) return x.getNodeValue();
            else return null;

     };


    /**
     * Получение определенного аттрирбута
     * @param ns узел
     * @param nm название аттрибута
     * @return значение аттрибута
     */
      public static String getAttr(Node ns, String nm, String def) {

             Node x = ns.getAttributes().getNamedItem(nm);
             if (x != null) return x.getNodeValue();
             else return def;

      };


};
