package nl.vpro.domain;

import java.util.*;
import java.util.stream.Collectors;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.util.ResortedSortedSet;
import nl.vpro.util.TriFunction;

/**
 * Utilities related to {@link TextualObject}s
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class TextualObjects {

    // some methods working on collections of 'OwnedText' objects (think of titles and descriptions)

    /**
     * Finds the first text in a collection of {@link TypedText}s with one of the given types. Wrapped in an optional, so never returns <code>null</code>
     */
    public static <OT extends TypedText> Optional<String> getOptional(Collection<OT> titles, TextualType... types) {
        return Optional.ofNullable(
            getObject(titles, types)
        ).map(TypedText::get);
    }


    public static <OT extends TypedText> String get(Collection<? extends OT> titles, String defaultValue, TextualType... types) {
        OT title = getObject(titles, types);
        return title == null ? defaultValue : title.get();
    }


    public static <OT extends OwnedText> SortedSet<OT> expand(
        Collection<OT> texts,
        TriFunction<String, OwnerType, TextualType, OT> creator,
        List<TextualType> types,
        List<OwnerType> owners) {
        SortedSet<OT> result = new TreeSet<>();
        result.addAll(texts);
        for(TextualType textualType : types) {
            for (OwnerType ownerType : owners) {
                expand(texts, textualType, ownerType).ifPresent(ot -> {
                    if (ot.getType() != textualType || ot.getOwner() != ownerType) {
                        result.add(creator.apply(ot.get(), ownerType, textualType));
                    }
                    }
                );
            }
        }
        return result;
    }

    public static <OT extends OwnedText> SortedSet<OT> expandTitles(
        Collection<OT> texts,
        TriFunction<String, OwnerType, TextualType, OT> creator) {
        return expand(texts,
            creator,
            Arrays.asList(TextualType.TITLES), Arrays.asList(OwnerType.ENTRIES));
    }

    public static <OT extends OwnedText> Optional<String> getOptional(Collection<OT> titles, OwnerType owner, TextualType type) {
        for (OT title : titles) {
            if (title.getOwner() == owner && title.getType() == type) {
                return Optional.of(title.get());
            }
        }
        return Optional.empty();
    }

    public static <OT extends OwnedText> Optional<String> get(Collection<OT> titles, Comparator<OwnerType> ownerType, TextualType... type) {
        return getObjects(titles, ownerType, type).stream().map(OwnedText::get).findFirst();
    }

    public static <OT extends TypedText> OT getObject(Collection<OT> titles, TextualType... types) {
        if (titles != null) {
            for (OT title : titles) {
                for (TextualType type : types) {
                    if (type == title.getType()) {
                        return title;
                    }
                }
            }
        }
        return null;
    }


    public static <OT extends OwnedText> Collection<OT> getObjects(Collection<? extends OT> titles, TextualType... types) {
        Comparator<OwnerType> comparator = Comparator.naturalOrder();
        return getObjects(titles, comparator, types);
    }


    protected static <OT extends OwnedText> Comparator<OT> getComparator(Comparator<OwnerType> ownerTypeComparator) {
        return (o1, o2) -> {
            int result = o1.getType().compareTo(o2.getType());
            if (result == 0) {
                return ownerTypeComparator.compare(o1.getOwner(), o2.getOwner());
            } else {
                return result;
            }
        };
    }

    public static <OT extends OwnedText> Collection<OT> getObjects(
        Collection<? extends OT> titles,
        Comparator<OwnerType> ownerTypeComparator,
        TextualType... types) {


        List<OT> returnValue = new ArrayList<>();
        if (titles == null){
            return returnValue;
        }
        Comparator<OT> comparator = getComparator(ownerTypeComparator);
        List<OT> list;
        if (titles instanceof List) {
            list = (List<OT>) titles;
        } else {
            list = new ArrayList<>();
            list.addAll(titles);
        }
        list.sort(comparator);
        for (OT title : list) {
            for (TextualType type : types) {
                if (type == title.getType()) {
                    returnValue.add(title);
                }
            }
        }
        return returnValue;
    }

    // some methods working TextualObjects themselves.

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>>  OwnerType[] findOwnersForTextFields(TO media) {
        SortedSet<OwnerType> result = new TreeSet<>();
        for (T title : media.getTitles()) {
            result.add(title.getOwner());
        }
        for (D description : media.getDescriptions()) {
            result.add(description.getOwner());
        }
        return result.toArray(new OwnerType[result.size()]);
    }


    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getTitle(TO media, OwnerType owner, TextualType type) {
        Optional<String> opt = getOptional(media.getTitles(), owner, type);
        return opt.orElse("");
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(TO media, OwnerType owner, TextualType type) {
        Optional<String> opt = getOptional(media.getDescriptions(), owner, type);
        return opt.orElse("");
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(TO media, TextualType... types) {
        Optional<String> opt = getOptional(media.getDescriptions(), types);
        return opt.orElse("");
    }


    /**
     * Sets the owner of all titles, descriptions, locations and images found in given MediaObject
     */
    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> void forOwner(TO media, OwnerType owner) {
        for (T title : media.getTitles()) {
            title.setOwner(owner);
        }
        for (D description : media.getDescriptions()) {
            description.setOwner(owner);
        }
    }

    public static <T extends Ownable> List<T> filter(Collection<T> ownables, OwnerType owner) {
        return ownables.stream().filter(item -> item.getOwner() == owner).collect(Collectors.toList());
    }


    public static <S> SortedSet<S> sorted(Set<S> set) {
        if (set == null) {
            return null;
        }
        if (set instanceof SortedSet) {
            //noinspection unchecked
            return (SortedSet) set;
        } else {
            return new ResortedSortedSet<>(set);
        }
    }


    public static <OT extends TypedText> String get(Collection<OT> titles, TextualType... work) {
        return getOptional(titles, work)
            .orElse(null);
    }

    /**
     * Returns the value for a certain {@link TextualType} and {@link OwnerType}. This implements a fall back mechanism.
     * It takes the first value with matching owner and type. If none found, it will fall back to the highest OwnerType ({@link OwnerType#BROADCASTER} and degrades until one is found.
     *
     * Furthermore if no 'LEXICO' typed values if found, the value for 'MAIN' will be returned.
     */
    public static <OT extends OwnedText> Optional<OT> expand(Collection<OT> titles, TextualType textualType, final OwnerType ownerType) {
        for (OT t : titles) {
            if (t.getType() == textualType && t.getOwner() == ownerType) {
                return Optional.of(t);
            }
        }
        OwnerType runningOwnerType = ownerType == OwnerType.first() ? OwnerType.down(OwnerType.first()) : OwnerType.first();
        while(runningOwnerType != OwnerType.last()) {
            for (OT t : titles) {
                if (t.getType() == textualType && t.getOwner() == runningOwnerType) {
                    return Optional.of(t);
                }
            }
            runningOwnerType = OwnerType.down(runningOwnerType);
        }
        if (textualType == TextualType.LEXICO) {
            return expand(titles, TextualType.MAIN, ownerType);
        }
        return Optional.empty();
    }

    public static <OT extends OwnedText> Optional<OT> expand(Collection<OT> titles, TextualType textualType) {
        return expand(titles, textualType, OwnerType.first());
    }

    /**
     * Give a collection, find the first object which equals the object we want to be in it.
     * If one found, the 'value' is copied to it.
     * If not, then the object is added to the collection.
     *
     * TypedText's are commonly stored in SortedSet's, where equals matches only owner and type, not the value itself.
     *
     * @param titles Collection
     * @param add the object to add or update.
     */
    public static <OT extends TypedText> boolean addOrUpdate(Collection<OT> titles, OT add) {
        Optional<OT> existing = titles.stream().filter(d -> d.equals(add)).findFirst();
        if (existing.isPresent()) {
            existing.get().set(add.get());
            return false;
        } else {
            titles.add(add);
            return true;
        }
    }
    /**
     * Copies all titles and descriptions from one {@link TextualObject} to another.
     * @since 5.3
     */
    public static <
        T1 extends OwnedText, D1 extends OwnedText, TO1 extends TextualObject<T1, D1, TO1>,
        T2 extends OwnedText,D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        > void copy(
            TO1 from,
            TO2 to) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.addTitle(title.get(), title.getOwner(), title.getType());
            }
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.addDescription(description.get(), description.getOwner(), description.getType());
            }
        }
    }

    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     * @param owner The owner of the fields in the destination
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        > void copy(
        TO1 from,
        TO2 to,
        OwnerType owner
    ) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.addTitle(title.get(), owner, title.getType());
            }
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.addDescription(description.get(), owner, description.getType());
            }
        }
    }

    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     * Then, remove all titles and descriptions (of the given owner) which were not in the source object.
     *
     * @param owner The owner of the fields in the destination
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        > void copyAndRemove(
        TO1 from,
        TO2 to,
        OwnerType owner) {
        copy(from, to, owner);
        retainAll(to.getTitles(), from.getTitles(), owner);
        retainAll(to.getDescriptions(), from.getDescriptions(), owner);
    }

    /**
     * From a collection of {@link OwnedText}'s remove all all elements with certain owner, which are not in the source collection of {@link TypedText}'s.
     * @param collection The collection to remove objects from
     * @param toRetain The collection of texts which are to be retained in collection
     * @since 5.3
     */
    public static <TO extends TypedText, TO2 extends OwnedText> void retainAll(
        Collection<TO2> collection,
        Collection<TO> toRetain,
        OwnerType owner) {
        if (toRetain != null) {
            collection.removeIf(t -> t.getOwner().equals(owner) && (!toRetain.contains(t)));
        }
    }

    /**
     * @param collection The collection to remove objects from
     * @param toRetain The collection of texts which are to be retained in collection
     * @since 5.3
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static <TO extends TypedText, TO2 extends TypedText> void retainAll(
        Collection<TO2> collection,
        Collection<TO> toRetain) {
        if (toRetain != null) {
            collection.removeIf(t -> (!toRetain.contains(t)));
        }
    }


    /**
     * Copy all texts from one collection of {@link TextualObjectUpdate} to another.
     * If the target collection is a {@link TextualObject} you want to use {@link #copy(TextualObjectUpdate, TextualObject, OwnerType)}
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends TypedText, D2 extends TypedText, TO2 extends TextualObjectUpdate<T2, D2, TO2>
        > void copyToUpdate(
        TO1 from,
        TO2 to) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.setTitle(title.get(), title.getType());
            }
        } else {
            to.setTitles(null);
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.setDescription(description.get(), description.getType());
            }
        } else {
            to.setDescriptions(null);
        }
    }
}
