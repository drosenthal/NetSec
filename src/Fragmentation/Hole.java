package Fragmentation;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
public class Hole implements Comparable<Hole>
{
    public int first;
    public int last;
    public Hole(int firstHole, int lastHole)
    {
        first = firstHole;
        last = lastHole;
    }

    @Override
    public int compareTo(Hole o)
    {
        return (this.last - o.last);
    }
}
