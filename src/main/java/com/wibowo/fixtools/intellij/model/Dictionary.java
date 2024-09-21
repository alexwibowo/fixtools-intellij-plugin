package com.wibowo.fixtools.intellij.model;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Dictionary implements Comparable<Dictionary> {
    public String alias;
    public String path;

    public Dictionary() {
    }

    public Dictionary(final String alias,
                      final String path) {
        this.alias = alias;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dictionary that = (Dictionary) o;
        return Objects.equals(alias, that.alias) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, path);
    }

    @Override
    public int compareTo(@NotNull Dictionary o) {
        return new CompareToBuilder()
                .append(this.alias, o.alias)
                .append(this.path, o.path)
                .toComparison();
    }

    @Override
    public String toString() {
        return "Dictionary{" +
                "alias='" + alias + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
