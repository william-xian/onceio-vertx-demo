package cn.xian.vertxdemo.uitls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main extends JFrame {
    public static final double E = 0.0001;
    public static final int MAX_D = 6;
    public static final int TOP = -500;
    public static final int BOTTOM = 500;
    public static final int LEFT = -500;
    public static final int RIGHT = 500;

    public static void main(String[] args) {
        //show(test());
        show(q1());
        //show(q2());
    }

    public static List<E> test() {
        List<P> test = LCP(L.create(1.0000, -0.1829, -302.4922), C.create(0.0000, 0.0000, 297.5584));
        List<E> testE = new ArrayList<>();
        for (P p : test) {
            testE.add(0, p);
            testE.addAll(0, p.p);
        }
        return testE;
    }

    public static List<E> q1() {
        Set<E> source = new HashSet<>();
        source.add(P.create(100, 0));
        source.add(P.create(0, 0));
        source.add(C.create(0, 0, 100));

        Set<E> target = new HashSet<>();
        target.add(L.create(1, 0, -100));
        Set<E> r = new HashSet<>();
        Map<E, E> trace = source.stream().collect(Collectors.toMap(k -> k, v -> v));
        path(trace, target, r, new ArrayList<>(), 0);
        List<E> result = getPath(trace, r);
        List<E> es = new ArrayList<>();
        es.addAll(result);
        return es;
    }

    public static List<E> q2() {
        Set<E> source = new HashSet<>();
        source.add(P.create(100, 0));
        source.add(P.create(0, 0));
        source.add(C.create(0, 0, 100));
        Set<E> target = new HashSet<>();
        target.add(P.create(-100, 0));
        target.add(P.create(0, 100));
        target.add(P.create(0, -100));
        Map<E, E> trace = source.stream().collect(Collectors.toMap(k -> k, v -> v));
        Set<E> r = new HashSet<>();
        path(trace, target, r, new ArrayList<>(), 0);
        List<E> result = getPath(trace, r);
        List<E> es = new ArrayList<>();
        es.addAll(result);
        return es;
    }


    public static void show(List<E> es) {
        Collections.sort(es, (a, b) -> a.value() - b.value());
        Main frame = new Main();
        frame.setVisible(true);
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel() {
            private int count = 0;

            public void paintE(Graphics g, E e) {
                if (e instanceof P) {
                    P p = (P) e;
                    g.setColor(Color.RED);
                    g.fillOval((int) p.x - 3, (int) p.y - 3, 6, 6);
                } else if (e instanceof L) {
                    L l = (L) e;
                    g.setColor(Color.DARK_GRAY);
                    if (Math.abs(l.b) < Main.E) {
                        int x1 = (int) -l.c;
                        int x2 = (int) -l.c;
                        int y1 = TOP;
                        int y2 = BOTTOM;
                        g.drawLine(x1, y1, x2, y2);
                    } else if (Math.abs(l.a) < Main.E) {
                        int y1 = (int) -l.c;
                        int y2 = (int) -l.c;
                        int x1 = TOP;
                        int x2 = BOTTOM;
                        g.drawLine(x1, y1, x2, y2);
                    } else {
                        int x1 = LEFT;
                        int y1 = (int) (-1 * (l.a * x1 + l.c) / l.b);
                        int x2 = RIGHT;
                        int y2 = (int) (-1 * (l.a * x2 + l.c) / l.b);
                        g.drawLine(x1, y1, x2, y2);
                    }
                } else if (e instanceof C) {
                    C c = (C) e;
                    g.setColor(Color.GREEN);
                    g.fillOval((int) c.x - 2, (int) c.y - 2, 4, 4);
                    g.drawOval((int) (c.x - c.r), (int) (c.y - c.r), (int) (2 * c.r), (int) (2 * c.r));
                }
            }

            @Override
            public void paint(Graphics g) {
                g.translate(400, 300);
                g.setColor(Color.RED);
                g.drawLine(-300, 0, 300, 0);
                g.drawLine(0, 300, 0, -300);
                g.clearRect(-300, -260 - 15, 50, 20);
                g.drawString("count:" + count, -300, -260);
                if (count < es.size()) {
                    E e = es.get(count);
                    System.out.println(e.value() + " :" + e.display());
                    paintE(g, e);
                    count++;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }

            @Override
            public void updateUI() {
                super.updateUI();

            }
        };
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        JButton btn = new JButton("Next");
        btn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.updateUI();
            }
        });
        frame.add(panel, BorderLayout.CENTER);
        frame.add(btn, BorderLayout.EAST);
    }

    public static void path(Map<E, E> trace, Set<E> target, Set<E> result, List<Set<E>> aged, int age) {
        System.out.println("path: " + age);
        /**第一步 */
        if (age == 0) {
            aged.add(trace.keySet());
        }
        List<E> cur = new ArrayList<>();
        cur.addAll(aged.get(aged.size() - 1));
        Set<E> newTrace = new HashSet<>();
        //TODO  父代优先
        for (int i = 0; i < aged.size(); i++) {
            if (i != aged.size() - 1) {
                cur.addAll(aged.get(i));
            }
            Collections.sort(cur, (a, b) -> a.value() - b.value());
            Set<P> ps = new HashSet<>();
            Set<L> ls = new HashSet<>();
            Set<C> cs = new HashSet<>();

            Set<P> op = new HashSet<>();
            Set<L> ol = new HashSet<>();
            Set<C> oc = new HashSet<>();
            for (E e : cur) {
                if (e instanceof P) {
                    op.add((P) e);
                } else if (e instanceof L) {
                    ol.add((L) e);
                } else if (e instanceof C) {
                    oc.add((C) e);
                }
            }
            PPLS(ls, op);
            PPCS(cs, op);

            LLPS(ps, ol);
            LCPS(ps, ol, oc);

            CCPS(ps, oc);
            PCLS(ls, op, oc);
            PLCS(cs, op, ol);
            PCCS(cs, op, oc);
            newTrace.addAll(ps);
            newTrace.addAll(ls);
            newTrace.addAll(cs);

            for (E e : newTrace) {
                if (target.remove(e)) {
                    result.add(e);
                }
                if (!trace.containsKey(e)) {
                    trace.put(e, e);
                }
            }
            if (target.isEmpty()) {
                break;
            }
        }
        newTrace.removeAll(trace.keySet());
        aged.add(newTrace);

        if (!target.isEmpty() && age < Main.MAX_D) {
            path(trace, target, result, aged, age + 1);
        }
    }

    static List<E> getPath(Map<E, E> trace, Set<E> target) {
        List<E> result = new ArrayList<>();
        getPath(result, trace, target);
        return result;
    }

    static void getPath(List<E> result, Map<E, E> all, Set<E> target) {
        Set<E> next = new HashSet<>();
        for (E e : target) {
            if (e.p != null && !e.p.isEmpty()) {
                next.addAll(e.p);
            }
            result.add(0, e);
        }
        if (!next.isEmpty()) {
            getPath(result, all, next);
        }
    }

    static void PLCS(Set<C> cs, Set<P> np, Set<L> nl) {
        System.out.println("PLCS " + new Date());
        for (P p : np) {
            for (L l : nl) {
                cs.addAll(PLC(p, l));
            }
        }
    }

    static void PCCS(Set<C> cs, Set<P> np, Set<C> nc) {
        System.out.println("PCCS " + new Date());
        for (P p : np) {
            for (C c : nc) {
                cs.addAll(PCC(p, c));
            }
        }
    }

    static void PPCS(Set<C> cs, Set<P> np) {
        System.out.println("PPCS " + new Date());
        P[] arr = np.toArray(new P[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                cs.addAll(PPC(arr[i], arr[j]));
            }
        }
    }

    static void PCLS(Set<L> ls, Set<P> np, Set<C> nc) {
        System.out.println("PCLS " + new Date());
        for (P p : np) {
            for (C c : nc) {
                ls.add(PCL(p, c));
            }
        }
    }

    static void PPLS(Set<L> ls, Set<P> np) {
        System.out.println("PPLS " + new Date());
        P[] arr = np.toArray(new P[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ls.add(PPL(arr[i], arr[j]));
            }
        }
    }

    static void LLPS(Set<P> ps, Set<L> nl) {
        System.out.println("LLPS " + new Date());
        L[] arr = nl.toArray(new L[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ps.addAll(LLP(arr[i], arr[j]));
            }
        }
    }

    static void LCPS(Set<P> ps, Set<L> nl, Set<C> nc) {
        System.out.println("LCPS " + new Date());
        for (L l : nl) {
            for (C c : nc) {
                ps.addAll(LCP(l, c));
            }
        }
    }

    static void CCPS(Set<P> ps, Set<C> nc) {
        System.out.println("CCPS " + new Date());
        C[] arr = nc.toArray(new C[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ps.addAll(CCP(arr[i], arr[j]));
            }
        }
    }


    static double d(P p1, P p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    static double d(P p, L l) {
        return Math.abs((l.a * p.x + l.b * p.y + l.c) / Math.sqrt(l.a * l.a + l.b * l.b));
    }

    static int gcd(int m, int n) {
        while (n != 0) {
            int rem = m % n;
            m = n;
            n = rem;
        }
        return m;
    }

    static L PPL(P p1, P p2) {
        double a = p1.y - p2.y;
        double b = p2.x - p1.x;
        if (Math.abs(a) < Main.E) {
            return L.create(0, 1, -p1.y, Arrays.asList(p1, p2));
        } else if (Math.abs(b) < Main.E) {
            return L.create(1, 0, -p1.x, Arrays.asList(p1, p2));
        } else {
            double c = p1.y * (p1.x - p2.x) - p1.x * (p1.y - p2.y);
            return L.create(a, b, c, Arrays.asList(p1, p2));
        }
    }

    static List<C> PPC(P p1, P p2) {
        double r = d(p1, p2);
        if (Math.abs(r) < Main.E) {
            return new ArrayList<>();
        }
        return Arrays.asList(C.create(p1.x, p1.y, r, Arrays.asList(p1, p2)), C.create(p2.x, p2.y, r, Arrays.asList(p1, p2)));
    }

    static List<C> PLC(P p, L l) {
        double d = d(p, l);
        if (d > Math.E) {
            return Arrays.asList(C.create(p.x, p.y, 2 * d, Arrays.asList(p, l)));
        } else {
            return new ArrayList<>();
        }
    }

    static L PCL(P p, C c) {
        return PPL(p, P.create(c.x, c.y, Arrays.asList(p, c)));
    }

    static List<C> PCC(P p, C c) {
        double d = d(p, P.create(c.x, c.y, Arrays.asList(p, c)));
        if (d >= c.r) {
            return Arrays.asList(C.create(p.x, p.y, d, Arrays.asList(p, c)));
        } else {
            return new ArrayList<>();
        }
    }

    static List<P> LLP(L l1, L l2) {
        List<P> result = new ArrayList<>();
        if (Math.abs(l1.a - l2.a) < Main.E && Math.abs(l1.a - l2.a) < Main.E) {
        } else if (Math.abs(l1.a) < Main.E) {
            double y = l1.c;
            double x = (l2.b * l1.c - l2.c) / l2.a;
            result.add(P.create(x, y, Arrays.asList(l1, l2)));
        } else {
            double y = (l2.a * l1.c - l1.a * l2.c) / (l1.a * l2.b - l2.a * l1.b);
            double x = -1f * (l1.b * y + l1.c) / l1.a;
            result.add(P.create(x, y, Arrays.asList(l1, l2)));
        }
        return result;
    }

    static List<P> LCP(L l, C c) {
        double d = d(P.create(c.x, c.y, Arrays.asList(l, c)), l);
        if (d > c.r) {
            /** 相离 */
            return new ArrayList<>();
        } else {
            /** 相交 */
            if (Math.abs(l.b) < Main.E) {
                /**颠倒坐标系 */
                L l2 = L.create(l.b, l.a, l.c, Arrays.asList(l, c));
                C c2 = C.create(c.y, c.x, c.r, Arrays.asList(l, c));
                List<P> r2 = LCP(l2, c2);
                List<P> r = new ArrayList<>(r2.size());
                /**颠倒过来*/
                for (int i = 0; i < r2.size(); i++) {
                    r.add(P.create(r2.get(i).y, r2.get(i).x, Arrays.asList(l, c)));
                }
                return r;
            } else {
                double A = l.a * l.a / (l.b * l.b) + 1;
                double B = 2 * l.a * l.c / (l.b * l.b) + 2 * l.a * c.y / l.b - 2 * c.x;
                double C = c.x * c.x + l.c * l.c / (l.b * l.b) + 2 * l.c * c.y / l.b + c.y * c.y - c.r * c.r;
                /** 相切 */
                if (Math.abs(d - c.r) < Main.E) {
                    double x1 = -1 * B / (2 * A);
                    double y1 = -1 * (l.a * x1 + l.c) / l.b;
                    return Arrays.asList(P.create(x1, y1, Arrays.asList(l, c)));
                } else {
                    double qrt = Math.sqrt(B * B / (4 * A * A) - C / A);
                    double x1 = -1f * B / (2 * A) + qrt;
                    double x2 = -1f * B / (2 * A) - qrt;
                    double y1 = -1f * (l.a * x1 + l.c) / l.b;
                    double y2 = -1f * (l.a * x2 + l.c) / l.b;
                    List<P> result = new ArrayList<>();
                    try {
                        P p1 = P.create(x1, y1, Arrays.asList(l, c));
                        P p2 = P.create(x2, y2, Arrays.asList(l, c));
                        result = Arrays.asList(p1, p2);
                    } catch (Exception e) {
                        String exp = String.format("%s,%s", l, c).replaceAll("\\(", ".create(");
                        System.err.printf("LCP(%s);\n", exp);
                        //e.printStackTrace();
                    }
                    return result;
                }
            }
        }
    }


    static List<P> CCPX(C c1, C c2, double L) {
        List<P> result = new ArrayList<>(2);
        try {
            if (Math.abs(L - c1.r - c2.r) < Main.E) {
                //外接圆
                double x = (c1.x < c2.x) ? (c1.x + c1.r) : (c2.x + c2.r);
                result.add(P.create(x, 0, Arrays.asList(c1, c2)));
            } else if (Math.abs(Math.abs(c1.r - c2.r) - L) < Main.E) {
                //内接圆
                double x;
                if (c1.r < c2.r) {
                    if (c1.x < c2.x) {
                        x = c1.x - c1.r;
                    } else {
                        x = c1.x + c1.r;
                    }
                } else {
                    if (c2.x < c1.x) {
                        x = c2.x - c2.r;
                    } else {
                        x = c2.x + c2.r;
                    }
                }
                result.add(P.create(x, 0, Arrays.asList(c1, c2)));
            } else {
                //交叉圆
                double x = (c1.r * c1.r - c2.r * c2.r + c2.x * c2.x - c1.x * c1.x) / 2 / (c2.x - c1.x);
                double delta = Math.sqrt(c1.r * c1.r - (x - c1.x) * (x - c1.x));
                double y1 = c1.y + delta;
                double y2 = c1.y - delta;
                result.add(P.create(x, y1, Arrays.asList(c1, c2)));
                result.add(P.create(x, y2, Arrays.asList(c1, c2)));
            }
        } catch (Exception e) {
            System.err.printf("CCPX(%s,%s);\n", c1, c2);
        }
        return result;
    }

    static List<P> CCP(C c1, C c2) {
        double L = d(P.create(c1.x, c1.y, Arrays.asList(c1, c2)), P.create(c2.x, c2.y, Arrays.asList(c1, c2)));
        if (L > (c1.r + c2.r) || L < Math.abs(c1.r - c2.r)) {
            return new ArrayList<>();
        } else {
            double dy = c2.y - c1.y;
            double dx = c2.x - c1.x;
            if (Math.abs(dy) < Main.E) {
                return CCPX(c1, c2, L);
            } else {
                double sin = dy / L;
                double cos = dx / L;
                rotationCN(c1, sin, cos);
                rotationCN(c2, sin, cos);
                List<P> result = CCPX(c1, c2, L);
                rotationC(c1, sin, cos);
                rotationC(c2, sin, cos);
                for (P p : result) {
                    rotationP(p, sin, cos);
                }
                return result;
            }
        }
    }

    static void rotationC(C p, double sin, double cos) {
        double x = p.x * cos - p.y * sin;
        double y = p.x * sin + p.y * cos;
        p.x = x;
        p.y = y;
    }

    static void rotationCN(C p, double sin, double cos) {
        double x = p.x * cos + p.y * sin;
        double y = -p.x * sin + p.y * cos;
        p.x = x;
        p.y = y;
    }

    static void rotationP(P p, double sin, double cos) {
        double x = p.x * cos - p.y * sin;
        double y = p.x * sin + p.y * cos;
        p.x = x;
        p.y = y;
    }

    static void rotationPN(P p, double sin, double cos) {
        double x = p.x * cos + p.y * sin;
        double y = -p.x * sin + p.y * cos;
        p.x = x;
        p.y = y;
    }

}

