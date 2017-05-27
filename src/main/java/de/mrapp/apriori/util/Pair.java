package de.mrapp.apriori.util;

// TODO: Comments & copyright
public class Pair<F, S> {

    public final F first;

    public final S second;

    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> Pair<F, S> create(final F first, final S second) {
        return new Pair<>(first, second);
    }


    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }

}