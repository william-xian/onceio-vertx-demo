package cn.xian.vertxdemo.uitls;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    public static final double E = 0.00001;
    public static final int MAX_D = 1;

    public static void main(String[] args) {
        Set<P> op = new HashSet<>();
        Set<L> ol = new HashSet<>();
        Set<C> oc = new HashSet<>();
        Set<E> target = new HashSet<>();
        op.add(new P(100, 0, new ArrayList<>()));
        oc.add(new C(0, 0, 100, new ArrayList<>()));
        target.add(new L(1, 0, -100, new ArrayList<>()));
        List<E> source = new ArrayList<>();
        source.addAll(op);
        source.addAll(ol);
        source.addAll(oc);
        source.addAll(target);
        List<E> result = path(op, ol, oc, target, new HashSet<>(), new HashSet<>(), new HashSet<>(), 0);

        for (E e : result) {
            System.out.println(e);
            if (target.contains(e)) {
                System.out.println("-----------");
            }
        }
        List<E> es = new ArrayList<>();
        es.addAll(source);
        es.addAll(result);
        Main frame = new Main();
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel() {
            private int count = 0;

            @Override
            public void paint(Graphics g) {
                g.translate(400, 300);
                g.setColor(Color.RED);
                g.drawLine(-300, 0, 300, 0);
                g.drawLine(0, 300, 0, -300);
                for (int i = 0; i < Math.min(count, es.size()); i++) {
                    E e = es.get(i);
                    if (e instanceof P) {
                        P p = (P) e;
                        g.setColor(Color.RED);
                        g.fillOval((int) p.x - 3, (int) p.y - 3, 6, 6);
                    } else if (e instanceof L) {
                        L l = (L) e;
                        g.setColor(Color.DARK_GRAY);
                        if (l.b < Main.E) {
                            int x1 = (int) -l.c;
                            int x2 = (int) -l.c;
                            int y1 = -400;
                            int y2 = 400;
                            g.drawLine(x1, y1, x2, y2);
                        } else if (l.a < Main.E) {
                            int y1 = (int) -l.c;
                            int y2 = (int) -l.c;
                            int x1 = -400;
                            int x2 = 400;
                            g.drawLine(x1, y1, x2, y2);
                        } else {
                            int x1 = -200;
                            int y1 = (int) (-1 * (l.a * x1 + l.c) / l.b);
                            int x2 = 200;
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
            }

            @Override
            public void updateUI() {
                super.updateUI();
                this.count++;

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
        frame.add(btn, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

    }

    public static List<E> path(Set<P> op, Set<L> ol, Set<C> oc, Set<E> target, Set<P> np, Set<L> nl, Set<C> nc, int age) {
        /**第一步 */
        Set<P> ps = new HashSet<>();
        Set<L> ls = new HashSet<>();
        Set<C> cs = new HashSet<>();
        if (age == 0) {
            LLPS(ps, new HashSet<>(), ol);
            LCPS(ps, new HashSet<>(), new HashSet<>(), ol, oc);
            CCPS(ps, new HashSet<>(), oc);

            PPLS(ls, new HashSet<>(), op);
            PCLS(ls, new HashSet<>(), new HashSet<>(), op, oc);
            CCLS(ls, new HashSet<>(), oc);

            PPCS(cs, new HashSet<>(), op);
            PLCS(cs, new HashSet<>(), new HashSet<>(), op, ol);
            PCCS(cs, new HashSet<>(), new HashSet<>(), op, oc);
        } else {
            LLPS(ps, ol, nl);
            LCPS(ps, ol, oc, nl, nc);
            CCPS(ps, oc, nc);

            PPLS(ls, op, np);
            PCLS(ls, op, oc, np, nc);
            CCLS(ls, oc, nc);

            PPCS(cs, op, np);
            PLCS(cs, op, ol, np, nl);
            PCCS(cs, op, oc, np, nc);
        }

        op.addAll(np);
        ol.addAll(nl);
        oc.addAll(nc);
        boolean hasAll = true;
        for (E e : target) {
            hasAll = ps.contains(e) || ls.contains(e) || oc.contains(e);
            if (!hasAll) break;
        }
        if (!hasAll && age < Main.MAX_D) {
            return path(op, ol, oc, target, ps, ls, cs, age + 1);
        } else if (hasAll) {
            List<E> result = new ArrayList<>();
            Map<E, E> all = new HashMap<>();
            for (E e : ps) {
                all.put(e, e);
            }
            for (E e : ls) {
                all.put(e, e);
            }
            for (E e : cs) {
                all.put(e, e);
            }

            getPath(result, all, target);
            for (E e : target) {
                hasAll = ps.contains(e) || ls.contains(e) || oc.contains(e);
                if (!hasAll) break;
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    static void getPath(List<E> result, Map<E, E> all, Set<E> target) {
        Set<E> next = new HashSet<>();
        for (E t : target) {
            E e = all.get(t);
            if (!result.contains(e)) {
                if (e.p != null && !e.p.isEmpty()) {
                    next.addAll(e.p);
                }
                result.add(0, e);
            }
        }
        if (!next.isEmpty()) {
            getPath(result, all, next);
        }
    }

    static void PLCS(Set<C> cs, Set<P> op, Set<L> ol, Set<P> np, Set<L> nl) {
        for (P p : np) {
            for (L l : nl) {
                cs.addAll(PLC(p, l));
            }
        }
        for (P p : op) {
            for (L l : nl) {
                cs.addAll(PLC(p, l));
            }
        }
        for (P p : np) {
            for (L l : ol) {
                cs.addAll(PLC(p, l));
            }
        }
    }

    static void PCCS(Set<C> cs, Set<P> op, Set<C> oc, Set<P> np, Set<C> nc) {
        for (P p : np) {
            for (C c : nc) {
                cs.addAll(PCC(p, c));
            }
        }
        for (P p : op) {
            for (C c : nc) {
                cs.addAll(PCC(p, c));
            }
        }
        for (P p : np) {
            for (C c : oc) {
                cs.addAll(PCC(p, c));
            }
        }
    }

    static void PPCS(Set<C> cs, Set<P> op, Set<P> np) {
        P[] arr = np.toArray(new P[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                cs.addAll(PPC(arr[i], arr[j]));
            }
        }
        for (P p1 : op) {
            for (P p2 : np) {
                cs.addAll((PPC(p1, p2)));
            }
        }
    }

    static void CCLS(Set<L> ls, Set<C> oc, Set<C> nc) {
        C[] arr = nc.toArray(new C[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ls.add(CCL(arr[i], arr[j]));
            }
        }
        for (C c1 : oc) {
            for (C c2 : nc) {
                ls.add((CCL(c1, c2)));
            }
        }
    }

    static void PCLS(Set<L> ls, Set<P> op, Set<C> oc, Set<P> np, Set<C> nc) {
        for (P p : np) {
            for (C c : nc) {
                ls.add(PCL(p, c));
            }
        }
        for (P p : op) {
            for (C c : nc) {
                ls.add(PCL(p, c));
            }
        }
        for (P p : np) {
            for (C c : oc) {
                ls.add(PCL(p, c));
            }
        }
    }

    static void PPLS(Set<L> ls, Set<P> op, Set<P> np) {
        P[] arr = np.toArray(new P[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ls.add(PPL(arr[i], arr[j]));
            }
        }
        for (P p1 : op) {
            for (P p2 : np) {
                ls.add((PPL(p1, p2)));
            }
        }
    }

    static void LLPS(Set<P> ps, Set<L> ol, Set<L> nl) {
        L[] arr = nl.toArray(new L[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ps.add(LLP(arr[i], arr[j]));
            }
        }
        for (L l1 : ol) {
            for (L l2 : nl) {
                ps.add((LLP(l1, l2)));
            }
        }
    }

    static void LCPS(Set<P> ps, Set<L> ol, Set<C> oc, Set<L> nl, Set<C> nc) {
        for (L l : nl) {
            for (C c : nc) {
                ps.addAll(LCP(l, c));
            }
        }
        for (L l : ol) {
            for (C c : nc) {
                ps.addAll(LCP(l, c));
            }
        }
        for (L l : nl) {
            for (C c : oc) {
                ps.addAll(LCP(l, c));
            }
        }
    }

    static void CCPS(Set<P> ps, Set<C> oc, Set<C> nc) {
        C[] arr = nc.toArray(new C[0]);
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                ps.addAll(CCP(arr[i], arr[j]));
            }
        }
        for (C c1 : oc) {
            for (C c2 : nc) {
                ps.addAll((CCP(c1, c2)));
            }
        }
    }


    static double d(P p1, P p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    static double d(P p, L l) {
        return (l.a * p.x + l.b * p.y + l.c) / Math.sqrt(l.a * l.a + l.b * l.b);
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
        double c = p1.y * (p1.x - p2.x) - p1.x * (p1.y - p2.y);
        return new L(a, b, c, Arrays.asList(p1, p2));
    }

    static List<C> PPC(P p1, P p2) {
        double r = d(p1, p2);
        return Arrays.asList(new C(p1.x, p1.y, r, Arrays.asList(p1, p2)), new C(p2.x, p2.y, r, Arrays.asList(p1, p2)));
    }

    static List<C> PLC(P p, L l) {
        double d = d(p, l);
        if (d > Math.E) {
            return Arrays.asList(new C(p.x, p.y, 2 * d, Arrays.asList(p, l)));
        } else {
            return new ArrayList<C>();
        }
    }

    static L PCL(P p, C c) {
        return PPL(p, new P(c.x, c.y, Arrays.asList(p, c)));
    }

    static List<C> PCC(P p, C c) {
        double d = d(p, new P(c.x, c.y, Arrays.asList(p, c)));
        if (d >= c.r) {
            return Arrays.asList(new C(p.x, p.y, d, Arrays.asList(p, c)));
        } else {
            return new ArrayList<>();
        }
    }

    static P LLP(L l1, L l2) {
        double y = (l2.a * l1.c - l1.a * l2.c) / (l1.a * l2.b - l2.a * l1.b);
        double x = -1f * (l1.b * y + l1.c) / l1.a;
        return new P(x, y, Arrays.asList(l1, l2));
    }

    static List<P> LCP(L l, C c) {
        double d = d(new P(c.x, c.y, Arrays.asList(l, c)), l);
        if (d > c.r) {
            return new ArrayList<>();
        } else {
            if (l.b == 0) {
                /**颠倒坐标系 */
                L l2 = new L(l.b, l.a, l.c, Arrays.asList(l, c));
                C c2 = new C(c.y, c.x, c.r, Arrays.asList(l, c));
                List<P> r2 = LCP(l2, c2);
                List<P> r = new ArrayList<>(r2.size());
                /**颠倒过来*/
                for (int i = 0; i < r2.size(); i++) {
                    r.add(new P(r2.get(i).y, r2.get(i).x, Arrays.asList(l, c)));
                }
                return r;
            } else {
                double A = l.a * l.a / (l.b * l.b) + 1;
                double B = 2 * l.a * l.c / (l.b * l.b) + 2 * l.a * c.y / l.b - 2 * c.x;
                double C = c.x * c.x + l.c * l.c / (l.b * l.b) + c.y * c.y - c.r * c.r;
                double qrt = Math.sqrt(B * B / (4 * A * A) - C / A);
                if (Math.abs(qrt) < Main.E) {
                    double x1 = -1 * B / (2 * A);
                    double y1 = -1 * (l.a * x1 + l.c) / l.b;
                    return Arrays.asList(new P(x1, y1, Arrays.asList(l, c)));
                } else {
                    double x1 = -1f * B / (2 * A) + qrt;
                    double x2 = -1f * B / (2 * A) - qrt;
                    double y1 = -1f * (l.a * x1 + l.c) / l.b;
                    double y2 = -1f * (l.a * x2 + l.c) / l.b;
                    return Arrays.asList(new P(x1, y1, Arrays.asList(l, c)), new P(x2, y2, Arrays.asList(l, c)));
                }
            }
        }
    }

    static List<P> CCP(C c1, C c2) {
        double R = d(new P(c1.x, c1.y, Arrays.asList(c1, c2)), new P(c2.x, c2.y, Arrays.asList(c1, c2)));
        if (R > c1.r + c2.r) {
            return new ArrayList<>();
        } else {
            double k2 = (c1.r * c1.r - c2.r * c2.r) / (2 * R * R);
            double k3 = 0.5f * Math.sqrt(2.0f * (c1.r * c1.r + c2.r * c2.r) / (R * R) - ((c1.r * c1.r - c2.r * c2.r) * (c1.r * c1.r - c2.r * c2.r)) / (R * R * R * R) - 1);
            if (Math.abs(k3) < Main.E) {
                double x1 = 0.5f * (c1.x + c2.x) + k2 * (c2.x - c1.x);
                double y1 = 0.5f * (c1.y + c2.y) + k2 * (c2.y - c1.y);
                return Arrays.asList(new P(x1, y1, Arrays.asList(c1, c2)));
            } else {
                double x1 = 0.5f * (c1.x + c2.x) + k2 * (c2.x - c1.x) + k3 * (c2.y - c1.y);
                double x2 = 0.5f * (c1.x + c2.x) + k2 * (c2.x - c1.x) - k3 * (c2.y - c1.y);
                double y1 = 0.5f * (c1.y + c2.y) + k2 * (c2.y - c1.y) + k3 * (c2.x - c1.x);
                double y2 = 0.5f * (c1.y + c2.y) + k2 * (c2.y - c1.y) - k3 * (c2.x - c1.x);
                return Arrays.asList(new P(x1, y1, Arrays.asList(c1, c2)), new P(x2, y2, Arrays.asList(c1, c2)));
            }
        }
    }

    static L CCL(C c1, C c2) {
        return PPL(new P(c1.x, c1.y, Arrays.asList(c1, c2)), new P(c2.x, c2.y, Arrays.asList(c1, c2)));
    }

}

abstract class E {
    public List<E> p;

    public abstract String toString();

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
    public int age;

    public P(double x, double y, List<E> p) {
        this.x = x;
        this.y = y;
        this.p = p;
    }

    @Override
    public String toString() {
        return String.format("P(%f,%f)", x, y);
    }

}

class L extends E {
    public double a;
    public double b;
    public double c;

    public L(double a, double b, double c, List<E> p) {
        if (Math.abs(a) < Main.E) {
            this.a = 0;
            this.b = 1;
            this.c = c / b;
        } else {
            this.a = 1;
            this.b = b / a;
            this.c = c / a;
        }

        this.p = p;
    }

    @Override
    public String toString() {
        return String.format("L(%f,%f,%f)", a, b, c);
    }

}

class C extends E {
    public double x;
    public double y;
    public double r;

    public C(double x, double y, double r, List<E> p) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.p = p;
    }

    @Override
    public String toString() {
        return String.format("C(%f,%f,%f)", x, y, r);
    }

}