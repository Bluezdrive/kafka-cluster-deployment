package de.volkerfaas.kafka.deployment.controller.model.resolver;

import de.volkerfaas.kafka.deployment.controller.model.SkipablePageRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class SkipablePageRequestHandlerMethodArgumentResolver implements PageableArgumentResolver {

    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private final SortArgumentResolver sortResolver;

    public SkipablePageRequestHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
        this.sortResolver = sortResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return SkipablePageRequest.class.equals(parameter.getParameterType());
    }

    @Override
    @NonNull
    public Pageable resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        final String pageParameterValue = webRequest.getParameter(getParameterNameToUse("page", parameter));
        final String sizeParameterValue = webRequest.getParameter(getParameterNameToUse("size", parameter));
        if (Objects.isNull(pageParameterValue) || Objects.isNull(sizeParameterValue)) {
            return SkipablePageRequest.unpaged();
        }
        final String skipParameterValue = webRequest.getParameter(getParameterNameToUse("skip", parameter));
        final int page = intValueOf(pageParameterValue, 0);
        final int size = intValueOf(sizeParameterValue, 20);
        final int skip = intValueOf(skipParameterValue, 0);
        final Sort sort = sortResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (sort.isSorted()) {
            return new SkipablePageRequest(page, size, skip, sort);
        }

        return new SkipablePageRequest(page, size, skip, Sort.unsorted());
    }

    private String getParameterNameToUse(String source, @Nullable MethodParameter parameter) {
        final StringBuilder builder = new StringBuilder(DEFAULT_PREFIX);
        final Qualifier qualifier = Objects.isNull(parameter) ? null : parameter.getParameterAnnotation(Qualifier.class);
        if (Objects.nonNull(qualifier)) {
            builder.append(qualifier.value());
            builder.append(DEFAULT_QUALIFIER_DELIMITER);
        }

        return builder.append(source).toString();
    }

    private int intValueOf(String parameterValue, int defaultValue) {
        if (Objects.isNull(parameterValue)) {
            return defaultValue;
        }

        return Integer.parseInt(parameterValue);
    }


}
