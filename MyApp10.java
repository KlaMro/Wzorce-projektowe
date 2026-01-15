interface Diameter {
    double baseDiagonal();
    double spaceDiagonal();
}

abstract class Bin {
    public abstract double surfaceArea();
    public abstract double volume();
}

class Bin2 extends Bin implements Diameter {
    private double a;
    private double b;
    private double h;

    public Bin2(double a, double b, double h) {
        this.a = a;
        this.b = b;
        this.h = h;
    }

    public double surfaceArea() {
        return 2 * (a * b + a * h + b * h);
    }

    public double volume() {
        return a * b * h;
    }

    public double baseDiagonal() {
        return Math.sqrt(a * a + b * b);
    }

    public double spaceDiagonal() {
        return Math.sqrt(a * a + b * b + h * h);
    }
}

class Cube extends Bin implements Diameter {
    private double a;

    public Cube(double a) {
        this.a = a;
    }

    public double surfaceArea() {
        return 6 * a * a;
    }

    public double volume() {
        return a * a * a;
    }

    public double baseDiagonal() {
        return a * Math.sqrt(2);
    }

    public double spaceDiagonal() {
        return a * Math.sqrt(3);
    }
}

public class MyApp10 {
    public static void main(String[] args) {
        Bin2 graniastoslup = new Bin2(3, 4, 5);
        Cube szescian = new Cube(4);

        System.out.println("GRANIASTOSŁUP PROSTOKĄTNY");
        System.out.println("Wymiary: a = 3, b = 4, h = 5");
        System.out.println("Pole powierzchni: " + graniastoslup.surfaceArea());
        System.out.println("Objętość: " + graniastoslup.volume());
        System.out.println("Przekątna podstawy: " + graniastoslup.baseDiagonal());
        System.out.println("Przekątna bryły: " + graniastoslup.spaceDiagonal());

        System.out.println();
        System.out.println("SZEŚCIAN");
        System.out.println("Wymiar krawędzi: a = 4");
        System.out.println("Pole powierzchni: " + szescian.surfaceArea());
        System.out.println("Objętość: " + szescian.volume());
        System.out.println("Przekątna podstawy: " + szescian.baseDiagonal());
        System.out.println("Przekątna bryły: " + szescian.spaceDiagonal());
    }
}

