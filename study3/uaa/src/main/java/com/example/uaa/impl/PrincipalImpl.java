package com.example.uaa.impl;

import java.security.Principal;
import java.util.Objects;

public class PrincipalImpl implements Principal {

    private final String name;

    public PrincipalImpl(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != getClass()) {
            return false;
        }
        final PrincipalImpl other = (PrincipalImpl) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
