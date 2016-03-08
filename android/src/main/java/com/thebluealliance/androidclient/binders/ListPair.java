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
        if (mSelectedList == LIST0) {
            mList0.add(location, object);
        } else {
            mList1.add(location, object);
        }
    }

    @Override
    public boolean add(T object) {
        return mSelectedList == LIST0
                ? mList0.add(object)
                : mList1.add(object);
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends T> collection) {
        return mSelectedList == LIST0
                ? mList0.addAll(location, collection)
                : mList1.addAll(location, collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return mSelectedList == LIST0
                ? mList0.addAll(collection)
                : mList1.addAll(collection);
    }

    @Override
    public void clear() {
        if (mSelectedList == LIST0) {
            mList0.clear();
        } else {
            mList1.clear();
        }
    }

    @Override
    public boolean contains(Object object) {
        return mSelectedList == LIST0
                ? mList0.contains(object)
                : mList1.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mSelectedList == LIST0
                ? mList0.containsAll(collection)
                : mList1.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return mSelectedList == LIST0
                ? mList0.get(location)
                : mList1.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mSelectedList == LIST0
                ? mList0.indexOf(object)
                : mList1.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mSelectedList == LIST0
                ? mList0.isEmpty()
                : mList1.isEmpty();
    }

    @NonNull @Override
    public Iterator<T> iterator() {
        return mSelectedList == LIST0
                ? mList0.iterator()
                : mList1.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return mSelectedList == LIST0
                ? mList0.lastIndexOf(object)
                : mList1.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return mSelectedList == LIST0
                ? mList0.listIterator()
                : mList1.listIterator();
    }

    @NonNull @Override
    public ListIterator<T> listIterator(int location) {
        return mSelectedList == LIST0
                ? mList0.listIterator(location)
                : mList1.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return mSelectedList == LIST0
                ? mList0.remove(location)
                : mList1.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return mSelectedList == LIST0
                ? mList0.remove(object)
                : mList1.remove(object);
    }

    @Override
    public boolean removeAll(@NonNull  Collection<?> collection) {
        return mSelectedList == LIST0
                ? mList0.removeAll(collection)
                : mList1.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return mSelectedList == LIST0
                ? mList0.retainAll(collection)
                : mList1.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return mSelectedList == LIST0
                ? mList0.set(location, object)
                : mList1.set(location, object);
    }

    @Override
    public int size() {
        return mSelectedList == LIST0
                ? mList0.size()
                : mList1.size();
    }

    @NonNull @Override
    public List<T> subList(int start, int end) {
        return mSelectedList == LIST0
                ? mList0.subList(start, end)
                : mList1.subList(start, end);
    }

    @NonNull @Override
    public Object[] toArray() {
        return mSelectedList == LIST0
                ? mList0.toArray()
                : mList1.toArray();
    }

    @NonNull @Override
    public <T1> T1[] toArray(@NonNull  T1[] array) {
        return mSelectedList == LIST0
                ? mList0.toArray(array)
                : mList1.toArray(array);
    }

    @Override
    public String toString() {
        return mSelectedList == LIST0
                ? mList0.toString()
                : mList1.toString();
    }

    @Override
    public int hashCode() {
        return mSelectedList == LIST0
                ? mList0.hashCode()
                : mList1.hashCode();
    }
}