abstract class E {
    public List<E> p;

    int value() {
        if (p == null || p.isEmpty()) {
            return 0;
        } else {
            int max = 0;
            for (E e : p) {
                int v = e.value();
                if (v > max) {
                    max = v;
                }
            }
            return max + 1;
        }
    }

    public abstract String toString();

    public String display() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (E pa : p) {
            result.append(pa.toString());
        }
        result.append(">");
        result.append(toString());
        result.append("]");
        return result.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        E e = (E) obj;
        return toString().equals(e.toString());
    }
}

class P extends E {
    public double x;
    public double y;

    private P(double x, double y, List<E> p) {
        this.x = x;
        this.y = y;
        this.p = p;
        String s = this.toString();
        String nan = String.format("%f", Double.NaN);
        String nInf = String.format("%f", Double.NEGATIVE_INFINITY);
        String pInf = String.format("%f", Double.POSITIVE_INFINITY);
        if (s.contains(nan) || s.contains(nInf) || s.contains(pInf)) {
            throw new RuntimeException();
        }
    }

    public static P create(double x, double y) {
        return create(x, y, new ArrayList<>());
    }

    public static P create(double x, double y, List<E> p) {
        P r = new P(x, y, p);
        return r;
    }

    public static P ref(double x, double y) {
        P c = new P(x, y, new ArrayList<>());
        return c;
    }

