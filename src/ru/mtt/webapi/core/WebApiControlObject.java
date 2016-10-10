package ru.mtt.webapi.core;

import com.google.gson.annotations.Expose;

import java.util.Map;
import java.util.Set;

/**
 * 
 * WebApi Invocation control parameters wrapper object
 * 
 * @author rnasibullin@mtt.ru
 */
public class WebApiControlObject {

        @Expose
        String cmd = null;
        @Expose String n1 = null;
        @Expose String k1 = null;
        @Expose String v1 = null;
        @Expose String n2 = null;
        @Expose String k2 = null;
        @Expose String v2 = null;
        @Expose String R = null;
        @Expose String node = null;
        @Expose String KEY = null;

        @Expose String c1 = null;
        @Expose String p1 = null;
        @Expose String c2 = null;
        @Expose String p2 = null;
        @Expose String REL = null;
        @Expose String l = null;
        @Expose String p = null;
        @Expose String v = null;

        @Expose
        Map props = null;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getN1() {
            return n1;
        }

        public void setN1(String n1) {
            this.n1 = n1;
        }

        public String getK1() {
            return k1;
        }

        public void setK1(String k1) {
            this.k1 = k1;
        }

        public String getV1() {
            return v1;
        }

        public void setV1(String v1) {
            this.v1 = v1;
        }

        public String getN2() {
            return n2;
        }

        public void setN2(String n2) {
            this.n2 = n2;
        }

        public String getK2() {
            return k2;
        }

        public void setK2(String k2) {
            this.k2 = k2;
        }

        public String getV2() {
            return v2;
        }

        public void setV2(String v2) {
            this.v2 = v2;
        }

        public String getR() {
            return R;
        }

        public void setR(String r) {
            R = r;
        }

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getKEY() {
            return KEY;
        }

        public void setKEY(String KEY) {
            this.KEY = KEY;
        }

        public String getC1() {
            return c1;
        }

        public void setC1(String c1) {
            this.c1 = c1;
        }

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public String getC2() {
            return c2;
        }

        public void setC2(String c2) {
            this.c2 = c2;
        }

        public String getP2() {
            return p2;
        }

        public void setP2(String p2) {
            this.p2 = p2;
        }

        public String getREL() {
            return REL;
        }

        public void setREL(String REL) {
            this.REL = REL;
        }

        public String getL() {
            return l;
        }

        public void setL(String l) {
            this.l = l;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public Map getProps() {
            return props;
        }

        public void setProps(Map props) {
            this.props = props;
        }

        public void complete () {

            if (props != null) {
                Set keys = getProps().keySet();
                for (Object xx: keys) {
                    if ("PID".equals(xx) || "ID".equals(xx) || "TTL".equals(xx)){
                        Object value = getProps().get(xx);
                        if (value instanceof Double) {
                            Double vx = (Double) value;
                            Long l = (long) vx.doubleValue();
                            getProps().put(xx, l);
                        }

                    }
                }
            }


        }


}
