package util;

/**
 * Created: 19.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

@FunctionalInterface
public interface Function2Args<T, U, R> {
 public R apply(T t, U u) ;
}
