package de.volkerfaas.kafka.deployment.controller.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;

public class SkipablePageRequest implements Pageable, Serializable {

    @Serial
    private static final long serialVersionUID = 1232825578694716873L;

    public static SkipablePageRequest unpaged() {
        return new SkipablePageRequest();
    }

    private final int skip;
    private final int page;
    private final int size;
    private final Sort sort;
    private final boolean paged;

    private SkipablePageRequest() {
        this.paged = false;
        this.skip = 0;
        this.page = 0;
        this.size = 0;
        this.sort = Sort.unsorted();
    }

    /**
     * Creates a new {@link SkipablePageRequest} with sort parameters applied.
     *
     * @param skip zero-based offset, must not be negative.
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    public SkipablePageRequest(int page, int size, int skip, Sort sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }
        Assert.notNull(sort, "Sort must not be null!");
        this.skip = skip;
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.paged = true;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return skip + ((long) page * (long) size);
    }

    public int getSkip() {
        return skip;
    }

    @Override
    @NonNull
    public Sort getSort() {
        return sort;
    }

    @Override
    @NonNull
    public Pageable next() {
        return new SkipablePageRequest(getPageNumber() + 1, getPageSize(), skip, getSort());
    }

    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return new SkipablePageRequest(0, getPageSize(), skip, getSort());
    }

    @Override
    public boolean hasPrevious() {
        return page > 0;
    }

    @Override
    public boolean isPaged() {
        return paged;
    }

    public Pageable previous() {
        return getPageNumber() == 0 ? this : new SkipablePageRequest(getPageNumber() - 1, getPageSize(), skip, getSort());
    }

}
