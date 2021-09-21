package online.nasgar.skyblockcore.api.loader;

public interface Loader<T, U> {

    T load(U params);

}