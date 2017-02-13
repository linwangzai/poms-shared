package nl.vpro.domain;

import java.util.*;
import java.util.stream.Collectors;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.util.ResortedSortedSet;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class TextualObjects {

    // some methods working on collections of 'OwnedText' objects (think of titles and descriptions)

    public static <OT extends OwnedText<OT>> Optional<String> getOptional(Collection<OT> titles, TextualType... types) {
        return Optional.ofNullable(getObject(titles, types)).map(OwnedText::get);
    }

    public static <OT extends OwnedText<OT>> String get(Collection<? extends OT> titles, String defaultValue, TextualType... types) {
        OT title = getObject(titles, types);
        return title == null ? defaultValue : title.get();
    }

    public static <OT extends OwnedText<OT>> String get(Collection<? extends OT> titles, TextualType... types) {
        return get(titles, "", types);
    }


    public static <OT extends OwnedText<OT>> Optional<String> getOptional(Collection<OT> titles, OwnerType owner, TextualType type) {
        for (OT title : titles) {
            if (title.getOwner() == owner && title.getType() == type) {
                return Optional.of(title.get());
            }
        }
        return Optional.empty();
    }

    public static <OT extends OwnedText<OT>> Optional<String> get(Collection<OT> titles, Comparator<OwnerType> ownerType, TextualType... type) {
        return getObjects(titles, ownerType, type).stream().map(OwnedText::get).findFirst();
    }

    public static <OT extends OwnedText> OT getObject(Collection<OT> titles, TextualType... types) {
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


    public static <OT extends OwnedText<OT>> Collection<OT> getObjects(Collection<? extends OT> titles, TextualType... types) {
        Comparator<OwnerType> comparator = Comparator.naturalOrder();
        return getObjects(titles, comparator, types);
    }


    protected static <OT extends OwnedText<OT>> Comparator<OT> getComparator(Comparator<OwnerType> ownerTypeComparator) {
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
            list = new ArrayList<OT>();
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
            return new ResortedSortedSet<S>(set);
        }
    }


}
