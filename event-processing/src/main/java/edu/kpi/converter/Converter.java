package edu.kpi.converter;

public interface Converter<F, T> {
    T convert(F fromObject);
}
