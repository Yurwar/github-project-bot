package edu.kpi.convertor;

public interface Convertor<F, T> {
    T convert(F fromObject);
}
