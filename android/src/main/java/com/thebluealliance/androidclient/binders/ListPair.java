package com.thebluealliance.androidclient.binders;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A List class that takes two sub-lists and can flip between them
 * It default
 * @param <T>
 */
public class ListPair<T> implements List<T> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LIST0, LIST1})
    public @interface ListOption {}
    public static final int LIST0 = 0;
    public static final int LIST1 = 1;


    private List<T> mList0, mList1;
    private @ListOption int mSelectedList;

    public ListPair(List<T> list0, List<T> list1) {
        mList0 = list0;
        mList1 = list1;
        mSelectedList = LIST0;
    }

    public @ListOption int getSelectedList() {
        return mSelectedList;
    }

    public void setSelectedList(@ListOption int selectedList) {
        mSelectedList = selectedList;
    }

    public void clearBoth() {
        mList0.clear();
        mList1.clear();
    }

    public void setCurrentListData(List<T> newData) {
        if (mSelectedList == LIST0) {
            mList0 = newData;
        } else {
            mList1 = newData;
        }
    }

    @Override
    public void add(int location, T object) {
        getActiveList().add(location, object);
    }

    @Override
    public boolean add(T object) {
        return getActiveList().add(object);
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends T> collection) {
        return getActiveList().addAll(location, collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return getActiveList().addAll(collection);
    }

    @Override
    public void clear() {
        getActiveList().clear();
    }

    @Override
    public boolean contains(Object object) {
        return getActiveList().contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return getActiveList().containsAll(collection);
    }

    @Override
    public T get(int location) {
        return getActiveList().get(location);
    }

    @Override
    public int indexOf(Object object) {
        return getActiveList().indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return getActiveList().isEmpty();
    }

    @NonNull @Override
    public Iterator<T> iterator() {
        return getActiveList().iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return getActiveList().lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getActiveList().listIterator();
    }

    @NonNull @Override
    public ListIterator<T> listIterator(int location) {
        return getActiveList().listIterator(location);
    }

    @Override
    public T remove(int location) {
        return getActiveList().remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return getActiveList().remove(object);
    }

    @Override
    public boolean removeAll(@NonNull  Collection<?> collection) {
        return getActiveList().removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return getActiveList().retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return getActiveList().set(location, object);
    }

    @Override
    public int size() {
        return getActiveList().size();
    }

    @NonNull @Override
    public List<T> subList(int start, int end) {
        return getActiveList().subList(start, end);
    }

    @NonNull @Override
    public Object[] toArray() {
        return getActiveList().toArray();
    }

    @NonNull @Override
    public <T1> T1[] toArray(@NonNull  T1[] array) {
        return getActiveList().toArray(array);
    }

    @Override
    public String toString() {
        return getActiveList().toString();
    }

    @Override
    public int hashCode() {
        return getActiveList().hashCode();
    }

    private List<T> getActiveList() {
        return mSelectedList == LIST0
                ? mList0
                : mList1;
    }
}
