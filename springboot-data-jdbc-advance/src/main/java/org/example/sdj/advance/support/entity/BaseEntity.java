package org.example.sdj.advance.support.entity;

import java.io.Serializable;

public class BaseEntity implements Serializable {

    public Object getPKValue() {
        return EntityUtil.getPKID( this);
    }

    @Override
    public int hashCode() {
        Object pkval = getPKValue();
        return pkval != null ? pkval.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
