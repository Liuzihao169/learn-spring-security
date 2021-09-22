package com.imooc.uaa.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionUtil {
    public static TreeSet<? extends GrantedAuthority> toTreeSet(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().collect(
            Collectors.toCollection(
                () -> new TreeSet<>(
                    Comparator.comparing(GrantedAuthority::getAuthority)
                )
            )
        );
    }

    public static <T> Set<T> mergeSet(final Set<T> a, final Set<T> b) {

        // Creating a set with 'a'
        Set<T> mergedSet = new HashSet<>(a);

        // add the second set to be merged
        mergedSet.addAll(b);

        // returning the merged set
        return mergedSet;
    }

    public static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[]) obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>) obj);
        }
        return list;
    }
}