    @Override
    public String toString() {
        if (Math.abs(x) < Main.E) {
            x = 0;
        }
        if (Math.abs(y) < Main.E) {
            y = 0;
        }
        return String.format("P(%.4f,%.4f)", x, y);
    }
}

class L extends E {
    public double a;
    public double b;
    public double c;

    public static L create(double a, double b, double c) {
        return create(a, b, c, new ArrayList<>());
    }

    public static L create(double a, double b, double c, List<E> p) {
        L l = new L(a, b, c, p);
        return l;
    }

    public static L ref(double a, double b, double c) {
        L l = new L(a, b, c, new ArrayList<>());
        return l;
    }

    private L(double a, double b, double c, List<E> p) {
        if (Math.abs(a) < Main.E) {
            this.a = 0;
            this.b = 1;
            this.c = c;
        } else if (Math.abs(b) < Main.E) {
            this.a = 1;
            this.b = 0;
            this.c = c;
        } else {
            this.a = 1;
            this.b = b / a;
            this.c = c / a;
        }
        this.p = p;
    }

    @Override
    public String toString() {
        if (Math.abs(a) < Main.E) {
            a = 0;
        }
        if (Math.abs(b) < Main.E) {
            b = 0;
        }
        if (Math.abs(c) < Main.E) {
            c = 0;
        }
        return String.format("L(%.4f,%.4f,%.4f)", a, b, c);
    }

}

class C extends E {
    public double x;
    public double y;
    public double r;

    private C(double x, double y, double r, List<E> p) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.p = p;
    }

    public static C create(double x, double y, double r) {
        return create(x, y, r, new ArrayList<>());
    }

    public static C create(double x, double y, double r, List<E> p) {
        C result = new C(x, y, r, p);
        return result;
    }

    public static C ref(double x, double y, double r) {
        C c = new C(x, y, r, new ArrayList<>());
        return c;
    }

    @Override
    public String toString() {
        if (Math.abs(x) < Main.E) {
            x = 0;
        }
        if (Math.abs(y) < Main.E) {
            y = 0;
        }
        if (Math.abs(r) < Main.E) {
            r = 0;
        }
        return String.format("C(%.4f,%.4f,%.4f)", x, y, r);
    }

}