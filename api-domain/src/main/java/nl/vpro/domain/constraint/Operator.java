package nl.vpro.domain.constraint;

import javax.xml.bind.annotation.XmlType;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@XmlType(name = "operatorType")
public enum Operator {
    LT,
    GT,
    EQ,
    LTE,
    GTE
}
