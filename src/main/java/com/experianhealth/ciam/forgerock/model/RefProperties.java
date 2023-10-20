package com.experianhealth.ciam.forgerock.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefProperties {
    private String _id;
    private String _rev;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RefProperties.class.getSimpleName() + "[", "]")
                .add("_id='" + _id + "'")
                .add("_rev='" + _rev + "'")
                .toString();
    }
}