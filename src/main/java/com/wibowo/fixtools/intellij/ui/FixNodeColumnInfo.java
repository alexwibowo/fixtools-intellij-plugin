package com.wibowo.fixtools.intellij.ui;

import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class FixNodeColumnInfo<Aspect> extends ColumnInfo<FixNode, Aspect> {

    private final Function<FixNode, Aspect> extractor;

    public FixNodeColumnInfo(final String columnName,
                             final Function<FixNode, Aspect> extractor) {
        super(columnName);
        this.extractor = extractor;
    }

    @Override
    public @Nullable Aspect valueOf(final FixNode fixNode) {
        return extractor.apply(fixNode);
    }
}
