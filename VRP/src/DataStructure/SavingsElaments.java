package DataStructure;

import java.util.ArrayList;

public class SavingsElaments {

    private ArrayList<Element> elements;

    public SavingsElaments() {
        this.elements = new ArrayList();
    }

    public int getFirstCi() {
        return this.elements.get(0).Ci;
    }

    public int getFirstCj() {
        return this.elements.get(0).Cj;
    }

    public void addSaving(int Ci, int Cj, double saving) {
        this.elements.add(new Element(Ci, Cj, saving));
    }

    public void removeFirst() {
        this.elements.remove(0);
    }

    public void sort() {
        this.elements.sort((o1, o2) -> {
            return (int)(o2.saving*100000-o1.saving*100000);
        });
    }

    public void printSavings() {
        for (Element element:this.elements) {
            System.out.println(element);
        }
    }

    private class Element {
        private int Ci;
        private int Cj;
        private double saving;

        public Element(int Ci, int Cj, double saving) {
            this.Ci = Ci;
            this.Cj = Cj;
            this.saving = saving;
        }

        @Override
        public String toString() {
            return "[" + Ci +
                    ", " + Cj +
                    " → " + saving +
                    ']';
        }
    }
}
